package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.model.FileFolder
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAllFilesUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.FileUploadUseCases
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetFileByIdUseCase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.fileFolderUsescases.createFolderUsecase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.fileFolderUsescases.getFilesByFolderUsecase
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.fileFolderUsescases.getFoldersByCategoryUsecase
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components.FileUiState
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components.fileFolderScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    private val getAllFilesUseCase: GetAllFilesUseCase,
    private val uploadFileUseCase: FileUploadUseCases,
    private val getFileByIdUseCase: GetFileByIdUseCase,
    private val getFilesByFolderUseCase: getFilesByFolderUsecase,
    private val getFoldersByCategoryUseCase: getFoldersByCategoryUsecase,
    private val createFileFolderUsecase: createFolderUsecase
) : ViewModel() {

    // UI State - Only data, no UI logic
    private val _fileuiState = MutableStateFlow(FileUiState())
    val fileUiState: StateFlow<FileUiState> = _fileuiState
    private val _folderUiState = MutableStateFlow(fileFolderScreenState())
    val folderUiState: StateFlow<fileFolderScreenState> = _folderUiState

    init {
        Log.d("FileViewModel", "🎯 FileViewModel initialized - Data layer ready")
        loadAllFolders()
    }

    private fun loadAllFolders(){
        viewModelScope.launch {
            _folderUiState.value= _folderUiState.value.copy(isLoading = true)
            combine(
                getAllFilesUseCase(),
                getFoldersByCategoryUseCase("pdf"),
                getFoldersByCategoryUseCase("photo"),
                getFoldersByCategoryUseCase("document")
            ){ files, pdfFolders, photoFolders, documentFolders->
                fileFolderScreenState(
                    files = files,
                    pdfFolders = pdfFolders,
                    photoFolders = photoFolders,
                    documentFolders = documentFolders,
                    isLoading = false
                )
            }.collect { state->
                _folderUiState.value= state
            }
        }
    }

    fun createFolder(category: String, name : String, isCustom: Boolean, color: Int, icon: String){
        viewModelScope.launch {
            _folderUiState.value = _folderUiState.value.copy(isLoading = true)

            val folder = FileFolder(
                name = name,
                color = color,
                isCustom = isCustom,
                category = category,
                icon = icon,
                createdAt = kotlinx.datetime.Clock.System.now(),
                updatedAt = kotlinx.datetime.Clock.System.now()
            )

            createFileFolderUsecase(folder)
            loadAllFolders()
        }
    }

    fun getFilesByFolder(folderId: Long){
        viewModelScope.launch {
            _fileuiState.value = _fileuiState.value.copy(isLoading = true)
            getFilesByFolderUseCase(folderId).collect { files->
                _fileuiState.value = _fileuiState.value.copy(files = files, isLoading = false)
            }
        }
    }


    // Data operations only
    private fun loadFiles() {
        viewModelScope.launch {
            Log.d("FileViewModel", "📂 Loading files from database")
            getAllFilesUseCase().collect { files ->
                _fileuiState.value = _fileuiState.value.copy(
                    files = files,
                    isLoading = false
                )
                Log.d("FileViewModel", "📂 Loaded ${files.size} files")
            }
        }
    }

    fun uploadFile(
        uri: String,
        name: String,
        fileType: String,
        size: Long,
        folderId: Long,
        mimeType: String,
        moveFile: Boolean
    ) {
        Log.d("FileViewModel", "🚀 Uploading file: $name")

        viewModelScope.launch {
            _fileuiState.value = _fileuiState.value.copy(isLoading = true)

            try {
                val fileId = uploadFileUseCase(
                    sourceUri = uri,
                    fileName = name,
                    fileType = fileType,
                    fileSize = size,
                    mimeType = mimeType,
                    folderId = folderId,
                    moveFile = moveFile
                )

                Log.d("FileViewModel", "✅ File uploaded successfully with ID: $fileId")

                // Reload files to reflect changes
                loadAllFolders()

            } catch (e: Exception) {
                Log.e("FileViewModel", "❌ Upload failed: ${e.message}", e)
                _fileuiState.value = _fileuiState.value.copy(
                    error = "Upload failed: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun getFileById(id: Long?) {
        viewModelScope.launch {
            _fileuiState.value = _fileuiState.value.copy(isLoading = true)
            try {
                val file = id?.let { getFileByIdUseCase(it) }
                _fileuiState.value = _fileuiState.value.copy(file = file, isLoading = false)
                Log.d("FileViewModel", "📄 Retrieved file: ${file?.fileName}")
            } catch (e: Exception) {
                _fileuiState.value = _fileuiState.value.copy(error = e.message, isLoading = false)
                Log.e("FileViewModel", "❌ Error getting file: ${e.message}")
            }
        }
    }

    // Simple state updates - UI will handle the logic
    fun clearError() {
        _fileuiState.value = _fileuiState.value.copy(error = null)
    }
}