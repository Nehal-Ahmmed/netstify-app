package com.nhbhuiyan.nestify.data.repository

import com.nhbhuiyan.nestify.data.datastore.SettingDatastore
import com.nhbhuiyan.nestify.domain.repository.SettingsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepoImpl @Inject constructor(
    private val settingsDatastore: SettingDatastore
) : SettingsRepo {
    override suspend fun setDarkTheme(isDark: Boolean) {
        settingsDatastore.setDarkTheme(isDark)
    }

    override val isDarkTheme: Flow<Boolean>
        get() = settingsDatastore.isDarkTheme

    override suspend fun setFontSize(fontSize: String) {
        settingsDatastore.setFontSize(fontSize)
    }

    override val fontSize: Flow<String>
        get() = settingsDatastore.fontsize

    override suspend fun setBiometricLock(isLock: Boolean) {
        settingsDatastore.setBiometricLock(isLock)
    }

    override val isBiometricLock: Flow<Boolean>
        get() = settingsDatastore.isBiometricLockEnabled

    override suspend fun setSyncEnabled(isSync: Boolean) {
        settingsDatastore.setSync(isSync)
    }

    override val isSyncEnabled: Flow<Boolean>
        get() = settingsDatastore.isSyncEnabled


}