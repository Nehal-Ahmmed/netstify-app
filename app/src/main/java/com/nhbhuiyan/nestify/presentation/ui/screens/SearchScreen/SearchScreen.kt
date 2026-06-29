package com.nhbhuiyan.nestify.presentation.ui.screens.SearchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SearchBarPill
import com.nhbhuiyan.nestify.presentation.ui.screens.ArchiveScreen.navigateToDetail
import com.nhbhuiyan.nestify.presentation.ui.screens.SearchScreen.data.SearchViewModel
import com.nhbhuiyan.nestify.presentation.ui.screens.common.FeedRow
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val c = NestifyTheme.colors
    val query by viewModel.query.collectAsState()
    val state by viewModel.state.collectAsState()

    val scopes = listOf("All", "Notes", "Links", "Files")
    var selected by remember { mutableIntStateOf(0) }
    val typeFor = mapOf(1 to "note", 2 to "link", 3 to "file")
    val visible = remember(state.results, selected) {
        if (selected == 0) state.results else state.results.filter { it.type == typeFor[selected] }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(title = "Search", onBack = { navController.popBackStack() })

        Column(Modifier.padding(horizontal = Space.screen)) {
            Spacer(Modifier.height(Space.m))
            SearchBarPill(
                value = query,
                onValueChange = viewModel::onQueryChange,
                placeholder = "Search notes, links and files…",
            )
            Spacer(Modifier.height(Space.m))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Space.s),
            ) {
                scopes.forEachIndexed { i, label ->
                    Chip(label = label, tone = ChipTone.Default, active = i == selected, onClick = { selected = i })
                }
            }
        }

        if (visible.isEmpty()) {
            Spacer(Modifier.height(Space.xxl))
            EmptyState(
                icon = Icons.Outlined.Search,
                title = if (query.isBlank()) "Start typing to search" else "No results",
                description = if (query.isBlank())
                    "Find notes, links and files across your whole workspace."
                else
                    "Nothing matched \"$query\". Try a different keyword or scope.",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Space.screen,
                    end = Space.screen,
                    top = Space.l,
                    bottom = Space.xl,
                ),
                verticalArrangement = Arrangement.spacedBy(Space.m),
            ) {
                items(items = visible, key = { it.type + it.id }) { item ->
                    FeedRow(item = item, onClick = { navigateToDetail(navController, item.type, item.id) })
                }
            }
        }
    }
}
