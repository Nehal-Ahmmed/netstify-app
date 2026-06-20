package com.nhbhuiyan.nestify.presentation.ui.screens.GalleryScreen

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.data.local.entity.GalleryCategory
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf(GalleryCategory.NORMAL) }
    var isPersonalUnlocked by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    Scaffold(
        containerColor = NestifySurface,
        topBar = {
            GalleryHeader()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add Media */ },
                containerColor = NestifySlate,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "Add Media")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Premium Category Selector
            GalleryCategorySelector(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    if (category == GalleryCategory.PERSONAL && !isPersonalUnlocked) {
                        handleBiometricAuth(context, activity) {
                            isPersonalUnlocked = true
                            selectedCategory = category
                        }
                    } else {
                        selectedCategory = category
                    }
                }
            )

            // Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                when (selectedCategory) {
                    GalleryCategory.PERSONAL -> {
                        if (isPersonalUnlocked) {
                            MediaGrid(title = "Secure Vault", items = 8)
                        } else {
                            LockedVaultUI {
                                handleBiometricAuth(context, activity) {
                                    isPersonalUnlocked = true
                                    selectedCategory = GalleryCategory.PERSONAL
                                }
                            }
                        }
                    }
                    GalleryCategory.FORMAL -> {
                        MediaGrid(title = "Documents & Cards", items = 4)
                    }
                    GalleryCategory.NORMAL -> {
                        MediaGrid(title = "Recent Media", items = 12)
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
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
                "Secure Gallery",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Icon(Icons.Default.Shield, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun GalleryCategorySelector(
    selectedCategory: GalleryCategory,
    onCategorySelected: (GalleryCategory) -> Unit
) {
    val categories = listOf(GalleryCategory.NORMAL, GalleryCategory.FORMAL, GalleryCategory.PERSONAL)
    
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category
            Surface(
                onClick = { onCategorySelected(category) },
                color = if (isSelected) NestifySlate else Color.White,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NestifySlate else Color.LightGray.copy(alpha = 0.4f)),
                modifier = Modifier.height(44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when(category) {
                            GalleryCategory.NORMAL -> Icons.Default.Collections
                            GalleryCategory.FORMAL -> Icons.Default.Assignment
                            GalleryCategory.PERSONAL -> Icons.Default.Https
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isSelected) Color.White else Color.Gray
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun MediaGrid(title: String, items: Int) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = NestifySlate
        )
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Image, null, tint = NestifySlate.copy(alpha = 0.1f), modifier = Modifier.size(48.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LockedVaultUI(onUnlock: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(NestifySlate.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Lock, null, tint = NestifySlate, modifier = Modifier.size(56.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Personal Vault is Locked",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = NestifySlate
        )
        Text(
            "Authentication required to access",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onUnlock,
            colors = ButtonDefaults.buttonColors(containerColor = NestifySlate),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Fingerprint, null)
            Spacer(Modifier.width(8.dp))
            Text("Unlock with Biometrics")
        }
    }
}

private fun handleBiometricAuth(
    context: android.content.Context,
    activity: FragmentActivity?,
    onSuccess: () -> Unit
) {
    if (activity == null) {
        Toast.makeText(context, "FragmentActivity required", Toast.LENGTH_SHORT).show()
        return
    }
    
    val executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(context, errString, Toast.LENGTH_SHORT).show()
            }
        })
        
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock Personal Vault")
        .setSubtitle("Authenticate to view secure media")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        .build()
        
    biometricPrompt.authenticate(promptInfo)
}
