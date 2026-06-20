package com.nhbhuiyan.nestify.presentation.ui.screens.settiingsScreen.components

import com.nhbhuiyan.nestify.domain.model.FontSize

data class SettingState(
    val isDarkTheme: Boolean = false ,
    val isBiometricEnabled: Boolean = false,
    val isSyncEnabled: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
