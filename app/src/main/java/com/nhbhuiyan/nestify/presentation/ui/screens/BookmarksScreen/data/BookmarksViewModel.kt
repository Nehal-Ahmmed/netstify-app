package com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAllFilesUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.GetAllLinksUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.GetAllNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val getAllLinksUseCase: GetAllLinksUseCase,
    private val getAllFilesUseCase: GetAllFilesUseCase
) : ViewModel() {
    private val _bookmarkState = MutableStateFlow(BookmarksState())
    val bookmarkState: StateFlow<BookmarksState> = _bookmarkState

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            _bookmarkState.value = _bookmarkState.value.copy(isLoading = true)

            //combined function calling
            combine(
                getAllNotesUseCase(),
                getAllLinksUseCase(),
                getAllFilesUseCase()
            ) { noteList, linkList, fileList ->

                val noteBookmarks = noteList.map { note ->
                    BookmarkItem(
                        id = note.id.toString(),
                        type = "note",
                        title = note.title,
                        subtitle = note.content.take(60) + if (note.content.length > 60) "..." else "",
                        timestamp = formatTimestamp(note.updatedAt.toEpochMilliseconds()),
                        icon = "📝",
                        color = 0xFF4CAF50
                    )
                }

                val linkBookmarks = linkList.map { link ->
                    BookmarkItem(
                        id = link.id.toString(),
                        type = "link",
                        title = link.title ?: link.domain,
                        subtitle = link.domain,
                        timestamp = formatTimestamp(link.updatedAt.toEpochMilliseconds()),
                        icon = "🔗",
                        color = 0xFF2196F3
                    )
                }

                val fileBookmarks = fileList.map { file ->
                    BookmarkItem(
                        id = file.id.toString(),
                        type = "file",
                        title = file.fileName,
                        subtitle = "${file.fileType} • ${formatFileSize(file.fileSize)}",
                        timestamp = formatTimestamp(file.updatedAt.toEpochMilliseconds()),
                        icon = getFileIcon(file.fileType),
                        color = 0xFFFF9800
                    )
                }

                (noteBookmarks + linkBookmarks + fileBookmarks)
                    .sortedByDescending {
                        when (it.type) {
                            "note" -> noteList.find { note -> note.id.toString() == it.id }?.updatedAt?.toEpochMilliseconds()
                                ?: 0

                            "link" -> linkList.find { link -> link.id.toString() == it.id }?.updatedAt?.toEpochMilliseconds()
                                ?: 0

                            "file" -> fileList.find { file -> file.id.toString() == it.id }?.updatedAt?.toEpochMilliseconds()
                                ?: 0

                            else -> 0
                        }
                    }
            }.collect { bookmarks ->
                _bookmarkState.value = BookmarksState(
                    bookmarks = bookmarks,
                    isLoading = false
                )
            }
        }
    }

    fun removeBookmark(itemId: String, itemType: String){
        val currentBookmarks = _bookmarkState.value.bookmarks
        _bookmarkState.value = _bookmarkState.value.copy(
            bookmarks = currentBookmarks.filter { it.id != itemId || it.type != itemType }
        )
    }

    fun refreshBookmarks(){
        loadBookmarks()
    }

    private fun formatTimestamp(timestamp: Long) : String{
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when{
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} mins ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
            diff < 30 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} days ago"
            else -> "${diff / (30 * 24 * 60 * 60 * 1000)} months ago"
        }
    }

    private fun formatFileSize(size: Long) : String{
        return when{
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }

    private fun getFileIcon(fileType: String) : String {
        return when (fileType.lowercase()) {
            "pdf" -> "📄"
            "image" -> "🖼️"
            "document" -> "📝"
            "video" -> "🎥"
            "audio" -> "🎵"
            else -> "📎"
        }
    }
}
