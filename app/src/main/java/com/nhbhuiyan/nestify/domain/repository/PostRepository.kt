package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.domain.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Academic Network feed repository. All posts live under
 * `classGroups/{groupId}/posts` so the feed stays class-scoped and cost-bounded.
 */
interface PostRepository {
    /**
     * Live feed for a class group, newest first, capped to a page limit (cost guardrail).
     * [typeFilter] is an optional category key applied client-side so the feed keeps a
     * single snapshot listener regardless of the active filter.
     */
    fun getPosts(groupId: String, typeFilter: String? = null): Flow<List<Post>>

    suspend fun createPost(groupId: String, post: Post): Result<Unit>

    /** Toggles [uid] in `likedBy` (arrayUnion/arrayRemove) and keeps `likeCount` in sync. */
    suspend fun toggleLike(groupId: String, postId: String, uid: String): Result<Unit>

    suspend fun deletePost(groupId: String, postId: String): Result<Unit>
}
