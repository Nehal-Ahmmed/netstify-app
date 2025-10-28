package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.CreateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val createNoteUseCase: CreateNoteUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateNoteState())
    val uiState: StateFlow<CreateNoteState> = _uiState

    fun updateTitle(title: String){
        _uiState.value= _uiState.value.copy(title=title)
    }

    fun updateContent(content: String){
        _uiState.value= _uiState.value.copy(content=content)
    }

    fun updateTags(tags: List<String>){
        _uiState.value = _uiState.value.copy(tags = tags)
    }

    fun createNote(){
        _uiState.value= _uiState.value.copy(
            isLoading = true,
            isNoteCreated = false,
            error = null
        )

        if(uiState.value.title.isNullOrEmpty() || uiState.value.content.isNullOrEmpty()){
            _uiState.value = _uiState.value.copy(error = "Title and content cannot be empty")
            return
        }

        viewModelScope.launch {

            try {
                createNoteUseCase(
                    title = uiState.value.title,
                    content = uiState.value.content,
                    tags = uiState.value.tags
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isNoteCreated = true,
                    error = null
                )
            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isNoteCreated = false,
                    error = e.message
                )
            }
        }
    }
}