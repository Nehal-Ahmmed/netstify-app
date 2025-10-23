package com.nhbhuiyan.nestify.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.CreateRoutineUsecases
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.GetAllClassRoutinesUsecases
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.GetClassRoutineById
import com.nhbhuiyan.nestify.presentation.ui.screens.RoutineScreen.components.ClassRoutineState
import com.nhbhuiyan.nestify.presentation.viewModel.filemanager.ImageFileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassroutineViewModel @Inject constructor(
    private val createRoutineUsecases: CreateRoutineUsecases,
    private val getAllClassRoutinesUseCases: GetAllClassRoutinesUsecases,
    private val getRoutineByIdUsecase: GetClassRoutineById,
    private val imageFileManager: ImageFileManager
) : ViewModel() {
    private val _state = MutableStateFlow(ClassRoutineState())
    val state: StateFlow<ClassRoutineState> = _state

    init {
        Log.d("nehal", "loadClassRoutines")
        loadClassRoutines()
    }

    private fun loadClassRoutines() {
        viewModelScope.launch {
            getAllClassRoutinesUseCases().collect { routines ->
                _state.value = ClassRoutineState(
                    routines = routines,
                    isLoading = false
                )
            }
        }
    }

    fun createRoutine(
        content: String,
        imageUri: Uri,
        imageDescription: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val imagePath = imageFileManager.saveImageFromUri(imageUri)
                if (imagePath != null) {
                    createRoutineUsecases(
                        content = content,
                        imageUri = imagePath,
                        imageDescription = imageDescription
                    )

                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = null
                    )
                    // Reload routines to show the new one
                    loadClassRoutines()
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Failed to save image"
                    )
                }
            } catch (e: Exception) {
                Log.e("ClassroutineViewModel", "Error creating routine: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to create routine: ${e.message}"
                )
            }
        }
    }

    fun getRoutineById(id: Long?) {

        if (id == null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val routine = getRoutineByIdUsecase(id)
                _state.value = _state.value.copy(
                    routine = routine,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                Log.e("ClassroutineViewModel", "Error getting routine: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load routine: ${e.message}"
                )
            }
        }
    }

    // Function to get image bitmap for display
    fun getImageBitmap(imagePath: String): Bitmap? {
        return imageFileManager.loadImageFromPath(imagePath)
    }
}