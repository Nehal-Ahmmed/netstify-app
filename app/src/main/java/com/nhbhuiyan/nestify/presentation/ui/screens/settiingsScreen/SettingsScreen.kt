package com.nhbhuiyan.nestify.presentation.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // State for settings options
    var darkThemeEnabled by remember { mutableStateOf(uiState.isDarkTheme) }
    var biometricEnabled by remember { mutableStateOf(uiState.isBiometricEnabled) }
    var syncEnabled by remember { mutableStateOf(uiState.isSyncEnabled) }
    var fontSize by remember { mutableStateOf(uiState.fontSize) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // MARK: - APPEARANCE SECTION
            item {
                SettingsSectionHeader(
                    title = "Appearance",
                    icon = Icons.Default.Palette
                )
            }

            item {
                SettingsOptionItem(
                    title = "Dark Theme",
                    subtitle = "Enable dark mode for better night viewing",
                    icon = Icons.Default.DarkMode,
                    trailingContent = {
                        Switch(
                            checked = darkThemeEnabled,
                            onCheckedChange = {
                                darkThemeEnabled = it
                                viewModel.onDarkThemeChanged(it)
                            }
                        )
                    }
                )
            }

            item {
                SettingsOptionItem(
                    title = "Font Size",
                    subtitle = "Adjust the text size throughout the app",
                    icon = Icons.Default.TextFields,
                    trailingContent = {
                        Text(
                            text = when (fontSize) {
                                FontSize.SMALL -> "Small"
                                FontSize.MEDIUM -> "Medium"
                                FontSize.LARGE -> "Large"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        // Show font size selector dialog
                        viewModel.showFontSizeSelector = true
                    }
                )
            }

            // MARK: - SECURITY SECTION
            item {
                SettingsSectionHeader(
                    title = "Security",
                    icon = Icons.Default.Security
                )
            }

            item {
                SettingsOptionItem(
                    title = "Biometric Lock",
                    subtitle = "Use fingerprint or face ID to secure your notes",
                    icon = Icons.Default.Fingerprint,
                    trailingContent = {
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = {
                                biometricEnabled = it
                                viewModel.onBiometricEnabledChanged(it)
                            }
                        )
                    }
                )
            }

            item {
                SettingsOptionItem(
                    title = "Auto Lock",
                    subtitle = "Automatically lock app after 5 minutes",
                    icon = Icons.Default.LockClock,
                    trailingContent = {
                        Text(
                            text = "5 min",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        // Navigate to auto lock settings
                    }
                )
            }

            // MARK: - SYNC & BACKUP SECTION
            item {
                SettingsSectionHeader(
                    title = "Sync & Backup",
                    icon = Icons.Default.CloudSync
                )
            }

            item {
                SettingsOptionItem(
                    title = "Cloud Sync",
                    subtitle = "Automatically sync your notes to the cloud",
                    icon = Icons.Default.CloudUpload,
                    trailingContent = {
                        Switch(
                            checked = syncEnabled,
                            onCheckedChange = {
                                syncEnabled = it
                                viewModel.onSyncEnabledChanged(it)
                            }
                        )
                    }
                )
            }

            item {
                SettingsOptionItem(
                    title = "Backup & Export",
                    subtitle = "Create backup or export your notes",
                    icon = Icons.Default.Backup,
                    onClick = {
                        viewModel.onBackupRequested()
                    }
                )
            }

            // MARK: - DATA MANAGEMENT SECTION
            item {
                SettingsSectionHeader(
                    title = "Data Management",
                    icon = Icons.Default.Storage
                )
            }

            item {
                SettingsOptionItem(
                    title = "Clear Cache",
                    subtitle = "Free up storage space",
                    icon = Icons.Default.DeleteSweep,
                    onClick = {
                        viewModel.onClearCacheRequested()
                    }
                )
            }

            item {
                SettingsOptionItem(
                    title = "Export All Data",
                    subtitle = "Export all notes as PDF or text files",
                    icon = Icons.Default.FileDownload,
                    onClick = {
                        viewModel.onExportDataRequested()
                    }
                )
            }

            // MARK: - ABOUT SECTION
            item {
                SettingsSectionHeader(
                    title = "About",
                    icon = Icons.Default.Info
                )
            }

            item {
                SettingsOptionItem(
                    title = "App Version",
                    subtitle = "Current version and update information",
                    icon = Icons.Default.Apps,
                    trailingContent = {
                        Text(
                            text = "v1.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    onClick = {
                        viewModel.onCheckForUpdates()
                    }
                )
            }

            item {
                SettingsOptionItem(
                    title = "Privacy Policy",
                    subtitle = "How we handle your data",
                    icon = Icons.Default.PrivacyTip,
                    onClick = {
                        viewModel.onPrivacyPolicyClicked()
                    }
                )
            }

            item {
                SettingsOptionItem(
                    title = "Terms of Service",
                    subtitle = "App usage terms and conditions",
                    icon = Icons.Default.Description,
                    onClick = {
                        viewModel.onTermsOfServiceClicked()
                    }
                )
            }

            item {
                SettingsOptionItem(
                    title = "Rate App",
                    subtitle = "Share your feedback on Play Store",
                    icon = Icons.Default.Star,
                    onClick = {
                        viewModel.onRateAppClicked()
                    }
                )
            }

            // MARK: - DANGER ZONE SECTION
            item {
                SettingsSectionHeader(
                    title = "Danger Zone",
                    icon = Icons.Default.Warning,
                    titleColor = MaterialTheme.colorScheme.error
                )
            }

            item {
                SettingsOptionItem(
                    title = "Delete All Notes",
                    subtitle = "Permanently delete all your notes",
                    icon = Icons.Default.DeleteForever,
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        viewModel.onDeleteAllNotesRequested()
                    }
                )
            }

            item {
                SettingsOptionItem(
                    title = "Reset All Settings",
                    subtitle = "Reset all settings to default",
                    icon = Icons.Default.Restore,
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        viewModel.onResetSettingsRequested()
                    }
                )
            }
        }

        // MARK: - DIALOGS
        if (viewModel.showFontSizeSelector) {
            FontSizeDialog(
                currentSize = fontSize,
                onSizeSelected = { newSize ->
                    fontSize = newSize
                    viewModel.onFontSizeChanged(newSize)
                    viewModel.showFontSizeSelector = false
                },
                onDismiss = { viewModel.showFontSizeSelector = false }
            )
        }

        if (viewModel.showBackupDialog) {
            BackupDialog(
                onExport = { viewModel.onExportBackup() },
                onImport = { viewModel.onImportBackup() },
                onDismiss = { viewModel.showBackupDialog = false }
            )
        }

        if (viewModel.showClearCacheDialog) {
            ConfirmationDialog(
                title = "Clear Cache",
                message = "This will free up storage space. This action cannot be undone.",
                onConfirm = { viewModel.onClearCacheConfirmed() },
                onDismiss = { viewModel.showClearCacheDialog = false }
            )
        }

        if (viewModel.showDeleteAllDialog) {
            ConfirmationDialog(
                title = "Delete All Notes",
                message = "This will permanently delete all your notes. This action cannot be undone!",
                confirmText = "Delete All",
                isDestructive = true,
                onConfirm = { viewModel.onDeleteAllNotesConfirmed() },
                onDismiss = { viewModel.showDeleteAllDialog = false }
            )
        }
    }
}

