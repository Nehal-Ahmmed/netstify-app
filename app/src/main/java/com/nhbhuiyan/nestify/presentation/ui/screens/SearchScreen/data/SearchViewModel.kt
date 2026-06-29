package com.nhbhuiyan.nestify.presentation.ui.screens.SearchScreen.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import com.nhbhuiyan.nestify.presentation.ui.screens.common.FeedListItem
import com.nhbhuiyan.nestify.presentation.ui.screens.common.relativeFeedTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SearchState(
    val results: List<FeedListItem> = emptyList(),
)

/**
 * Global content search over the existing [ContentRepository] search queries (notes/links/files).
 * Query changes are debounced and `flatMapLatest`'d so only the newest query is in flight.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ContentRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val state: StateFlow<SearchState> = _query
        .debounce(250)
        .flatMapLatest { raw ->
            val trimmed = raw.trim()
            if (trimmed.isEmpty()) {
                flowOf(SearchState())
            } else {
                val like = "%$trimmed%"
                combine(
                    repository.searchNotes(like),
                    repository.searchLinks(like),
                    repository.searchFiles(like),
                ) { notes, links, files ->
                    val noteItems = notes.map { n ->
                        FeedListItem(
                            id = n.id.toString(),
                            type = "note",
                            title = n.title,
                            subtitle = n.content.take(80),
                            timestamp = relativeFeedTime(n.updatedAt.toEpochMilliseconds()),
                        )
                    }
                    val linkItems = links.map { l ->
                        FeedListItem(
                            id = l.id.toString(),
                            type = "link",
                            title = l.title ?: l.domain,
                            subtitle = l.domain,
                            timestamp = relativeFeedTime(l.updatedAt.toEpochMilliseconds()),
                        )
                    }
                    val fileItems = files.map { f ->
                        FeedListItem(
                            id = f.id.toString(),
                            type = "file",
                            title = f.fileName,
                            subtitle = f.fileType,
                            timestamp = relativeFeedTime(f.updatedAt.toEpochMilliseconds()),
                        )
                    }
                    SearchState(results = noteItems + linkItems + fileItems)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchState())

    fun onQueryChange(value: String) {
        _query.value = value
    }
}
