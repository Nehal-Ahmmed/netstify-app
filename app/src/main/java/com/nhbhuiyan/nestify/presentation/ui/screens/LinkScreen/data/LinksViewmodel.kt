package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.*
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components.LinkUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinksViewmodel @Inject constructor(
    private val getAllLinksUseCase: GetAllLinksUseCase,
    private val createLinkUseCase: CreateLinkUseCase,
    private val getLinkByIdUseCase: GetLinkByIdUsecase,
    private val deleteLinkByIdUseCase: DeleteLinkUseCase,
    private val bookmarkLinkUseCase: BookmarkLinkUseCase,
    private val createLinkFolderUseCase: CreateLinkFolderUseCase,
    private val getAllLinkFoldersUseCase: GetAllLinkFoldersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LinkUiState())
    val uiState: StateFlow<LinkUiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getAllLinksUseCase(),
                getAllLinkFoldersUseCase()
            ) { links, folders ->
                _uiState.value.copy(
                    links = links,
                    folders = folders,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun createLink(title: String, description: String, url: String, folderId: Long? = null) {
        viewModelScope.launch {
            createLinkUseCase(title, description, url, folderId)
        }
    }

    fun createFolder(name: String, color: Int) {
        viewModelScope.launch {
            createLinkFolderUseCase(name, color)
        }
    }

    fun getLinkById(id: Long?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val link = id?.let { getLinkByIdUseCase(it) }
                _uiState.value = _uiState.value.copy(link = link, isLoading = false, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun deleteLinkById(id: Long) {
        viewModelScope.launch {
            deleteLinkByIdUseCase(id)
        }
    }

    fun bookmarkLink(linkId: Long, isBookmarked: Boolean) {
        viewModelScope.launch {
            bookmarkLinkUseCase(linkId, isBookmarked)
        }
    }
}