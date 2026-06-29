package com.nhbhuiyan.nestify.domain.model

/**
 * A single academic-network feed post, scoped under a class group
 * (`classGroups/{groupId}/posts/{id}` — same boundary as announcements).
 *
 * [type] is a stable string key (e.g. "academic_aid"); the presentation layer maps it
 * to a label + ChipTone. Like/comment counts are kept client-side (Spark plan, no Cloud
 * Functions); [likedBy] is the source of truth for the current user's like state.
 */
data class Post(
    val id: String,
    val authorUid: String,
    val authorName: String,
    val authorMeta: String,        // e.g. "CSE · Roll 042 · CR"
    val type: String,              // stable category key
    val title: String,
    val body: String,
    val tags: List<String> = emptyList(),
    val createdAt: Long? = null,   // epoch millis, null while server timestamp is pending
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val likedBy: List<String> = emptyList(),
)
