package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.GenericList
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components.FileUploadDialog
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components.fileItem
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.data.FileViewModel

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

    LaunchedEffect(folderId) {
        viewModel.getFilesByFolder(folderId = folderId)
    }

    state.files?.forEach { file->
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
            title = { Text("Upload Error") },
            text = { Text(state.error!!) },
            confirmButton = {
                Button(onClick = {
                    showErrorDialog = false
                    viewModel.clearError()
                }) {
                    Text("OK")
                }
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

    // Main UI Scaffold
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Files") }
            )
        },
        floatingActionButton = {
            // FAB with dropdown - UI Logic
            var expanded by remember { mutableStateOf(false) }

            Box {
                FloatingActionButton(
                    onClick = {
                        Log.d("FileScreen", "➕ FAB clicked")
                        expanded = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add File")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Upload File") },
                        onClick = {
                            Log.d("FileScreen", "📤 Upload File menu item clicked")
                            filePickerLauncher.launch("*/*")
                            expanded = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Upload, contentDescription = "Upload File")
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Content Area - UI Logic
        Log.d("FileScreen", "📦 Rendering content area")

        if (state.isLoading) {
            Log.d("FileScreen", "⏳ Showing loading state")
            LoadingShimmer()
        } else {
            Log.d("FileScreen", "📋 Showing file list with ${state.files.size} files")
            if (state.files.isEmpty()) {
                // Empty state UI
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No files yet. Upload your first file!")
                }
            } else {
                GenericList(
                    items = state.files,
                    modifier = Modifier.padding(innerPadding)
                ) { file ->
                    fileItem(
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

    Log.d("FileScreen", "🏁 FileScreen UI Composable COMPLETED")
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