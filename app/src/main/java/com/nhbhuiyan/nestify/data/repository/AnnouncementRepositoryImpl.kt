package com.nhbhuiyan.nestify.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nhbhuiyan.nestify.domain.model.Announcement
import com.nhbhuiyan.nestify.domain.repository.AnnouncementRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnnouncementRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AnnouncementRepository {

    override fun getAnnouncements(groupId: String): Flow<List<Announcement>> = callbackFlow {
        val query = firestore.collection("classGroups").document(groupId)
            .collection("announcements")
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.map { doc ->
                    Announcement(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        body = doc.getString("body") ?: "",
                        createdBy = doc.getString("createdBy") ?: "",
                        createdAt = (doc.get("createdAt") as? Timestamp)?.seconds?.times(1000),
                        priority = doc.getString("priority") ?: "low"
                    )
                }
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun postAnnouncement(groupId: String, announcement: Announcement): Result<Unit> {
        return try {
            val docRef = firestore.collection("classGroups").document(groupId)
                .collection("announcements").document(announcement.id)

            val payload = mapOf(
                "title" to announcement.title,
                "body" to announcement.body,
                "createdBy" to announcement.createdBy,
                "priority" to announcement.priority,
                "createdAt" to FieldValue.serverTimestamp()
            )

            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
