package com.nhbhuiyan.nestify.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.util.Log
import com.nhbhuiyan.nestify.domain.model.MergeRequest
import com.nhbhuiyan.nestify.domain.repository.MergeRequestRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MergeRequestRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MergeRequestRepository {

    private val TAG = "MergeRequestRepo"

    override suspend fun submitMergeRequest(groupId: String, mr: MergeRequest): Result<Unit> {
        Log.d(TAG, "Submitting MR ${mr.id} (type=${mr.type}) to group $groupId")
        return try {
            val docRef = firestore.collection("classGroups").document(groupId)
                .collection("mergeRequests").document(mr.id)

            val payload = mapOf(
                "type" to mr.type,
                "target" to mr.target,
                "semesterId" to mr.semesterId,
                "departmentCode" to mr.departmentCode,
                "data" to mr.data,
                "submittedBy" to mr.submittedBy,
                "submitterName" to mr.submitterName,
                "status" to mr.status,
                "submittedAt" to FieldValue.serverTimestamp()
            )

            docRef.set(payload).await()
            Log.d(TAG, "✅ MR ${mr.id} submitted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to submit MR ${mr.id}: ${e.message}", e)
            Result.failure(e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getMergeRequests(groupId: String): Flow<List<MergeRequest>> = callbackFlow {
        val query = firestore.collection("classGroups").document(groupId)
            .collection("mergeRequests")
            .orderBy("submittedAt", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.map { doc ->
                    MergeRequest(
                        id = doc.id,
                        type = doc.getString("type") ?: "",
                        target = doc.getString("target") ?: "",
                        semesterId = doc.getString("semesterId") ?: "",
                        departmentCode = doc.getString("departmentCode") ?: "",
                        data = (doc.get("data") as? Map<String, Any?>) ?: emptyMap(),
                        submittedBy = doc.getString("submittedBy") ?: "",
                        submitterName = doc.getString("submitterName") ?: "",
                        status = doc.getString("status") ?: "pending",
                        reviewedBy = doc.getString("reviewedBy"),
                        reviewNote = doc.getString("reviewNote"),
                        submittedAt = (doc.get("submittedAt") as? Timestamp)?.seconds?.times(1000),
                        reviewedAt = (doc.get("reviewedAt") as? Timestamp)?.seconds?.times(1000)
                    )
                }
                trySend(list)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun resolveMergeRequest(
        groupId: String,
        mrId: String,
        status: String,
        reviewerUid: String,
        reviewNote: String?
    ): Result<Unit> {
        return try {
            firestore.collection("classGroups").document(groupId)
                .collection("mergeRequests").document(mrId)
                .update(
                    mapOf(
                        "status" to status,
                        "reviewedBy" to reviewerUid,
                        "reviewNote" to reviewNote,
                        "reviewedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            Log.d(TAG, "✅ MR $mrId resolved to '$status'")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to resolve MR $mrId: ${e.message}", e)
            Result.failure(e)
        }
    }
}
