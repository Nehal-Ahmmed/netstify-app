package com.nhbhuiyan.nestify.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.nhbhuiyan.nestify.data.local.Dao.ContentDao
import com.nhbhuiyan.nestify.data.mapper.toFile
import com.nhbhuiyan.nestify.data.mapper.toFileEntity
import com.nhbhuiyan.nestify.data.mapper.toFileFolder
import com.nhbhuiyan.nestify.data.mapper.toFileFolderEntity
import com.nhbhuiyan.nestify.data.mapper.toLink
import com.nhbhuiyan.nestify.data.mapper.toLinkEntity
import com.nhbhuiyan.nestify.data.mapper.toLinkFolder
import com.nhbhuiyan.nestify.data.mapper.toLinkFolderEntity
import com.nhbhuiyan.nestify.data.mapper.toNote
import com.nhbhuiyan.nestify.data.mapper.toNoteEntity
import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.model.FileFolder
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    private val contentDao: ContentDao,
    private val context : Context
) : ContentRepository {
    override suspend fun createNote(note: Note): Long {
        return contentDao.insertNote(note.toNoteEntity())
    }

    override suspend fun updateNote(note: Note) {
        contentDao.updateNote(note.toNoteEntity())
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return contentDao.getAllNotes().map { note->
            note.map { it.toNote() }
        }
    }

    override suspend fun getNoteById(id: Long): Note? {
        return contentDao.getNoteById(id)?.toNote()
    }

    override suspend fun deleteNote(id: Long) {
        contentDao.deleteNote(id)
    }

    override suspend fun bookmarkNote(noteId: Long, isBookmarked: Boolean) {
        contentDao.bookmarkNote(noteId, isBookmarked)
    }

    override suspend fun createLink(link: Link): Long {
        return contentDao.insertLink(link.toLinkEntity())
    }

    override fun getAllLinks(): Flow<List<Link>> {
        return contentDao.getAllLinks().map { link->
            link.map { it.toLink() }
        }
    }

    override suspend fun getLinkById(id: Long): Link? {
        return contentDao.getLinkById(id)?.toLink()
    }

    override suspend fun deleteLink(id: Long) {
        contentDao.deleteLink(id)
    }

    override suspend fun bookmarkLink(id: Long, isBookmarked: Boolean) {
        contentDao.bookmarkLink(id, isBookmarked)
    }

    override suspend fun insertFileFolder(fileFolder: FileFolder): Long {
        return contentDao.insertFileFolder(fileFolderEntity = fileFolder.toFileFolderEntity())
    }

    override fun getFoldersByCategory(category: String): Flow<List<FileFolder>> {
        val folders=contentDao.getFoldersByCategory(category = category)
        return folders.map { filefolder->
            filefolder.map { it.toFileFolder() }
        }
    }

    override fun getFilesByFolder(folderId: Long): Flow<List<File>> {
        val files= contentDao.getFilesByFolder(folderId = folderId)
        return files.map { file->
            file.map { it.toFile() }
        }
    }

    override suspend fun createFile(file: File): Long {
        return contentDao.insertFile(file.toFileEntity())
    }

    override fun getAllFiles(): Flow<List<File>> {
        return contentDao.getAllFIles().map { file->
            file.map { it.toFile() }
        }
    }

    override suspend fun getFileById(id: Long): File? {
        return contentDao.getFileById(id)?.toFile()
    }

    override suspend fun deleteFile(id: Long) {
        contentDao.deletefile(id)
    }

    override suspend fun uploadFile(
        sourceUri: String,
        fileName: String,
        fileType: String,
        fileSize: Long,
        folderId: Long,
        mimeType: String,
        moveFile: Boolean
    ): Long {
        return try {

            Log.d("FileUpload", "Starting upload: $fileName, URI: $sourceUri")

            val destinationUri=if(moveFile){
                moveFileToAppStorage(sourceUri,fileName)
            }else{
                copyFileToAppStorage(sourceUri,fileName)
            }

            Log.d("FileUpload", "File stored at: $destinationUri")


            val file = File(
                uri = destinationUri,
                fileName = fileName,
                fileType = fileType,
                fileSize = fileSize,
                createdAt = kotlinx.datetime.Clock.System.now(),
                updatedAt = kotlinx.datetime.Clock.System.now(),
                mimeType = mimeType,
                folderId = folderId
            )

            val fileId = contentDao.insertFile(file.toFileEntity())
            Log.d("FileUpload", "File saved to database with ID: $fileId")
            fileId
        }catch (e: Exception){
            throw Exception("Failed to upload file : ${e.message}")
        }
    }

    override suspend fun copyFileToAppStorage(sourceUri: String, fileName: String): String {
        return try {
            val sourceUriObj = Uri.parse(sourceUri)
            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUriObj)

            if (inputStream == null) {
                Log.e("FileUpload", "Cannot open input stream for URI: $sourceUri")
                throw Exception("Cannot open source file")
            }

            // Create destination file in app's private storage
            val destinationFile = createUniqueFile(fileName)
            Log.d("FileUpload", "Destination file: ${destinationFile.absolutePath}")

            val outputStream = FileOutputStream(destinationFile)

            // Copy file content with buffer for better performance
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("FileUpload", "File copied successfully, size: ${destinationFile.length()} bytes")

            // Return the new file URI
            Uri.fromFile(destinationFile).toString()
        } catch (e: SecurityException) {
            Log.e("FileUpload", "Security exception: ${e.message}", e)
            throw Exception("Permission denied: ${e.message}")
        } catch (e: Exception) {
            Log.e("FileUpload", "Copy failed: ${e.message}", e)
            throw Exception("Failed to copy file: ${e.message}")
        }
    }

    override suspend fun moveFileToAppStorage(sourceUri: String, fileName: String): String {
        return try {
            val destinationUri = copyFileToAppStorage(sourceUri,fileName)

            if (deleteSourceFile(sourceUri)){
                destinationUri
            }else{
                println("Warning: Could not delete source file after copy")
                destinationUri
            }
        }catch (e: Exception){
            throw Exception("Failed to move file : ${e.message}")
        }
    }

    override suspend fun deleteSourceFile(sourceUri: String): Boolean {
        return try {
            val sourceUriObj = Uri.parse(sourceUri)

            if(sourceUriObj.scheme == "file"){
                val file = java.io.File(sourceUriObj.path ?: "")
                if(file.exists()){
                    file.delete()
                }else{
                    true
                }
            }else{
                true
            }
        }catch (e: Exception){
            throw Exception("Failed to delete source file : ${e.message}")
        }
    }

    override fun getAppFilesDirectory(): java.io.File {
        return java.io.File(context.filesDir, "uploaded_files").apply {
            if(!exists()){
                mkdirs()
            }
        }
    }

    private fun createUniqueFile(originalName: String) : java.io.File{
        val filesDir = getAppFilesDirectory()
        val fileExtension = getfileExtension(originalName)
        val baseName = getFileNameWithoutExtension(originalName)

        val uniqueFileName = if(fileExtension.isNotEmpty()){
            "${baseName}_${UUID.randomUUID()}.${fileExtension}"
        }else{
            "${baseName}_${UUID.randomUUID()}"
        }

        return java.io.File(filesDir,uniqueFileName)
    }

    private fun getfileExtension(fileName: String): String{
        return fileName.substringAfterLast('.',"").lowercase()
    }

    private fun getFileNameWithoutExtension(fileName: String): String{
        return fileName.substringBeforeLast('.',fileName)
    }


    //searching
    override fun searchNotes(query: String): Flow<List<Note>> {
        return contentDao.searchNotes(query).map { note->
            note.map { it.toNote() }
        }
    }

    override fun searchLinks(query: String): Flow<List<Link>> {
        return contentDao.searchLinks(query).map { link->
            link.map { it.toLink() }
        }
    }

    override fun searchFiles(query: String): Flow<List<File>> {
        return contentDao.searchFiles(query).map { file->
            file.map { it.toFile() }
        }
    }

    // Link Folder Implementation
    override suspend fun createLinkFolder(folder: com.nhbhuiyan.nestify.domain.model.LinkFolder): Long {
        return contentDao.insertLinkFolder(folder.toLinkFolderEntity())
    }

    override fun getAllLinkFolders(): Flow<List<com.nhbhuiyan.nestify.domain.model.LinkFolder>> {
        return contentDao.getAllLinkFolders().map { entities ->
            entities.map { it.toLinkFolder() }
        }
    }

    override suspend fun deleteLinkFolder(id: Long) {
        contentDao.deleteLinkFolder(id)
    }

    override fun getLinksByFolder(folderId: Long): Flow<List<Link>> {
        return contentDao.getLinksByFolder(folderId).map { entities ->
            entities.map { it.toLink() }
        }
    }
}