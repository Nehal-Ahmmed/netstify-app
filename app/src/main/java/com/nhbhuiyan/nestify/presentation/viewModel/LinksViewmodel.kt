package com.nhbhuiyan.nestify.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.CreateLinkUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.GetAllLinksUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.GetLinkByIdUsecase
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components.LinkUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinksViewmodel @Inject constructor(
    private val getAllLinksUseCase: GetAllLinksUseCase,
    private val createLinkUseCase: CreateLinkUseCase,
    private val getLinkByIdUseCase: GetLinkByIdUsecase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LinkUiState())
    val uiState: StateFlow<LinkUiState> = _uiState

    init {
        loadLinks()
    }

    private fun loadLinks() {
        viewModelScope.launch {
            getAllLinksUseCase().collect { links ->
                _uiState.value = LinkUiState(
                    links = links,
                    isLoading = false
                )
            }
        }
    }

    fun createLink(title: String, description: String, url: String){
        viewModelScope.launch {
            createLinkUseCase(title,description,url)
        }
    }

    fun getLinkById(id: Long?){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val link = id?.let { getLinkByIdUseCase(it) }
                Log.d("link",link.toString())
                _uiState.value = _uiState.value.copy(link=link, isLoading = false, error = null)
            }catch (e: Exception){
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}