// MARK: - SETTINGS COMPONENTS

/**
 * Header for settings sections with icon and title
 */
@Composable
fun SettingsSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = titleColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = titleColor
        )
    }
}

/**
 * Reusable settings option item with icon, title, subtitle and trailing content
 */
@Composable
fun SettingsOptionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = titleColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Trailing content (switch, text, chevron, etc.)
            trailingContent?.invoke()
        }
    }
}

// MARK: - DIALOG COMPONENTS

/**
 * Dialog for selecting font size
 */
@Composable
fun FontSizeDialog(
    currentSize: FontSize,
    onSizeSelected: (FontSize) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Font Size") },
        text = {
            Column {
                FontSize.values().forEach { size ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSizeSelected(size) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSize == size,
                            onClick = { onSizeSelected(size) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (size) {
                                FontSize.SMALL -> "Small"
                                FontSize.MEDIUM -> "Medium"
                                FontSize.LARGE -> "Large"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

/**
 * Dialog for backup options
 */
@Composable
fun BackupDialog(
    onExport: () -> Unit,
    onImport: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Backup & Export") },
        text = {
            Text("Choose whether to export your data as backup or import existing backup.")
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            Column {
                Button(
                    onClick = {
                        onExport()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export Backup")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        onImport()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import Backup")
                }
            }
        }
    )
}

/**
 * Reusable confirmation dialog
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDestructive) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(confirmText)
            }
        }
    )
}

// MARK: - DATA MODELS

/**
 * Enum for font size options
 */
enum class FontSize {
    SMALL, MEDIUM, LARGE
}

/**
 * Data class for settings state
 */
data class SettingsState(
    val isDarkTheme: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isSyncEnabled: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)