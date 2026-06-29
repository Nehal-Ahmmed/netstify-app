package com.nhbhuiyan.nestify.presentation.ui.screens.ArchiveScreen.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAllFilesUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.GetAllLinksUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.GetAllNotesUseCase
import com.nhbhuiyan.nestify.presentation.ui.screens.common.FeedListItem
import com.nhbhuiyan.nestify.presentation.ui.screens.common.relativeFeedTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArchiveState(
    val items: List<FeedListItem> = emptyList(),
    val isLoading: Boolean = false,
)

/**
 * Surfaces archived notes/links/files (the existing `isArchived` flag) — mirrors
 * [com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.data.BookmarksViewModel], filtering
 * on archive state instead. Read-only; no schema change.
 */
@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val getAllLinksUseCase: GetAllLinksUseCase,
    private val getAllFilesUseCase: GetAllFilesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ArchiveState(isLoading = true))
    val state: StateFlow<ArchiveState> = _state

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            combine(
                getAllNotesUseCase(),
                getAllLinksUseCase(),
                getAllFilesUseCase(),
            ) { notes, links, files ->
                val noteItems = notes.filter { it.isArchived }.map { n ->
                    n.updatedAt.toEpochMilliseconds() to FeedListItem(
                        id = n.id.toString(),
                        type = "note",
                        title = n.title,
                        subtitle = n.content.take(80),
                        timestamp = relativeFeedTime(n.updatedAt.toEpochMilliseconds()),
                    )
                }
                val linkItems = links.filter { it.isArchived }.map { l ->
                    l.updatedAt.toEpochMilliseconds() to FeedListItem(
                        id = l.id.toString(),
                        type = "link",
                        title = l.title ?: l.domain,
                        subtitle = l.domain,
                        timestamp = relativeFeedTime(l.updatedAt.toEpochMilliseconds()),
                    )
                }
                val fileItems = files.filter { it.isArchived }.map { f ->
                    f.updatedAt.toEpochMilliseconds() to FeedListItem(
                        id = f.id.toString(),
                        type = "file",
                        title = f.fileName,
                        subtitle = f.fileType,
                        timestamp = relativeFeedTime(f.updatedAt.toEpochMilliseconds()),
                    )
                }
                (noteItems + linkItems + fileItems)
                    .sortedByDescending { it.first }
                    .map { it.second }
            }.collect { items ->
                _state.value = ArchiveState(items = items, isLoading = false)
            }
        }
    }
}
