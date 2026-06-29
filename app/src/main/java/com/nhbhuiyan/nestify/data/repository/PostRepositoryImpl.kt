package com.nhbhuiyan.nestify.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nhbhuiyan.nestify.domain.model.Post
import com.nhbhuiyan.nestify.domain.repository.PostRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore-backed [PostRepository], mirroring [AnnouncementRepositoryImpl]:
 * one `callbackFlow` snapshot listener for reads, `doc.set(payload)` with a server
 * timestamp for writes. Like toggling runs in a transaction so `likeCount` never drifts
 * from `likedBy`. No Cloud Functions — every mutation is a Spark-plan client write.
 */
@Singleton
class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {

    private fun postsCollection(groupId: String) =
        firestore.collection("classGroups").document(groupId).collection("posts")

    override fun getPosts(groupId: String, typeFilter: String?): Flow<List<Post>> = callbackFlow {
        // Single listener, newest first, capped to a page limit (cost guardrail). The optional
        // type filter is applied client-side so the feed keeps ONE listener across filter changes
        // and avoids a composite Firestore index.
        val query = postsCollection(groupId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(FEED_PAGE_LIMIT)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.map { doc ->
                Post(
                    id = doc.id,
                    authorUid = doc.getString("authorUid") ?: "",
                    authorName = doc.getString("authorName") ?: "",
                    authorMeta = doc.getString("authorMeta") ?: "",
                    type = doc.getString("type") ?: "",
                    title = doc.getString("title") ?: "",
                    body = doc.getString("body") ?: "",
                    tags = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    createdAt = (doc.get("createdAt") as? Timestamp)?.seconds?.times(1000),
                    likeCount = (doc.getLong("likeCount") ?: 0L).toInt(),
                    commentCount = (doc.getLong("commentCount") ?: 0L).toInt(),
                    likedBy = (doc.get("likedBy") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                )
            } ?: emptyList()

            val filtered = if (typeFilter.isNullOrBlank()) list else list.filter { it.type == typeFilter }
            trySend(filtered)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createPost(groupId: String, post: Post): Result<Unit> {
        return try {
            val docRef = postsCollection(groupId).document(post.id)
            val payload = mapOf(
                "authorUid" to post.authorUid,
                "authorName" to post.authorName,
                "authorMeta" to post.authorMeta,
                "type" to post.type,
                "title" to post.title,
                "body" to post.body,
                "tags" to post.tags,
                "likeCount" to 0L,
                "commentCount" to 0L,
                "likedBy" to emptyList<String>(),
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleLike(groupId: String, postId: String, uid: String): Result<Unit> {
        return try {
            val docRef = postsCollection(groupId).document(postId)
            firestore.runTransaction { txn ->
                val snap = txn.get(docRef)
                val likedBy = (snap.get("likedBy") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                val count = snap.getLong("likeCount") ?: 0L
                if (likedBy.contains(uid)) {
                    txn.update(
                        docRef,
                        mapOf(
                            "likedBy" to FieldValue.arrayRemove(uid),
                            "likeCount" to (count - 1).coerceAtLeast(0)
                        )
                    )
                } else {
                    txn.update(
                        docRef,
                        mapOf(
                            "likedBy" to FieldValue.arrayUnion(uid),
                            "likeCount" to count + 1
                        )
                    )
                }
                null
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(groupId: String, postId: String): Result<Unit> {
        return try {
            postsCollection(groupId).document(postId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        /** Page cap for a single feed read — keeps reads bounded on the free plan. */
        const val FEED_PAGE_LIMIT = 50L
    }
}
