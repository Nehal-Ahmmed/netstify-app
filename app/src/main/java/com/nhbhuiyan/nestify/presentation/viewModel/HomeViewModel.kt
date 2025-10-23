package com.nhbhuiyan.nestify.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.GetAllClassRoutinesUsecases
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAllFilesUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.GetAllLinksUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.GetAllNotesUseCase
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.components.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllFilesUseCase: GetAllFilesUseCase,
    private  val getAllLinksUseCase: GetAllLinksUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val getAllClassRoutineUsecase: GetAllClassRoutinesUsecases
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state : StateFlow<HomeUiState> = _state

    init {
        loadAllContent()
    }

    private fun loadAllContent() {
        viewModelScope.launch {
            combine(
                getAllNotesUseCase(),
                getAllLinksUseCase(),
                getAllFilesUseCase(),
                getAllClassRoutineUsecase()
            ){notes, links, files, routines ->
                HomeUiState(
                    notes = notes,
                    links = links,
                    files = files,
                    routines=routines,
                    isLoading = false
                )
            }.collect{state->
                _state.value = state
            }
        }
    }
}