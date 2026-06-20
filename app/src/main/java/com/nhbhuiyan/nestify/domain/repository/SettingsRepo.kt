package com.nhbhuiyan.nestify.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepo {

    suspend fun setDarkTheme(isDark: Boolean)
    val isDarkTheme: Flow<Boolean>

    suspend fun setFontSize(fontSize: String)
    val fontSize: Flow<String>

    suspend fun setBiometricLock(isLock: Boolean)
    val isBiometricLock: Flow<Boolean>

    suspend fun setSyncEnabled(isSync: Boolean)
    val isSyncEnabled: Flow<Boolean>

}