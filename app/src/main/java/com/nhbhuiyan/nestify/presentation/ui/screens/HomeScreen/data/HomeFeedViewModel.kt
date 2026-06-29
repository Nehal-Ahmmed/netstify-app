package com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.manager.UserSessionManager
import com.nhbhuiyan.nestify.domain.model.Announcement
import com.nhbhuiyan.nestify.domain.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Read-only feed ViewModel for the Home dashboard: surfaces the class-group
 * announcements through the existing [AnnouncementRepository] so the dashboard can
 * render real broadcast data. Additive only — it does not modify any existing
 * ViewModel, repository, or data-layer API.
 */
@HiltViewModel
class HomeFeedViewModel @Inject constructor(
    announcementRepository: AnnouncementRepository,
    sessionManager: UserSessionManager,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val announcements: StateFlow<List<Announcement>> = sessionManager.sessionFlow.flatMapLatest { session ->
        if (session != null) {
            announcementRepository.getAnnouncements(session.classGroupId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )
}
