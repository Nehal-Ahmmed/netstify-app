package com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.data

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.model.ClassRoutine
import com.nhbhuiyan.nestify.domain.usecases.RoutinesUsecases.GetAllClassRoutinesUsecases
import com.nhbhuiyan.nestify.domain.usecases.FIleUsecases.GetAllFilesUseCase
import com.nhbhuiyan.nestify.domain.usecases.Linkusecases.GetAllLinksUseCase
import com.nhbhuiyan.nestify.domain.usecases.NoteUseCases.GetAllNotesUseCase
import com.nhbhuiyan.nestify.presentation.ui.screens.home.data.Category
import com.nhbhuiyan.nestify.presentation.ui.screens.home.data.HomeState
import com.nhbhuiyan.nestify.presentation.ui.screens.home.data.NoteItem
import com.nhbhuiyan.nestify.presentation.ui.screens.home.data.QuickAction
import com.nhbhuiyan.nestify.presentation.ui.screens.home.data.RecentItem
import com.nhbhuiyan.nestify.presentation.ui.screens.home.data.WeeklyProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * HomeViewModel that uses your actual domain models and use cases
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllFilesUseCase: GetAllFilesUseCase,
    private val getAllLinksUseCase: GetAllLinksUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val getAllClassRoutineUsecase: GetAllClassRoutinesUsecases
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        loadAllContent()
    }

    /**
     * Load all content from your use cases
     */
    private fun loadAllContent() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isLoading = true)

            // Combine all your data streams
            combine(
                getAllNotesUseCase(),
                getAllLinksUseCase(),
                getAllFilesUseCase(),
                getAllClassRoutineUsecase()
            ) { notes, links, files, routines ->
                // Transform your actual domain data to UI state
                transformToHomeState(
                    notes = notes,
                    links = links,
                    files = files,
                    routines = routines
                )
            }.collect { state ->
                _homeState.value = state.copy(isLoading = false)
            }
        }
    }

    /**
     * Transform your actual domain models to home screen UI state
     */
    private fun transformToHomeState(
        notes: List<Note>,
        links: List<Link>,
        files: List<File>,
        routines: List<ClassRoutine>
    ): HomeState {
        // Calculate statistics from real data
        val todaysNotesCount = calculateTodaysNotesCount(notes)
        val recentActivityCount = calculateRecentActivityCount(notes, links, files)

        // Create categories with REAL counts from your data
        val categories = createCategories(notes, links, files, routines)

        // Create recent items from actual data
        val recentItems = createRecentItems(notes, links, files)

        // Create continue editing notes
        val continueEditingNotes = createContinueEditingNotes(notes)

        // Extract trending tags from notes
        val trendingTags = extractTrendingTags(notes)

        return HomeState(
            userName = "Nehal", // You can make this dynamic later
            currentTime = getTimeBasedGreeting(),
            isLoading = false,
            todaysNotesCount = todaysNotesCount,
            recentActivityCount = recentActivityCount,
            totalNotesCount = notes.size,
            writingStreak = calculateWritingStreak(notes),
            weeklyProgress = calculateWeeklyProgress(notes),

            // Your actual domain data
            notes = notes,
            links = links,
            files = files,
            routines = routines,

            // UI data derived from domain data
            categories = categories,
            quickActions = createQuickActions(),
            continueEditingNotes = continueEditingNotes,
            trendingTags = trendingTags,
            recentItems = recentItems
        )
    }

    /**
     * Calculate notes created today based on your Note model
     */
    private fun calculateTodaysNotesCount(notes: List<Note>): Int {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return notes.count { note ->
            note.createdAt.toEpochMilliseconds() >= today
        }
    }

    /**
     * Calculate recent activity (last 24 hours)
     */
    private fun calculateRecentActivityCount(
        notes: List<Note>,
        links: List<Link>,
        files: List<File>
    ): Int {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis

        val recentNotes = notes.count {
            it.updatedAt.toEpochMilliseconds() >= yesterday
        }
        val recentLinks = links.count {
            it.updatedAt.toEpochMilliseconds() >= yesterday
        }
        val recentFiles = files.count {
            it.updatedAt.toEpochMilliseconds() >= yesterday
        }

        return recentNotes + recentLinks + recentFiles
    }

    /**
     * Create categories with real counts from your data
     */
    private fun createCategories(
        notes: List<Note>,
        links: List<Link>,
        files: List<File>,
        routines: List<ClassRoutine>
    ): List<Category> {
        return listOf(
            Category("notes", "Notes", "📝", Color(0xFF4CAF50), notes.size),
            Category("links", "Links", "🔗", Color(0xFF2196F3), links.size),
            Category("files", "Files", "📁", Color(0xFFFF9800), files.size),
            Category("routines", "Routines", "⏰", Color(0xFF9C27B0), routines.size),
            Category("archive", "Archive", "📦", Color(0xFF795548),
                notes.count { it.isArchived } + links.count { it.isArchived } + files.count { it.isArchived }
            ),
            Category("favorites", "Favorites", "⭐", Color(0xFFFFC107),
                // You can add favorite functionality later
                0
            )
        )
    }

    /**
     * Create recent items from your actual data
     */
    private fun createRecentItems(
        notes: List<Note>,
        links: List<Link>,
        files: List<File>
    ): List<RecentItem> {
        val allItems = mutableListOf<RecentItem>()

        // Add recent notes (last 2)
        notes.take(2).forEach { note ->
            allItems.add(
                RecentItem(
                    id = note.id.toString(),
                    type = "note",
                    title = note.title,
                    subtitle = note.content.take(50) + if (note.content.length > 50) "..." else "",
                    timestamp = formatTimestamp(note.updatedAt.toEpochMilliseconds()),
                    icon = "📔"
                )
            )
        }

        // Add recent links (last 1)
        links.take(1).forEach { link ->
            allItems.add(
                RecentItem(
                    id = link.id.toString(),
                    type = "link",
                    title = link.title ?: link.domain,
                    subtitle = link.domain,
                    timestamp = formatTimestamp(link.updatedAt.toEpochMilliseconds()),
                    icon = "🔗"
                )
            )
        }

        // Add recent files (last 1)
        files.take(1).forEach { file ->
            allItems.add(
                RecentItem(
                    id = file.id.toString(),
                    type = "file",
                    title = file.fileName,
                    subtitle = "${file.fileType} • ${formatFileSize(file.fileSize)}",
                    timestamp = formatTimestamp(file.updatedAt.toEpochMilliseconds()),
                    icon = getFileIcon(file.fileType)
                )
            )
        }

        // Sort by timestamp (newest first) and take top 4
        return allItems.sortedByDescending {
            when (it.type) {
                "note" -> notes.find { note -> note.id.toString() == it.id }?.updatedAt?.toEpochMilliseconds() ?: 0
                "link" -> links.find { link -> link.id.toString() == it.id }?.updatedAt?.toEpochMilliseconds() ?: 0
                "file" -> files.find { file -> file.id.toString() == it.id }?.updatedAt?.toEpochMilliseconds() ?: 0
                else -> 0
            }
        }.take(4)
    }

    /**
     * Create continue editing notes from recent notes
     */
    private fun createContinueEditingNotes(notes: List<Note>): List<NoteItem> {
        return notes.sortedByDescending { it.updatedAt.toEpochMilliseconds() }
            .take(3)
            .map { note ->
                NoteItem(
                    id = note.id.toString(),
                    title = note.title,
                    preview = note.content.take(80) + if (note.content.length > 80) "..." else "",
                    lastEdited = formatTimestamp(note.updatedAt.toEpochMilliseconds()),
                    wordCount = note.content.split("\\s+".toRegex()).size
                )
            }
    }

    /**
     * Extract trending tags from notes
     */
    private fun extractTrendingTags(notes: List<Note>): List<String> {
        val allTags = notes.flatMap { it.tags }
        return allTags.groupBy { it }
            .mapValues { it.value.size }
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { "#${it.key}" }
    }

    /**
     * Create quick actions
     */
    private fun createQuickActions(): List<QuickAction> {
        return listOf(
            QuickAction("quick_note", "Quick Note", "Capture thoughts instantly", "✏️", Color(0xFF4CAF50)),
            QuickAction("voice_note", "Voice Note", "Speak your thoughts", "🎤", Color(0xFF2196F3)),
            QuickAction("photo_note", "Photo Note", "Scan documents", "📷", Color(0xFFFF9800)),
            QuickAction("template", "Templates", "Use pre-made formats", "🎨", Color(0xFF9C27B0))
        )
    }

    /**
     * Calculate writing streak (simplified)
     */
    private fun calculateWritingStreak(notes: List<Note>): Int {
        // Simplified implementation - you can enhance this later
        return if (notes.isNotEmpty()) 7 else 0
    }

    /**
     * Calculate weekly progress
     */
    private fun calculateWeeklyProgress(notes: List<Note>): WeeklyProgress {
        // Simplified implementation
        return WeeklyProgress(
            currentWeekNotes = notes.size / 2,
            lastWeekNotes = notes.size / 4,
            progressPercentage = 25
        )
    }

    // Helper functions
    private fun getTimeBasedGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Morning"
            hour < 17 -> "Afternoon"
            else -> "Evening"
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} min ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
            else -> "${diff / (24 * 60 * 60 * 1000)} days ago"
        }
    }

    private fun formatFileSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            else -> "${size / (1024 * 1024)} MB"
        }
    }

    private fun getFileIcon(fileType: String): String {
        return when (fileType.lowercase()) {
            "pdf" -> "📄"
            "image" -> "🖼️"
            "document" -> "📝"
            "video" -> "🎥"
            "audio" -> "🎵"
            else -> "📎"
        }
    }

    /**
     * Refresh data
     */
    fun refreshData() {
        loadAllContent()
    }
}