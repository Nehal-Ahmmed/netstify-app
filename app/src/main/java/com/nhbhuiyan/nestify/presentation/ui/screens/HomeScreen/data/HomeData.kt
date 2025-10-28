package com.nhbhuiyan.nestify.presentation.ui.screens.home.data

import androidx.compose.ui.graphics.Color
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.model.ClassRoutine

/**
 * Data classes for Home Screen state management
 * Updated to work with your actual domain models
 */

// Main state data class
data class HomeState(
    val userName: String = "Nehal",
    val currentTime: String = "Morning",
    val isLoading: Boolean = false,
    val todaysNotesCount: Int = 0,
    val recentActivityCount: Int = 0,
    val totalNotesCount: Int = 0,
    val writingStreak: Int = 0,
    val weeklyProgress: WeeklyProgress = WeeklyProgress(),

    // Real data from your domain models
    val notes: List<Note> = emptyList(),
    val links: List<Link> = emptyList(),
    val files: List<File> = emptyList(),
    val routines: List<ClassRoutine> = emptyList(),

    // UI specific data derived from real data
    val categories: List<Category> = emptyList(),
    val quickActions: List<QuickAction> = emptyList(),
    val continueEditingNotes: List<NoteItem> = emptyList(),
    val trendingTags: List<String> = emptyList(),
    val recentItems: List<RecentItem> = emptyList()
)

// Weekly progress data
data class WeeklyProgress(
    val currentWeekNotes: Int = 0,
    val lastWeekNotes: Int = 0,
    val progressPercentage: Int = 0
)

// Category data for the grid - using your actual content types
data class Category(
    val id: String,
    val title: String,
    val icon: String,
    val color: Color,
    val count: Int = 0
)

// Quick action data
data class QuickAction(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val color: Color
)

// Note item for UI display
data class NoteItem(
    val id: String,
    val title: String,
    val preview: String,
    val lastEdited: String,
    val wordCount: Int = 0
)

// Recent item for UI display
data class RecentItem(
    val id: String,
    val type: String, // "note", "link", "file", "routine"
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val icon: String
)