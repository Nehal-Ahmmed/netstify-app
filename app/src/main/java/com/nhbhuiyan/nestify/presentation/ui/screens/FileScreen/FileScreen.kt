package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.GlassNavSpace
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.EmptyState
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components.FileUploadDialog
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.data.FileViewModel
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileScreen(navController: NavController, modifier: Modifier = Modifier) {
    Log.d("FileScreen", "🎬 FileScreen UI Composable STARTED")
    val currentBackStack = navController.currentBackStackEntry
    val folderId = currentBackStack?.arguments?.getString("folderId")?.toLong() ?: 1
    Log.d("FileScreen", "📁 Folder ID: $folderId")

    // Get ViewModel for data only
    val viewModel: FileViewModel = hiltViewModel()
    val state by viewModel.fileUiState.collectAsState()
    val context = LocalContext.current
    val c = NestifyTheme.colors

    LaunchedEffect(folderId) {
        viewModel.getFilesByFolder(folderId = folderId)
    }

    state.files?.forEach { file ->
        Log.d("FileScreen", "📁 folder id: ${file.folderId}")
    }

    // UI State - All UI logic managed here
    var showUploadDialog by remember { mutableStateOf(false) }
    var selectedFileUri by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Handle error state changes
    LaunchedEffect(state.error) {
        if (state.error != null) {
            showErrorDialog = true
            Log.d("FileScreen", "🚨 Error detected: ${state.error}")
        }
    }

    // File Picker Launcher - UI Logic
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        Log.d("FileScreen", "📁 File picker result: $uri")
        if (uri != null) {
            try {
                // Get file details (UI logic)
                val fileName = getFileNameFromUri(context, uri) ?: "Unknown File"
                val fileSize = getFileSizeFromUri(context, uri) ?: 0L
                val mimeType = context.contentResolver.getType(uri) ?: "*/*"
                val fileType = getFileExtension(fileName)

                Log.d("FileScreen", "✅ File selected - Name: $fileName, Size: $fileSize, Type: $mimeType")

                // Update UI state
                selectedFileUri = uri.toString()
                showUploadDialog = true

            } catch (e: Exception) {
                Log.e("FileScreen", "❌ Error processing selected file: ${e.message}", e)
                // UI handles the error display
                showErrorDialog = true
            }
        } else {
            Log.d("FileScreen", "❌ File selection cancelled")
        }
    }

    // Error Dialog - UI Logic
    if (showErrorDialog && state.error != null) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.clearError()
            },
            containerColor = c.surface,
            shape = Radii.xl,
            title = { Text("Upload Error", style = NestifyTheme.type.h3Serif, color = c.ink) },
            text = { Text(state.error!!, style = NestifyTheme.type.body, color = c.ink70) },
            confirmButton = {
                NButton(
                    label = "OK",
                    onClick = {
                        showErrorDialog = false
                        viewModel.clearError()
                    },
                )
            }
        )
    }

    // Upload Dialog - UI Logic
    if (showUploadDialog && selectedFileUri != null) {
        val parsedUri = Uri.parse(selectedFileUri)
        val fileName = getFileNameFromUri(context, parsedUri) ?: "Unknown File"
        val fileSize = getFileSizeFromUri(context, parsedUri) ?: 0L
        val fileSizeFormatted = formatFileSize(fileSize)
        val fileType = getFileExtension(fileName)
        val mimeType = context.contentResolver.getType(parsedUri) ?: "*/*"

        FileUploadDialog(
            fileName = fileName,
            fileSize = fileSizeFormatted,
            fileType = fileType,
            onDismiss = {
                Log.d("FileScreen", "📋 Upload dialog dismissed")
                showUploadDialog = false
                selectedFileUri = null
            },
            onUpload = { moveFile ->
                Log.d("FileScreen", "🚀 Upload initiated - Move: $moveFile")
                // Call ViewModel for data operation only
                viewModel.uploadFile(
                    uri = selectedFileUri!!,
                    name = fileName,
                    fileType = fileType,
                    size = fileSize,
                    mimeType = mimeType,
                    moveFile = moveFile,
                    folderId = folderId
                )
                // Update UI state
                showUploadDialog = false
                selectedFileUri = null
            }
        )
    }

    // Main UI
    Column(
        Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        var expanded by remember { mutableStateOf(false) }

        NestifyAppBar(
            title = "Files",
            onBack = { navController.popBackStack() },
            trailing = {
                Box {
                    IconButtonChrome(
                        Icons.Default.Add,
                        onClick = {
                            Log.d("FileScreen", "➕ FAB clicked")
                            expanded = true
                        },
                        tint = c.brand,
                        contentDescription = "Add File",
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Upload File", style = NestifyTheme.type.body, color = c.ink) },
                            onClick = {
                                Log.d("FileScreen", "📤 Upload File menu item clicked")
                                filePickerLauncher.launch("*/*")
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Upload, contentDescription = "Upload File", tint = c.ink70)
                            }
                        )
                    }
                }
            },
        )

        Log.d("FileScreen", "📦 Rendering content area")

        if (state.isLoading) {
            Log.d("FileScreen", "⏳ Showing loading state")
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = c.brand)
            }
        } else {
            Log.d("FileScreen", "📋 Showing file list with ${state.files.size} files")
            if (state.files.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.AutoMirrored.Outlined.InsertDriveFile,
                        title = "No files yet",
                        description = "Upload your first file to this folder to get started.",
                        primaryLabel = "Upload File",
                        onPrimary = { filePickerLauncher.launch("*/*") },
                    )
                }
            } else {
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
                    items(state.files, key = { it.id }) { file ->
                        FileRow(
                            file = file,
                            onClick = {
                                Log.d("FileScreen", "📁 File clicked: ${file.fileName}")
                                navController.navigate(Route.FileDetail.createRoute(fileId = file.id))
                            }
                        )
                    }
                }
            }
        }
    }

    Log.d("FileScreen", "🏁 FileScreen UI Composable COMPLETED")
}

@Composable
private fun FileRow(file: File, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(
        modifier = Modifier.fillMaxWidth(),
        padding = Space.m,
        onClick = onClick,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.m),
        ) {
            IconTile(icon = fileTypeIcon(file.fileType))
            Column(Modifier.weight(1f)) {
                OneLine(
                    text = file.fileName,
                    style = NestifyTheme.type.h3Serif,
                    color = c.ink,
                )
                Kicker(
                    listOf(
                        file.fileType.uppercase().ifBlank { "FILE" },
                        formatFileSize(file.fileSize),
                    ).joinToString(" · ")
                )
            }
        }
    }
}

private fun fileTypeIcon(fileType: String): ImageVector = when (fileType.lowercase()) {
    "pdf" -> Icons.Outlined.PictureAsPdf
    "jpg", "jpeg", "png", "gif", "webp", "image" -> Icons.Outlined.Image
    "mp4", "mkv", "mov", "avi", "video" -> Icons.Outlined.VideoFile
    "doc", "docx", "txt", "document" -> Icons.Outlined.Description
    else -> Icons.AutoMirrored.Outlined.InsertDriveFile
}

// UI Helper functions (Pure utility - no business logic)
private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        cursor.getString(nameIndex)
    } ?: uri.lastPathSegment ?: "Unknown_File"
}

private fun getFileSizeFromUri(context: android.content.Context, uri: Uri): Long? {
    return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
        cursor.moveToFirst()
        cursor.getLong(sizeIndex)
    } ?: 0L
}

private fun getFileExtension(fileName: String): String {
    return fileName.substringAfterLast('.', "")
}

private fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }
}
