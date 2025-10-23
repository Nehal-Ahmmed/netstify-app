package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.data.local.entity.ClassRoutineEntity
import com.nhbhuiyan.nestify.domain.model.ClassRoutine
import com.nhbhuiyan.nestify.domain.model.File
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

    //Links
    suspend fun createLink(link: Link): Long
    fun getAllLinks(): Flow<List<Link>>
    suspend fun getLinkById(id: Long): Link?
    suspend fun deleteLink(id: Long)

    //Files
    suspend fun createFile(file: File): Long
    fun getAllFiles(): Flow<List<File>>
    suspend fun getFileById(id: Long): File?
    suspend fun deleteFile(id: Long)

    //class routine
    suspend fun createRoutine(classRoutine: ClassRoutine): Long
    fun getAllRoutines(): Flow<List<ClassRoutine>>
    suspend fun getRoutineById(id: Long): ClassRoutine?
    suspend fun deleteRoutine(classRoutine: ClassRoutine)


    //Common: Search
    fun searchNotes(query: String): Flow<List<Note>>
    fun searchLinks(query: String): Flow<List<Link>>
    fun searchFiles(query: String): Flow<List<File>>
    //fun searchRoutine(query: String) : Flow<List<ClassRoutine>>


}