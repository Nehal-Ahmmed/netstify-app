package com.nhbhuiyan.nestify.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.nhbhuiyan.nestify.domain.manager.UserSessionManager
import android.util.Log
import com.nhbhuiyan.nestify.domain.repository.AuthRepository
import com.nhbhuiyan.nestify.data.sync.SyncManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

import com.google.firebase.firestore.FirebaseFirestore

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sessionManager: UserSessionManager,
    private val syncManager: SyncManager
) : AuthRepository {

    private val TAG = "AuthRepository"

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        Log.d(TAG, "Attempting email sign-in for: $email")
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Auth user is null")
            Log.d(TAG, "✅ Email sign-in successful for uid: ${user.uid}")
            sessionManager.bind(user.uid)
            
            // Restore cloud backup on sign-in
            try {
                syncManager.performRestore()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to restore backup on sign-in: ${e.message}")
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Email sign-in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> {
        Log.d(TAG, "Attempting email sign-up for: $email")
        return try {
            val identity = com.nhbhuiyan.nestify.domain.model.StudentIdentity.parse(email)
            if (identity == null) {
                throw IllegalArgumentException("Only CUET student email addresses (@student.cuet.ac.bd) are allowed to register.")
            }

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Auth user is null")
            
            // Client-side Spark plan replacement for onUserCreate
            val userPayload = mapOf(
                "uid" to user.uid,
                "email" to email,
                "displayName" to (user.displayName ?: email.substringBefore("@")),
                "role" to "student",
                "classGroupId" to identity.classGroupId,
                "departmentCode" to identity.departmentCode,
                "rollNumber" to identity.rollNumber,
                "studentId" to identity.studentId,
                "batchYear" to identity.batchYear,
                "photoUrl" to "",
                "pendingReview" to false,
                "providers" to listOf("password"),
                "linkedEmails" to emptyList<String>()
            )
            firestore.collection("users").document(user.uid).set(userPayload).await()

            firestore.collection("classGroups").document(identity.classGroupId)
                .collection("roster").document(user.uid)
                .set(
                    mapOf(
                        "uid" to user.uid,
                        "displayName" to (user.displayName ?: email.substringBefore("@")),
                        "rollNumber" to identity.rollNumber,
                        "role" to "student"
                    )
                ).await()
            Log.d(TAG, "✅ Roster binding created for ${identity.classGroupId}")

            sessionManager.bind(user.uid)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Email sign-up failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        Log.d(TAG, "Attempting Google sign-in")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: throw Exception("Google Auth user is null")
            
            // Verify if user doc exists, else create it client-side (first-time sign-in)
            val userDoc = firestore.collection("users").document(user.uid).get().await()
            if (!userDoc.exists()) {
                Log.d(TAG, "First-time Google sign-in, creating user doc for uid: ${user.uid}")
                val email = user.email ?: ""
                val identity = com.nhbhuiyan.nestify.domain.model.StudentIdentity.parse(email)
                if (identity == null) {
                    // Reject registration with non-institutional Google account
                    user.delete().await()
                    auth.signOut()
                    throw IllegalArgumentException("Account not found. Please register with your CUET student email first, then link your Google account in Settings.")
                }
                val userPayload = mapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "displayName" to (user.displayName ?: email.substringBefore("@")),
                    "role" to "student",
                    "classGroupId" to identity.classGroupId,
                    "departmentCode" to identity.departmentCode,
                    "rollNumber" to identity.rollNumber,
                    "studentId" to identity.studentId,
                    "batchYear" to identity.batchYear,
                    "photoUrl" to "",
                    "pendingReview" to false,
                    "providers" to listOf("google.com"),
                    "linkedEmails" to emptyList<String>()
                )
                firestore.collection("users").document(user.uid).set(userPayload).await()

                firestore.collection("classGroups").document(identity.classGroupId)
                    .collection("roster").document(user.uid)
                    .set(
                        mapOf(
                            "uid" to user.uid,
                            "displayName" to (user.displayName ?: email.substringBefore("@")),
                            "rollNumber" to identity.rollNumber,
                            "role" to "student"
                        )
                    ).await()
                Log.d(TAG, "✅ Roster binding created for ${identity.classGroupId}")
            } else {
                Log.d(TAG, "User doc already exists for uid: ${user.uid}")
            }

            sessionManager.bind(user.uid)
            
            // Restore cloud backup on sign-in
            try {
                syncManager.performRestore()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to restore backup on sign-in: ${e.message}")
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Google sign-in failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun linkGoogleAccount(idToken: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("No user signed in"))
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = user.linkWithCredential(credential).await()
            val linkedUser = result.user ?: throw Exception("Failed to link Google account")

            // Update Firestore user document with new providers and linked email
            val providers = linkedUser.providerData.map { it.providerId }
            val linkedEmails = linkedUser.providerData.mapNotNull { it.email }.filter { it != linkedUser.email }

            firestore.collection("users").document(linkedUser.uid).update(
                mapOf(
                    "providers" to providers,
                    "linkedEmails" to linkedEmails
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlinkGoogleAccount(): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("No user signed in"))
        return try {
            user.unlink(GoogleAuthProvider.PROVIDER_ID).await()

            // Update Firestore user document
            val providers = user.providerData.map { it.providerId }
            val linkedEmails = user.providerData.mapNotNull { it.email }.filter { it != user.email }

            firestore.collection("users").document(user.uid).update(
                mapOf(
                    "providers" to providers,
                    "linkedEmails" to linkedEmails
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        Log.d(TAG, "Signing out user: ${auth.currentUser?.uid}")
        try {
            syncManager.clearLocalData()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing database on sign out: ${e.message}")
        }
        auth.signOut()
        sessionManager.unbind()
    }
}
