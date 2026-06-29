package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.FileFolder
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyInput
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components.fileFolderScreenState
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.data.FileViewModel
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun FolderScreen(
    navController: NavController
) {
    val viewmodel: FileViewModel = hiltViewModel()
    val state by viewmodel.folderUiState.collectAsState()
    val c = NestifyTheme.colors

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var selectedCategoryForCreation by remember { mutableStateOf("pdf") }

    if (showCreateFolderDialog) {
        CreateFolderDialog(
            initialCategory = selectedCategoryForCreation,
            onDismiss = { showCreateFolderDialog = false },
            onCreateFolder = { folderName, categoryType, icon ->
                viewmodel.createFolder(
                    category = categoryType,
                    name = folderName,
                    isCustom = true,
                    color = getCategoryColor(categoryType),
                    icon = icon
                )
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
            title = "All Folders",
            onBack = { navController.popBackStack() },
            trailing = {
                IconButtonChrome(
                    Icons.Default.CreateNewFolder,
                    onClick = { showCreateFolderDialog = true },
                    tint = c.brand,
                    contentDescription = "Create Folder",
                )
            },
        )

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = c.brand)
                }
            }

            state.pdfFolders.isEmpty() && state.photoFolders.isEmpty() && state.documentFolders.isEmpty() -> {
                EmptyFoldersState(
                    onCreateFolder = {
                        selectedCategoryForCreation = "pdf"
                        showCreateFolderDialog = true
                    }
                )
            }

            else -> {
                AllCategoriesContent(
                    state = state,
                    onFolderClick = { folder ->
                        Log.d("folderScreen", "📁 Folder clicked: ${folder.id}")
                        navController.navigate(route = Route.Files.createFolder(folderId = folder.id))
                    },
                    onCreateFolderClick = { category ->
                        selectedCategoryForCreation = category
                        showCreateFolderDialog = true
                    },
                )
            }
        }
    }
}

@Composable
fun AllCategoriesContent(
    state: fileFolderScreenState,
    onFolderClick: (FileFolder) -> Unit,
    onCreateFolderClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Space.xxl),
        contentPadding = PaddingValues(
            start = Space.screen,
            end = Space.screen,
            top = Space.l,
            bottom = GlassNavSpace,
        ),
    ) {
        // PDFs Section
        if (state.pdfFolders.isNotEmpty()) {
            item {
                CategorySection(
                    title = "PDFs",
                    folders = state.pdfFolders,
                    categoryColor = 0xFF4CAF50,
                    onFolderClick = onFolderClick,
                    onCreateFolderClick = { onCreateFolderClick("pdf") }
                )
            }
        }

        // Photos Section
        if (state.photoFolders.isNotEmpty()) {
            item {
                CategorySection(
                    title = "Photos",
                    folders = state.photoFolders,
                    categoryColor = 0xFF2196F3,
                    onFolderClick = onFolderClick,
                    onCreateFolderClick = { onCreateFolderClick("photo") }
                )
            }
        }

        // Documents Section
        if (state.documentFolders.isNotEmpty()) {
            item {
                CategorySection(
                    title = "Documents",
                    folders = state.documentFolders,
                    categoryColor = 0xFFFF9800,
                    onFolderClick = onFolderClick,
                    onCreateFolderClick = { onCreateFolderClick("document") }
                )
            }
        }

        // Empty category sections (for adding new folders)
        item {
            AddCategorySections(
                hasPdfs = state.pdfFolders.isNotEmpty(),
                hasPhotos = state.photoFolders.isNotEmpty(),
                hasDocuments = state.documentFolders.isNotEmpty(),
                onCreateFolderClick = onCreateFolderClick
            )
        }
    }
}

@Composable
fun CategorySection(
    title: String,
    folders: List<FileFolder>,
    categoryColor: Long,
    onFolderClick: (FileFolder) -> Unit,
    onCreateFolderClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Space.m)
    ) {
        SectionHead(
            title = title,
            kicker = "${folders.size} folder${if (folders.size > 1) "s" else ""}",
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Space.m)
        ) {
            items(folders, key = { it.id }) { folder ->
                HorizontalFolderCard(
                    folder = folder,
                    onClick = { onFolderClick(folder) }
                )
            }
            item {
                AddHorizontalFolderCard(
                    categoryColor = categoryColor,
                    onClick = onCreateFolderClick
                )
            }
        }
    }
}

@Composable
fun HorizontalFolderCard(folder: FileFolder, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    val accent = Color(folder.color)
    NestifyCard(
        modifier = Modifier.width(140.dp),
        padding = Space.l,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Space.m)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = folder.icon, style = NestifyTheme.type.h2Serif)
            }
            Text(
                text = folder.name,
                style = NestifyTheme.type.label,
                color = c.ink,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AddHorizontalFolderCard(categoryColor: Long, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    val accent = Color(categoryColor)
    Column(
        modifier = Modifier
            .width(140.dp)
            .clip(Radii.l)
            .background(c.surface2)
            .border(1.dp, c.hair2, Radii.l)
            .clickable(onClick = onClick)
            .padding(Space.l),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Space.m)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add folder",
                tint = accent,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "Add Folder",
            style = NestifyTheme.type.label,
            textAlign = TextAlign.Center,
            color = c.ink50
        )
    }
}

