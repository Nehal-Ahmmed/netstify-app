package com.nhbhuiyan.nestify.domain.manager

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.nhbhuiyan.nestify.data.datastore.SettingDatastore
import com.nhbhuiyan.nestify.domain.model.UserRole
import com.nhbhuiyan.nestify.domain.model.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val settingDatastore: SettingDatastore
) {
    private val TAG = "UserSessionManager"
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _sessionFlow = MutableStateFlow<UserSession?>(null)
    val sessionFlow: StateFlow<UserSession?> = _sessionFlow.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        // Recover session from cache if FirebaseAuth is already signed in
        scope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val cachedRole = settingDatastore.userRole.first()
                val cachedGroupId = settingDatastore.classGroupId.first()
                val cachedDept = settingDatastore.deptCode.first()
                val cachedStudentId = settingDatastore.studentId.first()

                _sessionFlow.value = UserSession(
                    uid = currentUser.uid,
                    email = currentUser.email ?: "",
                    role = UserRole.fromName(cachedRole),
                    classGroupId = cachedGroupId,
                    departmentCode = cachedDept,
                    rollNumber = cachedStudentId.takeLast(3),
                    displayName = currentUser.displayName ?: "",
                    photoUrl = currentUser.photoUrl?.toString() ?: "",
                    linkedEmails = emptyList(),
                    providers = emptyList()
                )
                // Start listening for live changes
                bind(currentUser.uid)
            }
        }
    }

    /**
     * Starts listening to user document updates in Firestore.
     */
    fun bind(uid: String) {
        synchronized(this) {
            listenerRegistration?.remove()
            
            Log.d(TAG, "Binding session listener for uid: $uid")
            listenerRegistration = firestore.collection("users").document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error listening to user doc: ${error.message}", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        updateSessionFromDoc(snapshot)
                    }
                }
        }
    }

    /**
     * Clears local states, cache, and cancels snapshot listener.
     */
    fun unbind() {
        synchronized(this) {
            Log.d(TAG, "Unbinding session listener")
            listenerRegistration?.remove()
            listenerRegistration = null
            _sessionFlow.value = null

            scope.launch {
                settingDatastore.clearUserSession()
            }
        }
    }

    /**
     * Forces Firebase Auth to refresh token and update custom claims.
     */
    suspend fun forceRefreshClaims() {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                Log.d(TAG, "Forcing claims refresh")
                currentUser.getIdToken(true).await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error forcing claims refresh: ${e.message}", e)
        }
    }

    private fun updateSessionFromDoc(snapshot: DocumentSnapshot) {
        Log.d(TAG, "Snapshot received for user doc: exists=${snapshot.exists()}, path=${snapshot.reference.path}")
        val uid = snapshot.getString("uid") ?: return
        val email = snapshot.getString("email") ?: ""
        val roleStr = snapshot.getString("role") ?: "student"
        val classGroupId = snapshot.getString("classGroupId") ?: ""
        val deptCode = snapshot.getString("departmentCode") ?: ""
        val roll = snapshot.getString("rollNumber") ?: ""
        val displayName = snapshot.getString("displayName") ?: ""
        val photoUrl = snapshot.getString("photoUrl") ?: ""
        Log.d(TAG, "Parsed user doc snapshot: uid=$uid, email=$email, role=$roleStr, classGroupId=$classGroupId, roll=$roll")

        @Suppress("UNCHECKED_CAST")
        val linkedEmails = (snapshot.get("linkedEmails") as? List<String>) ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val providers = (snapshot.get("providers") as? List<String>) ?: emptyList()

        val role = UserRole.fromName(roleStr)

        val updatedSession = UserSession(
            uid = uid,
            email = email,
            role = role,
            classGroupId = classGroupId,
            departmentCode = deptCode,
            rollNumber = roll,
            displayName = displayName,
            photoUrl = photoUrl,
            linkedEmails = linkedEmails,
            providers = providers
        )

        // Detect if role changed on the user doc listener and force claims refresh
        val oldSession = _sessionFlow.value
        if (oldSession != null && oldSession.role != role) {
            Log.i(TAG, "User role changed from ${oldSession.role} to $role. Forcing claims token refresh.")
            scope.launch {
                forceRefreshClaims()
            }
        }

        _sessionFlow.value = updatedSession

        // Update local settings datastore cache
        scope.launch {
            settingDatastore.setUserSession(
                role = roleStr,
                classGroupId = classGroupId,
                studentId = snapshot.getString("studentId") ?: "",
                deptCode = deptCode
            )
        }
    }
}
