package com.nhbhuiyan.nestify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhbhuiyan.nestify.domain.model.AcademicUser
import com.nhbhuiyan.nestify.domain.model.UserRole
import com.nhbhuiyan.nestify.domain.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun getAcademicUser(uid: String): Flow<AcademicUser?> = callbackFlow {
        val docRef = firestore.collection("users").document(uid)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val user = AcademicUser(
                    uid = snapshot.getString("uid") ?: "",
                    email = snapshot.getString("email") ?: "",
                    role = UserRole.fromName(snapshot.getString("role")),
                    classGroupId = snapshot.getString("classGroupId") ?: "",
                    departmentCode = snapshot.getString("departmentCode") ?: "",
                    rollNumber = snapshot.getString("rollNumber") ?: "",
                    displayName = snapshot.getString("displayName") ?: "",
                    photoUrl = snapshot.getString("photoUrl") ?: "",
                    studentId = snapshot.getString("studentId") ?: "",
                    batchYear = snapshot.getString("batchYear") ?: "",
                    pendingReview = snapshot.getBoolean("pendingReview") ?: false
                )
                trySend(user)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateAcademicUser(uid: String, displayName: String, photoUrl: String): Result<Unit> {
        return try {
            firestore.collection("users").document(uid)
                .update(
                    mapOf(
                        "displayName" to displayName,
                        "photoUrl" to photoUrl
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAcademicUserRole(uid: String, role: String): Result<Unit> {
        return try {
            firestore.collection("users").document(uid)
                .update("role", role)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
