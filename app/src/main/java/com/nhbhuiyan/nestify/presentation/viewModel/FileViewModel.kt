package com.nhbhuiyan.nestify.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.CreateFileUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAllFilesUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetFileByIdUseCase
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components.FileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    private val getAllFilesUseCase: GetAllFilesUseCase,
    private val createFileUseCase: CreateFileUseCase,
    private val getFileByIdUseCase: GetFileByIdUseCase,

    ) : ViewModel(){
    private val _uiState = MutableStateFlow(FileUiState())
    val uiState: StateFlow<FileUiState> = _uiState
    init {
        loadFiles()
    }

    private fun loadFiles() {
        viewModelScope.launch {
            getAllFilesUseCase().collect { files ->
                _uiState.value = FileUiState(
                    files = files,
                    isLoading = false
                )
            }
        }
    }
    fun createFiles(uri: String,name: String,fileType: String,size: Long,type: String,mimeType: String){
        viewModelScope.launch {
            createFileUseCase(
                uri = uri,
                fileName = name,
                fileType = fileType,
                fileSize = size,
                mimeType = mimeType
            )
        }
    }

    fun getFileById(id: Long?){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val file= id?.let { getFileByIdUseCase(it) }
                _uiState.value = _uiState.value.copy(file = file, isLoading = false)
                Log.d("FileViewModel", "getFileById: $file")
            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                Log.d("FileViewModel", "getFileById: ${e.message}")
            }
        }
    }
}