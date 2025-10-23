package com.nhbhuiyan.nestify.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.CreateNoteUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.GetAllNotesUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.GetNoteByIdUseCases
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.components.NotesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            getAllNotesUseCase().collect{notes->
                _uiState.value = NotesUiState(
                    notes = notes,
                    isLoading = false
                )
            }
        }
    }

     fun createNote(title: String, content: String, tags: List<String> = emptyList()){
        viewModelScope.launch {
            createNoteUseCase(title,content,tags)
        }
    }

    fun getNoteById(id: Long){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val note = getNoteByIdUseCase(id)
                Log.d("noteId",note.toString())
                _uiState.value = _uiState.value.copy(
                    note = note,
                    isLoading = false
                )
            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }

        }
    }
}