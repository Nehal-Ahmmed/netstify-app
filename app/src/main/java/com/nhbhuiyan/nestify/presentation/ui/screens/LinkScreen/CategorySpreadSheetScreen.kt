package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySpreadSheetScreen(navController: NavController, categoryId: String) {
    val category = mockLinkCategories.find { it.id == categoryId } ?: mockLinkCategories.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.name, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add New Item */ }) {
                        Icon(Icons.Default.Add, "Add Item")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF1F4F9) // Slightly gray background for the "desk"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // "Spread Note Sheet" Container
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                shape = RoundedCornerShape(4.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Vertical Margin Line (Lined Paper Effect)
                    Canvas(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(40.dp)
                    ) {
                        val marginOffset = 2.dp.toPx()

                        drawLine(
                            color = Color.Red.copy(alpha = 0.2f),
                            start = androidx.compose.ui.geometry.Offset(
                                x = size.width - marginOffset,
                                y = 0f
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                x = size.width - marginOffset,
                                y = size.height
                            ),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 42.dp) // Start after the red margin line
                    ) {
                        items(mockLinkItems) { item ->
                            SpreadSheetRow(item = item) {
                                navController.navigate(Route.LinkGroupDetail.createRoute(item.id))
                            }
                            // Horizontal Line
                            HorizontalDivider(color = Color(0xFFE0E6ED), thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpreadSheetRow(item: LinkItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Small related image/icon
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF8FAFB)
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = NestifySlate,
                modifier = Modifier.padding(10.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NestifySlate
            )
            Text(
                text = item.description,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Rotate this or use a chevron
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(16.dp).padding(start = 4.dp) // Flip or use Forward
        )
    }
}
