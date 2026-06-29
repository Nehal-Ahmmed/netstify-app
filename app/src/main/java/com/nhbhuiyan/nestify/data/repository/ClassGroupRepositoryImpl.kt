package com.nhbhuiyan.nestify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhbhuiyan.nestify.domain.model.ClassGroup
import com.nhbhuiyan.nestify.domain.model.ClassGroupRosterItem
import com.nhbhuiyan.nestify.domain.repository.ClassGroupRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassGroupRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ClassGroupRepository {

    @Suppress("UNCHECKED_CAST")
    override fun getClassGroup(groupId: String): Flow<ClassGroup?> = callbackFlow {
        val docRef = firestore.collection("classGroups").document(groupId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val group = ClassGroup(
                    groupId = groupId,
                    currentLevel = snapshot.getLong("currentLevel")?.toInt() ?: 1,
                    currentTerm = snapshot.getLong("currentTerm")?.toInt() ?: 1,
                    crList = (snapshot.get("crList") as? List<String>) ?: emptyList(),
                    adminList = (snapshot.get("adminList") as? List<String>) ?: emptyList()
                )
                trySend(group)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    override fun getClassRoster(groupId: String): Flow<List<ClassGroupRosterItem>> = callbackFlow {
        val collectionRef = firestore.collection("classGroups").document(groupId).collection("roster")
        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val rosterList = snapshot.documents.map { doc ->
                    ClassGroupRosterItem(
                        rollNumber = doc.id,
                        uid = doc.getString("uid") ?: "",
                        displayName = doc.getString("displayName") ?: ""
                    )
                }
                trySend(rosterList)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateClassSettings(groupId: String, level: Int, term: Int): Result<Unit> {
        return try {
            firestore.collection("classGroups").document(groupId)
                .update(
                    mapOf(
                        "currentLevel" to level,
                        "currentTerm" to term
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
