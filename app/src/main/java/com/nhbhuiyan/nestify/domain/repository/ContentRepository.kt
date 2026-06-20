package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.data.local.entity.FileEntity
import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.model.FileFolder
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    //Notes
    suspend fun createNote(note: Note): Long
    suspend fun updateNote(note: Note)
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun deleteNote(id: Long)
    suspend fun bookmarkNote(noteId: Long, isBookmarked: Boolean)

    //Links
    suspend fun createLink(link: Link): Long
    fun getAllLinks(): Flow<List<Link>>
    suspend fun getLinkById(id: Long): Link?
    suspend fun deleteLink(id: Long)
    suspend fun bookmarkLink(id: Long, isBookmarked: Boolean)

    //file folder
    suspend fun insertFileFolder(fileFolder: FileFolder) : Long
    fun getFoldersByCategory(category: String) : Flow<List<FileFolder>>
    fun getFilesByFolder(folderId: Long) : Flow<List<File>>


    //Files
    suspend fun createFile(file: File): Long
    fun getAllFiles(): Flow<List<File>>
    suspend fun getFileById(id: Long): File?
    suspend fun deleteFile(id: Long)
    suspend fun uploadFile(
        sourceUri: String,
        fileName: String,
        fileType: String,
        fileSize: Long,
        folderId: Long,
        mimeType: String,
        moveFile: Boolean
    ): Long
    suspend fun copyFileToAppStorage(sourceUri: String, fileName: String): String
    suspend fun moveFileToAppStorage(sourceUri: String, fileName: String): String
    suspend fun deleteSourceFile(sourceUri: String): Boolean
    fun getAppFilesDirectory(): java.io.File


    // Link Folder
    suspend fun createLinkFolder(folder: com.nhbhuiyan.nestify.domain.model.LinkFolder): Long
    fun getAllLinkFolders(): Flow<List<com.nhbhuiyan.nestify.domain.model.LinkFolder>>
    suspend fun deleteLinkFolder(id: Long)
    fun getLinksByFolder(folderId: Long): Flow<List<Link>>

    //Common: Search
    fun searchNotes(query: String): Flow<List<Note>>
    fun searchLinks(query: String): Flow<List<Link>>
    fun searchFiles(query: String): Flow<List<File>>
}