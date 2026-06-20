    package com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.data

    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.nhbhuiyan.nestify.domain.model.Note
    import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.BookmarkNoteUseCase
    import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.CreateNoteUseCase
    import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.DeleteNoteUseCase
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
        private val getNoteByIdUseCase: GetNoteByIdUseCases,
        private val deleteNoteUsecases: DeleteNoteUseCase,
        private val bookmarkNoteUseCase: BookmarkNoteUseCase
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(NotesUiState())
        val uiState: StateFlow<NotesUiState> = _uiState

        init {
            loadNotes()
        }

        private fun loadNotes() {
            viewModelScope.launch {
                getAllNotesUseCase().collect { notes ->
                    _uiState.value = _uiState.value.copy(
                        notes = notes,
                        isLoading = false
                    )
                }
            }
        }

        fun getNoteById(id: Long) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)

                try {
                    val note = getNoteByIdUseCase(id)
                    Log.d("noteId", note.toString())
                    _uiState.value = _uiState.value.copy(
                        note = note,
                        isLoading = false
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }

            }
        }

        fun deleteNote(id: Long) {
            viewModelScope.launch {
                deleteNoteUsecases(id)
            }
        }

        fun bookmarkNote(id: Long, isBookmarked: Boolean){
            viewModelScope.launch {
                Log.d("NOTE_DETAIL", "🔄 ViewModel: Bookmarking note $id to $isBookmarked")

                // Update the database
                bookmarkNoteUseCase(isBookmarked = isBookmarked, noteId = id)

                // Get the current state
                val currentState = _uiState.value
                val currentNote = currentState.note

                // Update the current note in UI state WITHOUT setting it to null
                if (currentNote != null && currentNote.id == id) {
                    val updatedNote = currentNote.copy(isBookmarked = isBookmarked)
                    _uiState.value = currentState.copy(
                        note = updatedNote, // Keep the note, just update bookmark status
                        notes = currentState.notes.map { note ->
                            if (note.id == id) note.copy(isBookmarked = isBookmarked) else note
                        }
                    )
                    Log.d("NOTE_DETAIL", "✅ ViewModel: Note updated successfully, isBookmarked: $isBookmarked")
                } else {
                    Log.d("NOTE_DETAIL", "❌ ViewModel: Current note is null or ID mismatch")
                }
            }
        }

    }