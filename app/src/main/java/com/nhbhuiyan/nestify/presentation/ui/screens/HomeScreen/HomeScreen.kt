package com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.screens.home.components.CategoriesSection
import com.nhbhuiyan.nestify.presentation.ui.screens.home.components.HeaderSection
import com.nhbhuiyan.nestify.presentation.ui.screens.home.components.ProductivityInsightsSection
import com.nhbhuiyan.nestify.presentation.ui.screens.home.components.QuickActionsSection
import com.nhbhuiyan.nestify.presentation.ui.screens.home.components.QuickStatsSection
import com.nhbhuiyan.nestify.presentation.ui.screens.home.components.RecentItemsSection
import com.nhbhuiyan.nestify.presentation.ui.screens.home.components.SmartSuggestionsSection
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.data.HomeViewModel

/**
 * Main Home Screen for Nestify - Personal Note Taking App
 *
 * This screen serves as the dashboard showing:
 * - Personalized greeting and quick stats
 * - Categories with real counts from database
 * - Quick actions for productivity
 * - Smart suggestions and recent items
 * - Productivity insights and writing streak
 *
 * Uses real data from ViewModel which combines notes, links, files, and routines
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeState by viewModel.homeState.collectAsState()
    val context = LocalContext.current


    // Show loading state
    if (homeState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFD)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text("Loading your notes...", modifier = Modifier.padding(top = 16.dp))
            }
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Route.createNote.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Create New Note")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFD)),
            contentPadding = paddingValues,
            state = rememberLazyListState(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // All your existing sections remain the same
            // They will now show REAL data from your database
            item {
                HeaderSection(
                    userName = homeState.userName,
                    currentTime = homeState.currentTime,
                    onSearchClicked = { navController.navigate(Route.Search.route) }
                )
            }

            item {
                QuickStatsSection(
                    todaysNotesCount = homeState.todaysNotesCount,
                    recentActivityCount = homeState.recentActivityCount,
                    onStatsClicked = { Toast.makeText(context,"navigate to stats page",Toast.LENGTH_SHORT).show() }
                )
            }

            item {
                CategoriesSection(
                    categories = homeState.categories,
                    onCategoryClicked = { category ->
                        when (category.id) {
                            "notes" -> navController.navigate(Route.Notes.route)
                            "links" -> navController.navigate(Route.Links.route)
                            "files" -> navController.navigate(Route.Files.route)
                            "routines" -> navController.navigate(Route.Routines.route)
                            "archive" -> navController.navigate(Route.Archive.route)
                            "favorites" -> navController.navigate(Route.Favorites.route)
                        }
                    }
                )
            }

            // ... rest of your sections remain the same
            item {
                QuickActionsSection(
                    quickActions = homeState.quickActions,
                    onQuickActionClicked = { action ->
//                        when (action.id) {
//                            "quick_note" -> navController.navigate("create_note?type=quick")
//                            "voice_note" -> navController.navigate("voice_note")
//                            "photo_note" -> navController.navigate("camera_capture")
//                            "template" -> navController.navigate("templates")
//                        }
                    }
                )
            }

            item {
                SmartSuggestionsSection(
                    continueEditingNotes = homeState.continueEditingNotes,
                    trendingTags = homeState.trendingTags,
                    onNoteClicked = { noteId -> navController.navigate("note/$noteId") },
                    onTagClicked = { tag -> navController.navigate("search?query=$tag") }
                )
            }

            item {
                RecentItemsSection(
                    recentItems = homeState.recentItems,
                    onItemClicked = { item ->
//                        when (item.type) {
//                            "note" -> navController.navigate("note/${item.id}")
//                            "link" -> navController.navigate("link/${item.id}")
//                            "file" -> navController.navigate("file/${item.id}")
//                            "routine" -> navController.navigate("routine/${item.id}")
//                        }
                    }
                )
            }

            item {
                ProductivityInsightsSection(
                    writingStreak = homeState.writingStreak,
                    weeklyProgress = homeState.weeklyProgress,
                    totalNotesCount = homeState.totalNotesCount,
                    onInsightsClicked = { Toast.makeText(context,"navigate to insights page",Toast.LENGTH_SHORT).show() }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}