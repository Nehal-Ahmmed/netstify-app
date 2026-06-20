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
}