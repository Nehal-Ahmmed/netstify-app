package com.nhbhuiyan.nestify.presentation.ui.screens.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.repository.SettingsRepo
import com.nhbhuiyan.nestify.domain.manager.UserSessionManager
import com.nhbhuiyan.nestify.data.sync.SyncManager
import com.nhbhuiyan.nestify.presentation.ui.screens.settiingsScreen.components.SettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepo,
    private val authRepository: com.nhbhuiyan.nestify.domain.repository.AuthRepository,
    val sessionManager: UserSessionManager,
    private val syncManager: SyncManager
) : ViewModel() {

    val sessionFlow = sessionManager.sessionFlow

    // MARK: - UI STATE
    private val _uiState = MutableStateFlow(SettingState(isDarkTheme = false))
    val uiState: StateFlow<SettingState> = _uiState

    // MARK: - DIALOG STATES
    var showBackupDialog by mutableStateOf(false)
    var showClearCacheDialog by mutableStateOf(false)

    init {
        loadSettingFromRepository()
        Log.d("viewmodel", "loadSettingfromRepository: ${uiState.value} ")
    }

    private fun loadSettingFromRepository() {

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            launch {
                repository.isDarkTheme.collect { isDark ->
                    Log.d("viewmodel", "loadSettingFromRepository: $isDark")
                    _uiState.update { it.copy(isDarkTheme = isDark) }
                    Log.d("viewmodel", "loadSettingFromRepository: ${uiState.value} ")
                }
            }

            launch {
                repository.themeMode.collect { mode ->
                    _uiState.update { it.copy(themeMode = mode) }
                }
            }

            launch {
                repository.isSyncEnabled.collect { isSync ->
                    _uiState.update { it.copy(isSyncEnabled = isSync) }
                }
            }
        }

        _uiState.value = _uiState.value.copy(isLoading = false)
    }


    /**
     * Handle dark theme toggle
     */
    fun onDarkThemeChanged(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDarkTheme = enabled)
            repository.setDarkTheme(enabled)
        }
    }

    /**
     * Handle 3-way theme mode change ("system" | "light" | "dark"). Persisted via
     * [SettingsRepo.setThemeMode]; MainActivity observes it to drive [NestifyTheme].
     */
    fun onThemeModeChanged(mode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(themeMode = mode)
            repository.setThemeMode(mode)
        }
    }

    /**
     * Handle cloud sync toggle
     */
    fun onSyncEnabledChanged(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncEnabled = enabled)
            repository.setSyncEnabled(enabled)
            if (enabled) {
                syncManager.performRestore()
            }
        }
    }

    /**
     * Handle backup request
     */
    fun onBackupRequested() {
        showBackupDialog = true
    }

    /**
     * Handle export backup
     */
    fun onExportBackup() {
        viewModelScope.launch {
            syncManager.performBackup()
        }
    }

    /**
     * Handle import backup
     */
    fun onImportBackup() {
        viewModelScope.launch {
            syncManager.performRestore()
        }
    }

    /**
     * Handle clear cache request
     */
    fun onClearCacheRequested() {
        showClearCacheDialog = true
    }

    /**
     * Confirm clear cache
     */
    fun onClearCacheConfirmed() {
        viewModelScope.launch {
            // TODO: Clear app cache
            // Clear temporary files
            // Clear image cache, etc.
        }
    }

    /**
     * Handle export data request
     */
    fun onExportDataRequested() {
        viewModelScope.launch {
            // TODO: Export all notes as PDF/text
            // Generate comprehensive report
            // Share or save to device
        }
    }

    /**
     * Handle check for updates
     */
    fun onCheckForUpdates() {
        viewModelScope.launch {
            // TODO: Check for app updates
            // Connect to Play Store API
            // Show update dialog if available
        }
    }

    /**
     * Handle privacy policy click
     */
    fun onPrivacyPolicyClicked() {
        // TODO: Open privacy policy web page
        // Or show in-app privacy policy screen
    }

    /**
     * Handle terms of service click
     */
    fun onTermsOfServiceClicked() {
        // TODO: Open terms web page
        // Or show in-app terms screen
    }

    /**
     * Handle rate app click
     */
    fun onRateAppClicked() {
        // TODO: Open Play Store rating page
        // Or show in-app rating dialog
    }

    fun linkGoogleAccount(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.linkGoogleAccount(idToken)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to link account")
            }
        }
    }

    fun unlinkGoogleAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.unlinkGoogleAccount()
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to unlink account")
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onComplete()
        }
    }
}