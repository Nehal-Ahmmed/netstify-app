package com.nhbhuiyan.nestify.presentation.ui.screens.LibraryScreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhbhuiyan.nestify.presentation.ui.screens.LibraryScreen.viewmodel.LibraryViewModel
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemStatus
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemType
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val libraryItems by viewModel.libraryItems.collectAsState()
    
    val currentlyReading = libraryItems.filter { it.status == LibraryItemStatus.READING }
    val catalog = libraryItems.filter { it.status != LibraryItemStatus.READING }

    Scaffold(
        containerColor = NestifySurface,
        floatingActionButton = {
            FloatingActionButton(
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
                containerColor = NestifySkyBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Add Library Item")
            }
        },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(NestifyGradients.meshGradient())
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "My Library",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Search, null, tint = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(24.dp)
        ) {
            // Categories Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    items(listOf("All", "Books", "Documents", "Research", "Drafts")) { category ->
                        CategoryChip(category, category == "All")
                    }
                }
            }

            // Featured: Currently Reading
            if (currentlyReading.isNotEmpty()) {
                item {
                    Text(
                        "Currently Reading",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = NestifySlate
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    currentlyReading.take(1).forEach { book ->
                        FeaturedBookCard(book)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Catalog Section
            item {
                Text(
                    "Your Catalog",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = NestifySlate
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Catalog Grid (Chunked into rows of 2)
            val chunkedCatalog = catalog.chunked(2)
            items(chunkedCatalog) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CatalogBookItem(Modifier.weight(1f), rowItems[0].title, rowItems[0].itemType.name)
                    if (rowItems.size > 1) {
                        CatalogBookItem(Modifier.weight(1f), rowItems[1].title, rowItems[1].itemType.name)
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(label: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) NestifySlate else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NestifySlate else Color.LightGray.copy(alpha = 0.5f)),
        modifier = Modifier.height(36.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Gray
            )
        }
    }
}

@Composable
fun FeaturedBookCard(book: LibraryItemEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Cover Placeholder
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NestifySkyBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Book, null, tint = NestifySkyBlue, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NestifySlate,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    "By ${book.author ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                LinearProgressIndicator(
                    progress = 0.65f, // Placeholder for reading progress
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = NestifySkyBlue,
                    trackColor = NestifySkyBlue.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Reading in Progress", style = MaterialTheme.typography.labelSmall, color = NestifySkyBlue)
            }
        }
    }
}

@Composable
fun CatalogBookItem(modifier: Modifier, title: String, category: String) {
    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.MenuBook, null, tint = NestifySlate.copy(alpha = 0.2f), modifier = Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = NestifySlate)
        Text(category, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}
