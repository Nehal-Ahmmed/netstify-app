package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SearchBarPill
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components.CreateFolderDialog
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components.CreateLinkDialog
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.data.LinksViewmodel
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun LinksListScreen(navController: NavController) {
    val viewModel: LinksViewmodel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val c = NestifyTheme.colors

    var selectedFolderId by remember { mutableStateOf<Long?>(null) }
    var showCreateLinkDialog by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredLinks = state.links.filter {
        (selectedFolderId == null || it.folderId == selectedFolderId) &&
        (it.title?.contains(searchQuery, ignoreCase = true) == true ||
         it.url.contains(searchQuery, ignoreCase = true))
    }

    if (showCreateLinkDialog) {
        CreateLinkDialog(
            folders = state.folders,
            onDismiss = { showCreateLinkDialog = false },
            onCreateLink = { title, desc, url, folderId ->
                viewModel.createLink(title, desc, url, folderId)
                showCreateLinkDialog = false
            }
        )
    }

    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = { showCreateFolderDialog = false },
            onCreateFolder = { name, color ->
                viewModel.createFolder(name, color)
                showCreateFolderDialog = false
            }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = "Smart Links",
            subtitle = "Your digital collection",
            onBack = { navController.popBackStack() },
            trailing = {
                IconButtonChrome(
                    Icons.Default.Add,
                    onClick = { showCreateLinkDialog = true },
                    tint = c.brand,
                    contentDescription = "Add Link",
                )
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Space.screen,
                end = Space.screen,
                top = Space.l,
                bottom = GlassNavSpace,
            ),
            verticalArrangement = Arrangement.spacedBy(Space.m),
        ) {
            item(key = "search") {
                SearchBarPill(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search your links…",
                )
            }

            // Folders Section
            item(key = "folders") {
                SectionHead(
                    title = "Categories",
                    actionText = "+ New Folder",
                    onAction = { showCreateFolderDialog = true },
                )
                Spacer(Modifier.height(Space.m))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Space.s)) {
                    item {
                        Chip(
                            label = "All Links",
                            tone = if (selectedFolderId == null) ChipTone.Default else ChipTone.Ghost,
                            active = selectedFolderId == null,
                            onClick = { selectedFolderId = null },
                        )
                    }
                    items(state.folders) { folder ->
                        Chip(
                            label = folder.name,
                            tone = if (selectedFolderId == folder.id) ChipTone.Default else ChipTone.Ghost,
                            active = selectedFolderId == folder.id,
                            onClick = { selectedFolderId = folder.id },
                        )
                    }
                }
            }

            // Links Section
            item(key = "collectionHead") {
                Spacer(Modifier.height(Space.s))
                SectionHead(title = "Your Collection", kicker = "Saved")
            }

            if (filteredLinks.isEmpty()) {
                item(key = "empty") {
                    Spacer(Modifier.height(Space.l))
                    EmptyState(
                        icon = Icons.Outlined.Inbox,
                        title = "Your collection is empty",
                        description = "Save a link to start building your digital library.",
                        primaryLabel = "Add a link",
                        onPrimary = { showCreateLinkDialog = true },
                    )
                }
            }

            items(filteredLinks, key = { it.id }) { link ->
                ModernLinkCard(
                    link = link,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                        context.startActivity(intent)
                    },
                    onDelete = { viewModel.deleteLinkById(link.id) }
                )
            }
        }
    }
}

@Composable
fun ModernLinkCard(link: Link, onClick: () -> Unit, onDelete: () -> Unit) {
    val c = NestifyTheme.colors
    var showMenu by remember { mutableStateOf(false) }

    NestifyCard(modifier = Modifier.fillMaxWidth(), padding = Space.m, onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.m),
        ) {
            // Preview thumbnail or icon tile
            if (link.previewImageUrl != null) {
                AsyncImage(
                    model = link.previewImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(Radii.m),
                    contentScale = ContentScale.Crop,
                )
            } else {
                IconTile(icon = Icons.Outlined.Link, size = 56.dp, corner = 14.dp)
            }

            Column(Modifier.weight(1f)) {
                OneLine(
                    text = link.title ?: link.url,
                    style = NestifyTheme.type.h3Serif,
                    color = c.ink,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = link.domain,
                    style = NestifyTheme.type.meta,
                    color = c.brand,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!link.description.isNullOrBlank()) {
                    Spacer(Modifier.height(Space.xs))
                    Text(
                        text = link.description ?: "",
                        style = NestifyTheme.type.body,
                        color = c.ink50,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Box {
                IconButtonChrome(
                    Icons.Outlined.MoreVert,
                    onClick = { showMenu = true },
                    tint = c.ink50,
                    contentDescription = "More",
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = c.surface,
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete", style = NestifyTheme.type.body, color = c.coral) },
                        onClick = { onDelete(); showMenu = false },
                        leadingIcon = { Icon(Icons.Outlined.DeleteOutline, null, tint = c.coral) },
                    )
                }
            }
        }
    }
}
