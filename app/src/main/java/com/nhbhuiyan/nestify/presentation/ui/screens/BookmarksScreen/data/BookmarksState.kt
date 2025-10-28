package com.nhbhuiyan.nestify.presentation.ui.screens.BookmarksScreen.data

data class BookmarksState(
    val bookmarks: List<BookmarkItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


data class BookmarkItem(
    val id: String,
    val type: String, // "note", "link", "file"
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val icon: String,
    val color: Long = 0xFF4CAF50
)