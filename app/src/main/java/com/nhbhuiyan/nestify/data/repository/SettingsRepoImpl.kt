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

    override suspend fun setThemeMode(mode: String) {
        settingsDatastore.setThemeMode(mode)
    }

    override val themeMode: Flow<String>
        get() = settingsDatastore.themeMode

    override suspend fun setSyncEnabled(isSync: Boolean) {
        settingsDatastore.setSync(isSync)
    }

    override val isSyncEnabled: Flow<Boolean>
        get() = settingsDatastore.isSyncEnabled

    override suspend fun setDefaultLevel(level: Int) {
        settingsDatastore.setDefaultLevel(level)
    }

    override val defaultLevel: Flow<Int>
        get() = settingsDatastore.defaultLevel

    override suspend fun setDefaultTerm(term: Int) {
        settingsDatastore.setDefaultTerm(term)
    }

    override val defaultTerm: Flow<Int>
        get() = settingsDatastore.defaultTerm

}