package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.model.LinkFolder
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components.CreateFolderDialog
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components.CreateLinkDialog
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.data.LinksViewmodel
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinksListScreen(navController: NavController) {
    val viewModel: LinksViewmodel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
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

    Scaffold(
        containerColor = NestifySurface,
        topBar = {
            LinksHeader(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateLinkDialog = true },
                containerColor = NestifySlate,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Add Link")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Folders Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NestifySlate
                    )
                    TextButton(onClick = { showCreateFolderDialog = true }) {
                        Text("+ New Folder", style = MaterialTheme.typography.labelMedium)
                    }
                }
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        FolderPill(
                            name = "All Links",
                            isSelected = selectedFolderId == null,
                            onClick = { selectedFolderId = null }
                        )
                    }
                    items(state.folders) { folder ->
                        FolderPill(
                            name = folder.name,
                            isSelected = selectedFolderId == folder.id,
                            onClick = { selectedFolderId = folder.id }
                        )
                    }
                }
            }

            // Links Section
            item {
                Text(
                    "Your Collection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NestifySlate,
                    modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp)
                )
            }

            if (filteredLinks.isEmpty()) {
                item {
                    EmptyCollectionState()
                }
            }

            items(filteredLinks) { link ->
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
fun LinksHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(NestifyGradients.meshGradient())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                    Text(
                        "Smart Links",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Icon(Icons.Default.Link, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
            }
            
            // Modern Search Bar in Header
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search your links...", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) }
                )
            }
        }
    }
}

@Composable
fun FolderPill(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) NestifySlate else Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (isSelected) NestifySlate else Color.LightGray.copy(alpha = 0.4f)),
        modifier = Modifier.height(44.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
            Text(
                name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Gray
            )
        }
    }
}

@Composable
fun ModernLinkCard(link: Link, onClick: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column {
            // Preview Image or Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(getRandomGradient(link.url.length))
            ) {
                if (link.previewImageUrl != null) {
                    AsyncImage(
                        model = link.previewImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Link,
                        null,
                        modifier = Modifier.size(56.dp).align(Alignment.Center),
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                }
                
                // Floating Action Menu on Card
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape).size(32.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { onDelete(); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Delete, null) }
                        )
                    }
                }
            }

            // Link Info
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = link.title ?: link.url,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NestifySlate,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = link.domain,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = link.description ?: "Click to explore this resource on ${link.domain}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EmptyCollectionState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Inbox, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(Modifier.height(16.dp))
        Text("Your collection is empty", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    }
}

fun getRandomGradient(seed: Int): Brush {
    val gradients = listOf(
        Brush.linearGradient(listOf(Color(0xFF4A6572), Color(0xFFAEC4D1))),
        Brush.linearGradient(listOf(Color(0xFFE6D0BA), Color(0xFFC7DBE3))),
        Brush.linearGradient(listOf(Color(0xFF333F48), Color(0xFF4A6572))),
        Brush.linearGradient(listOf(Color(0xFFE6D0BA), Color(0xFF333F48))),
        Brush.linearGradient(listOf(Color(0xFFFDE8E9), Color(0xFFE6D0BA)))
    )
    return gradients[seed % gradients.size]
}
