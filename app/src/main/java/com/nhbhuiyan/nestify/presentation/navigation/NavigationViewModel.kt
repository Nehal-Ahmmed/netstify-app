package com.nhbhuiyan.nestify.presentation.navigation

import androidx.lifecycle.ViewModel
import com.nhbhuiyan.nestify.domain.manager.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    val sessionManager: UserSessionManager
) : ViewModel() {
    val session = sessionManager.sessionFlow
}
