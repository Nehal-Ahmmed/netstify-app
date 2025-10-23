package com.nhbhuiyan.nestify.presentation.ui.screens.RoutineScreen.components

import com.nhbhuiyan.nestify.domain.model.ClassRoutine

data class ClassRoutineState(
    val isLoading : Boolean = true,
    val routine: ClassRoutine? = null,
    val routines: List<ClassRoutine> = emptyList(),
    val error : String? = null
)
