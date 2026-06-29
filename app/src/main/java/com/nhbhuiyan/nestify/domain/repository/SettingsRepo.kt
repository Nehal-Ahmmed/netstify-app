package com.nhbhuiyan.nestify.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepo {

    suspend fun setDarkTheme(isDark: Boolean)
    val isDarkTheme: Flow<Boolean>

    /** 3-way theme mode: "system" | "light" | "dark". */
    suspend fun setThemeMode(mode: String)
    val themeMode: Flow<String>

    suspend fun setSyncEnabled(isSync: Boolean)
    val isSyncEnabled: Flow<Boolean>

    suspend fun setDefaultLevel(level: Int)
    val defaultLevel: Flow<Int>

    suspend fun setDefaultTerm(term: Int)
    val defaultTerm: Flow<Int>

}