@Composable
fun AddCategorySections(
    hasPdfs: Boolean,
    hasPhotos: Boolean,
    hasDocuments: Boolean,
    onCreateFolderClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Space.l)
    ) {
        if (!hasPdfs) {
            AddCategoryCard(
                title = "Add PDF Folders",
                description = "Organize your PDF files",
                categoryColor = 0xFF4CAF50,
                onClick = { onCreateFolderClick("pdf") }
            )
        }
        if (!hasPhotos) {
            AddCategoryCard(
                title = "Add Photo Folders",
                description = "Organize your images",
                categoryColor = 0xFF2196F3,
                onClick = { onCreateFolderClick("photo") }
            )
        }
        if (!hasDocuments) {
            AddCategoryCard(
                title = "Add Document Folders",
                description = "Organize your documents",
                categoryColor = 0xFFFF9800,
                onClick = { onCreateFolderClick("document") }
            )
        }
    }
}

@Composable
fun AddCategoryCard(
    title: String,
    description: String,
    categoryColor: Long,
    onClick: () -> Unit
) {
    val c = NestifyTheme.colors
    val accent = Color(categoryColor)
    NestifyCard(
        modifier = Modifier.fillMaxWidth(),
        padding = Space.l,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.l)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(title, style = NestifyTheme.type.h3Serif, color = c.ink)
                Kicker(description)
            }
        }
    }
}

@Composable
fun EmptyFoldersState(
    onCreateFolder: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        EmptyState(
            icon = Icons.Outlined.Folder,
            title = "No Folders Created Yet",
            description = "Create folders to organize your files by category. Each category appears here with its own horizontal list.",
            primaryLabel = "Create Your First Folder",
            onPrimary = onCreateFolder,
        )
    }
}

private fun getCategoryColor(category: String): Int {
    return when (category.lowercase()) {
        "pdf" -> 0xFF4CAF50   // Green
        "photo" -> 0xFF2196F3  // Blue
        "document" -> 0xFFFF9800 // Orange
        else -> 0xFF9C27B0     // Purple
    }.toInt()
}

@Composable
fun CreateFolderDialog(
    initialCategory: String,
    onDismiss: () -> Unit,
    onCreateFolder: (String, String, String) -> Unit
) {
    val c = NestifyTheme.colors
    var folderName by remember { mutableStateOf("New Folder") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedIcon by remember { mutableStateOf("📁") }
    val categories = listOf(
        "pdf" to "PDFs",
        "photo" to "Photos",
        "document" to "Documents"
    )
    val icons = listOf(
        "📁", "📑", "🖼️", "📄", "📚", "🎓", "📜", "🏠", "💼",
        "⭐", "🔒", "🎨", "📝", "📊", "🎯", "💰", "❤️", "✨"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.surface,
        shape = Radii.xl,
        title = {
            Text(
                text = "Create New Folder",
                style = NestifyTheme.type.h2Serif,
                color = c.ink,
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Space.l)
            ) {
                NestifyInput(
                    value = folderName,
                    onValueChange = { folderName = it },
                    label = "Folder Name",
                    placeholder = "e.g., Class Notes, Certificates",
                    modifier = Modifier.fillMaxWidth(),
                )

                Column {
                    Text(
                        text = "Category",
                        style = NestifyTheme.type.label,
                        color = c.ink70,
                        modifier = Modifier.padding(bottom = Space.s)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Space.s),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { (category, label) ->
                            Chip(
                                label = label,
                                tone = if (selectedCategory == category) ChipTone.Brand else ChipTone.Ghost,
                                onClick = { selectedCategory = category },
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = "Folder Icon",
                        style = NestifyTheme.type.label,
                        color = c.ink70,
                        modifier = Modifier.padding(bottom = Space.s)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Space.m),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = selectedIcon, style = NestifyTheme.type.displaySerif)
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Space.s)
                    ) {
                        items(icons) { icon ->
                            IconChip(
                                icon = icon,
                                isSelected = selectedIcon == icon,
                                onClick = { selectedIcon = icon }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            NButton(
                label = "Create Folder",
                onClick = {
                    if (folderName.isNotBlank()) {
                        onCreateFolder(folderName, selectedCategory, selectedIcon)
                    }
                },
                full = true,
            )
        },
        dismissButton = {
            NButton(
                label = "Cancel",
                onClick = onDismiss,
                variant = BtnVariant.Ghost,
                full = true,
            )
        }
    )
}

@Composable
fun IconChip(
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val c = NestifyTheme.colors
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(Radii.m)
            .background(if (isSelected) c.brandSoft else c.surface2)
            .border(1.dp, if (isSelected) c.brand else c.hair2, Radii.m)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = icon, style = NestifyTheme.type.h3Serif)
    }
}
