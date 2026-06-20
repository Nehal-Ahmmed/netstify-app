package com.nhbhuiyan.nestify.presentation.ui.screens.ServiceScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceScreen(navController: NavController) {
    val services = listOf(
        ServiceData(
            title = "AI Academic Assistant",
            description = "Get smart definitions, automated summaries, and study tools powered by Gemini AI.",
            icon = Icons.Default.AutoAwesome,
            color = NestifySkyBlue
        ),
        ServiceData(
            title = "Professional CV Builder",
            description = "Generate industry-standard PDFs from your profile in seconds. Ready for your next big break.",
            icon = Icons.Default.Article,
            color = NestifyPeach
        ),
        ServiceData(
            title = "Secure Biometric Vault",
            description = "Protect your professional documents and private gallery with system-level biometric security.",
            icon = Icons.Default.Fingerprint,
            color = NestifyGreen
        ),
        ServiceData(
            title = "Cloud Productivity Sync",
            description = "Seamlessly sync your notes, routines, and tasks across all your devices with real-time updates.",
            icon = Icons.Default.CloudSync,
            color = NestifyBlueGray
        )
    )

    Scaffold(
        containerColor = NestifySurface,
        bottomBar = {
            ServiceBottomBar()
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Stunning Header
            item {
                ServiceHeader()
            }

            // Introduction Text
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tailored Solutions",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Elevate Your Professional\nProductivity",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                        color = NestifySlate
                    )
                }
            }

            // Services List
            items(services) { service ->
                ServiceCard(service)
            }

            // Trust Section
            item {
                TrustSection()
            }
        }
    }
}

@Composable
fun ServiceHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(NestifyGradients.meshGradient()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Verified,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Premium Services",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
fun ServiceCard(service: ServiceData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(service.color.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    service.icon,
                    contentDescription = null,
                    tint = service.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(20.dp))
            Column {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NestifySlate
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun TrustSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Why Professionals Choose Nestify",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = NestifySlate
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TrustIcon(Icons.Default.Security, "Privacy")
            TrustIcon(Icons.Default.Speed, "Fast")
            TrustIcon(Icons.Default.Update, "Modern")
        }
    }
}

@Composable
fun TrustIcon(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, modifier = Modifier.size(24.dp), tint = NestifySlate.copy(alpha = 0.5f))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun ServiceBottomBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Start Your Journey", style = MaterialTheme.typography.labelMedium)
                Text("Custom Plans Available", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = NestifySlate),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Get Started")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

data class ServiceData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)
