package com.nhbhuiyan.nestify.presentation.ui.screens.Management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.manager.UserSessionManager
import com.nhbhuiyan.nestify.domain.model.Announcement
import com.nhbhuiyan.nestify.domain.model.ClassGroupRosterItem
import com.nhbhuiyan.nestify.domain.model.MergeRequest
import com.nhbhuiyan.nestify.domain.model.UserRole
import com.nhbhuiyan.nestify.domain.repository.AnnouncementRepository
import com.nhbhuiyan.nestify.domain.repository.ClassGroupRepository
import com.nhbhuiyan.nestify.domain.repository.MergeRequestRepository
import com.nhbhuiyan.nestify.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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

import com.nhbhuiyan.nestify.domain.repository.DepartmentRepository

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val classGroupRepository: ClassGroupRepository,
    private val mergeRequestRepository: MergeRequestRepository,
    private val userRepository: UserRepository,
    private val departmentRepository: DepartmentRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    val sessionFlow = sessionManager.sessionFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    val roster: StateFlow<List<ClassGroupRosterItem>> = sessionFlow.flatMapLatest { session ->
        if (session != null) {
            classGroupRepository.getClassRoster(session.classGroupId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pendingMergeRequests: StateFlow<List<MergeRequest>> = sessionFlow.flatMapLatest { session ->
        if (session != null) {
            mergeRequestRepository.getMergeRequests(session.classGroupId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _managementStatus = MutableStateFlow<ResultStatus>(ResultStatus.Idle)
    val managementStatus: StateFlow<ResultStatus> = _managementStatus.asStateFlow()

    fun postAnnouncement(title: String, body: String, priority: String, onSuccess: () -> Unit) {
        _managementStatus.value = ResultStatus.Loading
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.value
            if (session == null) {
                _managementStatus.value = ResultStatus.Error("No active user session")
                return@launch
            }

            val announcement = Announcement(
                id = UUID.randomUUID().toString(),
                title = title,
                body = body,
                createdBy = session.uid,
                priority = priority.lowercase()
            )

            announcementRepository.postAnnouncement(session.classGroupId, announcement)
                .onSuccess {
                    _managementStatus.value = ResultStatus.Success
                    onSuccess()
                }
                .onFailure {
                    _managementStatus.value = ResultStatus.Error(it.localizedMessage ?: "Failed to post announcement")
                }
        }
    }

    fun setUserRole(uid: String, role: UserRole) {
        _managementStatus.value = ResultStatus.Loading
        viewModelScope.launch {
            userRepository.updateAcademicUserRole(uid, role.roleName)
                .onSuccess {
                    _managementStatus.value = ResultStatus.Success
                }
                .onFailure {
                    _managementStatus.value = ResultStatus.Error(it.localizedMessage ?: "Failed to update role")
                }
        }
    }

    fun resolveMergeRequest(mrId: String, status: String, reviewNote: String?) {
        _managementStatus.value = ResultStatus.Loading
        viewModelScope.launch {
            val session = sessionManager.sessionFlow.value
            if (session == null) {
                _managementStatus.value = ResultStatus.Error("No active user session")
                return@launch
            }

            // If approving, apply the proposed data directly to Firestore (client-side Spark plan replacement)
            if (status == "accepted") {
                val mr = pendingMergeRequests.value.firstOrNull { it.id == mrId }
                if (mr != null) {
                    val applyResult = applyMergeRequestData(mr)
                    if (applyResult.isFailure) {
                        _managementStatus.value = ResultStatus.Error(
                            applyResult.exceptionOrNull()?.localizedMessage ?: "Failed to apply merge request data"
                        )
                        return@launch
                    }
                    android.util.Log.d("ManagementVM", "✅ Applied MR data: type=${mr.type}, target=${mr.target}")
                }
            }

            mergeRequestRepository.resolveMergeRequest(
                groupId = session.classGroupId,
                mrId = mrId,
                status = status,
                reviewerUid = session.uid,
                reviewNote = reviewNote
            ).onSuccess {
                android.util.Log.d("ManagementVM", "✅ MR $mrId resolved as $status")
                _managementStatus.value = ResultStatus.Success
            }.onFailure {
                android.util.Log.e("ManagementVM", "❌ Failed to resolve MR $mrId: ${it.message}")
                _managementStatus.value = ResultStatus.Error(it.localizedMessage ?: "Failed to resolve request")
            }
        }
    }

    /**
     * Client-side application of approved merge request data.
     * Replaces the Cloud Function `onMergeRequestResolved` from index.ts.
     */
    private suspend fun applyMergeRequestData(mr: MergeRequest): Result<Unit> {
        return try {
            when (mr.type) {
                "subject" -> {
                    val subject = com.nhbhuiyan.nestify.domain.model.AcademicSubject(
                        code = mr.target,
                        name = (mr.data["name"] as? String) ?: mr.target,
                        credits = ((mr.data["credits"] as? Number)?.toFloat()) ?: 3.0f,
                        examDate = (mr.data["examDate"] as? String) ?: ""
                    )
                    departmentRepository.saveSubject(mr.departmentCode, mr.semesterId, subject)
                }
                "topic" -> {
                    val topic = com.nhbhuiyan.nestify.domain.model.AcademicTopic(
                        id = (mr.data["id"] as? String) ?: java.util.UUID.randomUUID().toString(),
                        subjectCode = mr.target,
                        section = (mr.data["section"] as? String) ?: "A",
                        title = (mr.data["title"] as? String) ?: ""
                    )
                    departmentRepository.saveTopic(mr.departmentCode, mr.semesterId, mr.target, topic)
                }
                else -> {
                    android.util.Log.w("ManagementVM", "⚠️ Unsupported MR type: ${mr.type}")
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearStatus() {
        _managementStatus.value = ResultStatus.Idle
    }
}

sealed interface ResultStatus {
    object Idle : ResultStatus
    object Loading : ResultStatus
    object Success : ResultStatus
    data class Error(val message: String) : ResultStatus
}
