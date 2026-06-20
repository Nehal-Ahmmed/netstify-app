package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkGroupDetailScreen(navController: NavController, groupId: String) {
    val context = LocalContext.current
    val item = mockLinkItems.find { it.id == groupId } ?: mockLinkItems.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Details", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Toggle Edit Mode */ }) {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NestifySurface)
            )
        },
        containerColor = NestifySurface
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = item.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = NestifySlate
                )
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Text(
                    "Associated Links",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NestifySlate,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(item.links) { subLink ->
                SubLinkCard(subLink = subLink) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(subLink.url))
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun SubLinkCard(subLink: SubLink, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECF0F1))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subLink.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NestifySlate
                )
                Text(
                    text = subLink.url,
                    fontSize = 13.sp,
                    color = Color(0xFF3498DB), // Blue color for links
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { onClick() }
                        .padding(top = 4.dp),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onClick) {
                Icon(Icons.Default.OpenInNew, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}
