package com.nhbhuiyan.nestify.presentation.ui.screens.AcademicFeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.manager.UserSessionManager
import com.nhbhuiyan.nestify.domain.model.Post
import com.nhbhuiyan.nestify.domain.model.UserRole
import com.nhbhuiyan.nestify.domain.model.UserSession
import com.nhbhuiyan.nestify.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Academic Network feed ViewModel. Mirrors the session-scoped flow pattern used by
 * [com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.data.HomeFeedViewModel] /
 * ManagementViewModel: a single posts stream keyed off the active class group, plus
 * create/like/delete actions that read the current session for author identity.
 *
 * One snapshot listener feeds the whole screen; category filtering is done in the UI so
 * changing the filter never re-subscribes (cost guardrail).
 */
@HiltViewModel
class NetworkFeedViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val sessionManager: UserSessionManager,
) : ViewModel() {

    val sessionFlow: StateFlow<UserSession?> = sessionManager.sessionFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts: StateFlow<List<Post>> = sessionFlow.flatMapLatest { session ->
        if (session != null && session.classGroupId.isNotBlank()) {
            postRepository.getPosts(session.classGroupId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    private val _status = MutableStateFlow<FeedStatus>(FeedStatus.Idle)
    val status: StateFlow<FeedStatus> = _status.asStateFlow()

    fun createPost(
        type: String,
        title: String,
        body: String,
        tags: List<String>,
        onSuccess: () -> Unit = {},
    ) {
        _status.value = FeedStatus.Posting
        viewModelScope.launch {
            val session = sessionFlow.value
            if (session == null || session.classGroupId.isBlank()) {
                _status.value = FeedStatus.Error("No active class group")
                return@launch
            }

            val post = Post(
                id = UUID.randomUUID().toString(),
                authorUid = session.uid,
                authorName = session.displayName.ifBlank { "Anonymous" },
                authorMeta = buildAuthorMeta(session),
                type = type,
                title = title.trim(),
                body = body.trim(),
                tags = tags,
            )

            postRepository.createPost(session.classGroupId, post)
                .onSuccess {
                    _status.value = FeedStatus.Idle
                    onSuccess()
                }
                .onFailure {
                    _status.value = FeedStatus.Error(it.localizedMessage ?: "Failed to post")
                }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val session = sessionFlow.value ?: return@launch
            if (session.classGroupId.isBlank()) return@launch
            postRepository.toggleLike(session.classGroupId, postId, session.uid)
                .onFailure {
                    _status.value = FeedStatus.Error(it.localizedMessage ?: "Failed to update like")
                }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            val session = sessionFlow.value ?: return@launch
            if (session.classGroupId.isBlank()) return@launch
            postRepository.deletePost(session.classGroupId, postId)
                .onFailure {
                    _status.value = FeedStatus.Error(it.localizedMessage ?: "Failed to remove post")
                }
        }
    }

    /** True when the user authored the post or holds a CR/admin+ role (moderation). */
    fun canModerate(post: Post, session: UserSession?): Boolean {
        if (session == null) return false
        return post.authorUid == session.uid || session.role.rank >= UserRole.CR.rank
    }

    fun clearStatus() {
        _status.value = FeedStatus.Idle
    }

    private fun buildAuthorMeta(session: UserSession): String {
        val parts = mutableListOf<String>()
        if (session.departmentCode.isNotBlank()) parts += session.departmentCode
        if (session.rollNumber.isNotBlank()) parts += "Roll ${session.rollNumber}"
        if (session.role.rank >= UserRole.CR.rank) parts += session.role.roleName.uppercase()
        return parts.joinToString(" · ")
    }
}

sealed interface FeedStatus {
    object Idle : FeedStatus
    object Posting : FeedStatus
    data class Error(val message: String) : FeedStatus
}
