package com.nhbhuiyan.nestify.presentation.ui.screens.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.model.FontSize
import com.nhbhuiyan.nestify.domain.repository.SettingsRepo
import com.nhbhuiyan.nestify.presentation.ui.screens.settiingsScreen.components.SettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepo
) : ViewModel() {

    // MARK: - UI STATE
    private val _uiState = MutableStateFlow(SettingState(isDarkTheme = false))
    val uiState: StateFlow<SettingState> = _uiState

    // MARK: - DIALOG STATES
    var showFontSizeSelector by mutableStateOf(false)
    var showBackupDialog by mutableStateOf(false)
    var showClearCacheDialog by mutableStateOf(false)
    var showDeleteAllDialog by mutableStateOf(false)

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
                repository.isBiometricLock.collect { isBiometric ->
                    _uiState.update { it.copy(isBiometricEnabled = isBiometric) }
                }
            }

            launch {
                repository.isSyncEnabled.collect { isSync ->
                    _uiState.update { it.copy(isSyncEnabled = isSync) }
                }
            }

            launch {
                repository.fontSize.collect { fontsize ->
                    val fontSizeEnum = when (fontsize) {
                        "SMALL"  -> FontSize.SMALL
                        "LARGE"  -> FontSize.LARGE
                        "XLARGE" -> FontSize.XLARGE
                        else     -> FontSize.MEDIUM
                    }
                    _uiState.update { it.copy(fontSize = fontSizeEnum) }
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
     * Handle biometric lock toggle
     */
    fun onBiometricEnabledChanged(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBiometricEnabled = enabled)
            repository.setBiometricLock(enabled)
        }
    }

    /**
     * Handle cloud sync toggle
     */
    fun onSyncEnabledChanged(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncEnabled = enabled)
            repository.setSyncEnabled(enabled)
        }
    }

    /**
     * Handle font size change
     */
    fun onFontSizeChanged(fontSize: FontSize) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(fontSize = fontSize)
            repository.setFontSize(fontSize.name)
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
            // TODO: Implement backup export logic
            // Export notes to JSON/PDF
            // Save to device storage or cloud
        }
    }

    /**
     * Handle import backup
     */
    fun onImportBackup() {
        viewModelScope.launch {
            // TODO: Implement backup import logic
            // Import from JSON file
            // Validate and restore data
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

    /**
     * Handle delete all notes request
     */
    fun onDeleteAllNotesRequested() {
        showDeleteAllDialog = true
    }

    /**
     * Confirm delete all notes
     */
    fun onDeleteAllNotesConfirmed() {
        viewModelScope.launch {
            // TODO: Delete all notes from database
            // Show confirmation message
            // Navigate to empty state
        }
    }

    /**
     * Handle reset settings request
     */
    fun onResetSettingsRequested() {
        viewModelScope.launch {
            // TODO: Reset all settings to default
            _uiState.value = SettingState() // Reset to default
            // TODO: Clear all SharedPreferences/DataStore
        }
    }
}