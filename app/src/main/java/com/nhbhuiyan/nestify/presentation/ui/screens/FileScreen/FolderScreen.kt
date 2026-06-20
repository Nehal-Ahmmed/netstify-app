package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.FileFolder
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components.fileFolderScreenState
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.data.FileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    navController: NavController
) {
    val viewmodel: FileViewModel = hiltViewModel()
    val state by viewmodel.folderUiState.collectAsState()

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "📁 All Folders", style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showCreateFolderDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.CreateNewFolder, contentDescription = "Create Folder")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            state.isLoading -> {
                LoadingShimmer()
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
                    onCreateFolderClick = {category->
                        selectedCategoryForCreation = category
                        showCreateFolderDialog = true
                    },
                    modifier = Modifier.padding(innerPadding)
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
            modifier: Modifier
        ) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                // PDFs Section
                if (state.pdfFolders.isNotEmpty()) {
                    item {
                        CategorySection(
                            title = "📑 PDFs",
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
                            title = "🖼️ Photos",
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
                            title = "📄 Documents",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${folders.size} folder${if (folders.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
            Card(
                onClick = onClick,
                modifier = Modifier.width(140.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(folder.color).copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(folder.color).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = folder.icon,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    // Folder Name
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        @Composable
        fun AddHorizontalFolderCard(categoryColor: Long, onClick: () -> Unit) {
            Card(
                onClick = onClick,
                modifier = Modifier.width(140.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(categoryColor).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add folder",
                            tint = Color(categoryColor),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Add Text
                    Text(
                        text = "Add Folder",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Show "Add PDF Folders" section if no PDF folders exist
                if (!hasPdfs) {
                    AddCategoryCard(
                        title = "📑 Add PDF Folders",
                        description = "Organize your PDF files",
                        categoryColor = 0xFF4CAF50,
                        onClick = { onCreateFolderClick("pdf") }
                    )
                }

                // Show "Add Photo Folders" section if no photo folders exist
                if (!hasPhotos) {
                    AddCategoryCard(
                        title = "🖼️ Add Photo Folders",
                        description = "Organize your images",
                        categoryColor = 0xFF2196F3,
                        onClick = { onCreateFolderClick("photo") }
                    )
                }

                // Show "Add Document Folders" section if no document folders exist
                if (!hasDocuments) {
                    AddCategoryCard(
                        title = "📄 Add Document Folders",
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
            Card(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(categoryColor).copy(alpha = 0.05f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(categoryColor).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color(categoryColor),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Text Content
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        @Composable
        fun EmptyFoldersState(
            onCreateFolder: () -> Unit
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📁",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "No Folders Created Yet",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Create folders to organize your files by category. Each category will appear here with its own horizontal list.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = onCreateFolder,
                    modifier = Modifier.height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Your First Folder")
                }
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
            var folderName by remember { mutableStateOf("New Folder") }
            var selectedCategory by remember { mutableStateOf(initialCategory) }
            var selectedIcon by remember { mutableStateOf("📁") }
            val categories = listOf(
                "pdf" to "📑 PDFs",
                "photo" to "🖼️ Photos",
                "document" to "📄 Documents"
            )
            val icons = listOf(
                "📁", "📑", "🖼️", "📄", "📚", "🎓", "📜", "🏠", "💼",
                "⭐", "🔒", "🎨", "📝", "📊", "🎯", "💰", "❤️", "✨"
            )

            AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                    Text(
                        text = "Create New Folder",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Folder Name
                        OutlinedTextField(
                            value = folderName,
                            onValueChange = { folderName = it },
                            label = { Text("Folder Name") },
                            placeholder = { Text("e.g., Class Notes, Certificates, etc.") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Category Selection
                        Column {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                categories.forEach { (category, label) ->
                                    CategoryChip(
                                        label = label,
                                        isSelected = selectedCategory == category,
                                        color = getCategoryColor(category),
                                        onClick = { selectedCategory = category },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Icon Selection
                        Column {
                            Text(
                                text = "Folder Icon",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Selected Icon Preview
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedIcon,
                                    style = MaterialTheme.typography.displaySmall
                                )
                            }

                            // Icon Grid
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    Button(
                        onClick = {
                            if (folderName.isNotBlank()) {
                                onCreateFolder(folderName, selectedCategory, selectedIcon)
                            }
                        },
                        enabled = folderName.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Folder")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        @Composable
        fun CategoryChip(
            label: String,
            isSelected: Boolean,
            color: Int,
            onClick: () -> Unit,
            modifier: Modifier = Modifier
        ) {
            FilterChip(
                selected = isSelected,
                onClick = onClick,
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(color).copy(alpha = 0.1f),
                    selectedLabelColor = Color(color),
                    selectedLeadingIconColor = Color(color)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = isSelected,
                    selected = isSelected,
                    borderColor = if (isSelected) Color(color) else Color.Transparent
                ),
                modifier = modifier
            )
        }

        @Composable
        fun IconChip(
            icon: String,
            isSelected: Boolean,
            onClick: () -> Unit
        ) {
            FilterChip(
                selected = isSelected,
                onClick = onClick,
                label = { Text(icon) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
                modifier = Modifier.size(48.dp)
            )
        }


