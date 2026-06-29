package com.nhbhuiyan.nestify.presentation.ui.screens.settiingsScreen.components

data class SettingState(
    val isDarkTheme: Boolean = false ,
    val themeMode: String = "system", // "system" | "light" | "dark"
    val isSyncEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
