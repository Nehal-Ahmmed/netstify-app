package com.nhbhuiyan.nestify.domain.manager

import com.nhbhuiyan.nestify.domain.repository.SettingsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppSettingManager @Inject constructor(
    private val settingRepo : SettingsRepo
) {
    //dark theme
    val isDarkMode : Flow<Boolean> = settingRepo.isDarkTheme
    suspend fun setDarkTheme(isDark : Boolean) = settingRepo.setDarkTheme(isDark = isDark)

    // 3-way theme mode ("system" | "light" | "dark")
    val themeMode : Flow<String> = settingRepo.themeMode
    suspend fun setThemeMode(mode : String) = settingRepo.setThemeMode(mode)
}