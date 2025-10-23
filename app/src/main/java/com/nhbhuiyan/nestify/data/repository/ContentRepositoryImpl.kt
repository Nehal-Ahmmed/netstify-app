package com.nhbhuiyan.nestify.data.repository

import com.nhbhuiyan.nestify.data.local.Dao.ContentDao
import com.nhbhuiyan.nestify.data.local.entity.ClassRoutineEntity
import com.nhbhuiyan.nestify.data.mapper.toClassRoutine
import com.nhbhuiyan.nestify.data.mapper.toClassRoutineEntity
import com.nhbhuiyan.nestify.data.mapper.toFile
import com.nhbhuiyan.nestify.data.mapper.toFileEntity
import com.nhbhuiyan.nestify.data.mapper.toLink
import com.nhbhuiyan.nestify.data.mapper.toLinkEntity
import com.nhbhuiyan.nestify.data.mapper.toNote
import com.nhbhuiyan.nestify.data.mapper.toNoteEntity
import com.nhbhuiyan.nestify.domain.model.ClassRoutine
import com.nhbhuiyan.nestify.domain.model.File
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    private val contentDao: ContentDao
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
        contentDao.deleteFile(id)
    }

    override suspend fun createRoutine(classRoutine: ClassRoutine): Long {
        return contentDao.insertRoutine(classRoutine.toClassRoutineEntity())
    }

    override fun getAllRoutines(): Flow<List<ClassRoutine>> {
        return contentDao.getAllRoutines().map { routine->
            routine.map { it.toClassRoutine()}
        }
    }

    override suspend fun getRoutineById(id: Long): ClassRoutine? {
        return contentDao.getRoutineById(id).toClassRoutine()
    }

//    override suspend fun getRoutineById(id: Long): ClassRoutine? {
//        return contentDao.getRoutineById(id)?.toClassRoutine()
//    }

    override suspend fun deleteRoutine(classRoutine: ClassRoutine) {
        contentDao.deleteRoutine(classRoutine.toClassRoutineEntity())
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
}