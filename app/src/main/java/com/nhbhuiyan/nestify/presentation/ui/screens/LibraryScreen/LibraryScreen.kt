package com.nhbhuiyan.nestify.presentation.ui.screens.LibraryScreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhbhuiyan.nestify.presentation.ui.screens.LibraryScreen.viewmodel.LibraryViewModel
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemStatus
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemType
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ProgressBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val libraryItems by viewModel.libraryItems.collectAsState()
    val c = NestifyTheme.colors

    val currentlyReading = libraryItems.filter { it.status == LibraryItemStatus.READING }
    val catalog = libraryItems.filter { it.status != LibraryItemStatus.READING }

    val categories = listOf("All", "Books", "Documents", "Research", "Drafts")
    var selectedCategory by remember { mutableIntStateOf(0) }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = "My Library",
            trailing = {
                IconButtonChrome(Icons.Outlined.Search, onClick = { /* search */ }, contentDescription = "Search")
                IconButtonChrome(
                    Icons.Outlined.Add,
                    onClick = {
                        viewModel.addItem(
                            LibraryItemEntity(
                                title = "Professional Android Dev",
                                author = "Reto Meier",
                                linkOrFilePath = "",
                                status = LibraryItemStatus.READING,
                                itemType = LibraryItemType.BOOK
                            )
                        )
                    },
                    tint = c.brand,
                    contentDescription = "Add Library Item",
                )
            },
        )

        if (libraryItems.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.MenuBook,
                title = "Your library is empty",
                description = "Add books, documents and research to build your personal collection.",
                primaryLabel = "Add an item",
                onPrimary = {
                    viewModel.addItem(
                        LibraryItemEntity(
                            title = "Professional Android Dev",
                            author = "Reto Meier",
                            linkOrFilePath = "",
                            status = LibraryItemStatus.READING,
                            itemType = LibraryItemType.BOOK
                        )
                    )
                },
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Space.screen,
                end = Space.screen,
                top = Space.m,
                bottom = GlassNavSpace,
            ),
            verticalArrangement = Arrangement.spacedBy(Space.l),
        ) {
            // Category filter row
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Space.s),
                ) {
                    categories.forEachIndexed { i, label ->
                        Chip(
                            label = label,
                            tone = ChipTone.Default,
                            active = i == selectedCategory,
                            onClick = { selectedCategory = i },
                        )
                    }
                }
            }

            if (currentlyReading.isNotEmpty()) {
                item { SectionHead(title = "Currently reading", kicker = "In progress") }
                items(currentlyReading.take(1)) { book -> FeaturedBookCard(book) }
            }

            if (catalog.isNotEmpty()) {
                item { SectionHead(title = "Your catalog", kicker = "Collection") }
                items(catalog.chunked(2)) { rowItems ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Space.l),
                    ) {
                        CatalogBookItem(Modifier.weight(1f), rowItems[0].title, rowItems[0].itemType.name)
                        if (rowItems.size > 1) {
                            CatalogBookItem(Modifier.weight(1f), rowItems[1].title, rowItems[1].itemType.name)
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedBookCard(book: LibraryItemEntity) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.l)) {
            IconTile(Icons.Outlined.MenuBook, size = 72.dp, corner = 18.dp)
            Column(Modifier.weight(1f)) {
                Text(
                    book.title,
                    style = NestifyTheme.type.h3Serif,
                    color = c.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                OneLine(
                    "By ${book.author ?: "Unknown"}",
                    style = NestifyTheme.type.meta,
                    color = c.ink50,
                )
                Spacer(Modifier.height(Space.m))
                ProgressBar(value = 0.65f)
                Spacer(Modifier.height(Space.xs))
                Kicker("Reading in progress", color = c.brand)
            }
        }
    }
}

@Composable
fun CatalogBookItem(modifier: Modifier, title: String, category: String) {
    val c = NestifyTheme.colors
    Column(modifier) {
        NestifyCard(modifier = Modifier.fillMaxWidth().height(132.dp)) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                IconTile(Icons.Outlined.MenuBook, size = 48.dp)
            }
        }
        Spacer(Modifier.height(Space.s))
        OneLine(title, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
        Kicker(category)
    }
}
