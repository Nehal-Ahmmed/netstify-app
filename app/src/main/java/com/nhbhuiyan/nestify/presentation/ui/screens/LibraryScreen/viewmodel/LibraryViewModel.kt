package com.nhbhuiyan.nestify.presentation.ui.screens.LibraryScreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemStatus
import com.nhbhuiyan.nestify.domain.usecases.LibraryUseCases.AddLibraryItemUseCase
import com.nhbhuiyan.nestify.domain.usecases.LibraryUseCases.DeleteLibraryItemUseCase
import com.nhbhuiyan.nestify.domain.usecases.LibraryUseCases.GetAllLibraryItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAllLibraryItemsUseCase: GetAllLibraryItemsUseCase,
    private val addLibraryItemUseCase: AddLibraryItemUseCase,
    private val deleteLibraryItemUseCase: DeleteLibraryItemUseCase
) : ViewModel() {

    private val _libraryItems = MutableStateFlow<List<LibraryItemEntity>>(emptyList())
    val libraryItems: StateFlow<List<LibraryItemEntity>> = _libraryItems.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            getAllLibraryItemsUseCase().collect { items ->
                _libraryItems.value = items
            }
        }
    }

    fun addItem(item: LibraryItemEntity) {
        viewModelScope.launch {
            addLibraryItemUseCase(item)
        }
    }

    fun deleteItem(item: LibraryItemEntity) {
        viewModelScope.launch {
            deleteLibraryItemUseCase(item)
        }
    }
}
