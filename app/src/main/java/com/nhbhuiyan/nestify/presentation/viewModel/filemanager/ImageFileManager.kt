package com.nhbhuiyan.nestify.presentation.viewModel.filemanager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ImageFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun saveImageFromUri(contentUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver

            // Create a unique file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "class_routine_${timeStamp}.jpg"
            val imageFile = File(context.filesDir, fileName)

            contentResolver.openInputStream(contentUri)?.use { inputStream ->
                FileOutputStream(imageFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return@withContext imageFile.absolutePath // Store this path in database

        } catch (e: Exception) {
            Log.e("ImageFileManager", "Error saving image: ${e.message}")
            null
        }
    }

    fun loadImageFromPath(filePath: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(filePath)
        } catch (e: Exception) {
            Log.e("ImageFileManager", "Error loading image: ${e.message}")
            null
        }
    }

    suspend fun deleteImage(filePath: String) {
        withContext(Dispatchers.IO) {
            try {
                File(filePath).delete()
            } catch (e: Exception) {
                Log.e("ImageFileManager", "Error deleting image: ${e.message}")
            }
        }
    }
}