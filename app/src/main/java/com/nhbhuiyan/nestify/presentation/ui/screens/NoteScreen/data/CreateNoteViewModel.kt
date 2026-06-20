package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.CreateNoteUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.GetNoteByIdUseCases
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val createNoteUseCase: CreateNoteUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCases,
    private val updateNoteUseCase: UpdateNoteUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateNoteState())
    val uiState: StateFlow<CreateNoteState> = _uiState

    suspend fun loadNote(id: Long){
        val note : Note? = getNoteByIdUseCase(id)
    _uiState.value= _uiState.value.copy(
        id = id,
        title = note?.title ?: "",
        content = note?.content ?: "",
        tags = note?.tags ?: emptyList(),
        createdAt = note?.createdAt,
        isLoading = false,
        isNoteCreated = false,
        error = null
    )
    }


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

    fun updateNote(){
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
                updateNoteUseCase(
                    id = uiState.value.id ?: 0,
                    title = uiState.value.title,
                    content = uiState.value.content,
                    tags = uiState.value.tags,
                    createdAt = uiState.value.createdAt ?: Clock.System.now()
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