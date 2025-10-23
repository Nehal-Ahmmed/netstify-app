package com.nhbhuiyan.nestify.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nhbhuiyan.nestify.data.local.entity.ClassRoutineEntity
import com.nhbhuiyan.nestify.data.local.entity.FileEntity
import com.nhbhuiyan.nestify.data.local.entity.LinkEntity
import com.nhbhuiyan.nestify.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    //Notes operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note : NoteEntity) : Long
    @Update
    suspend fun updateNote(note : NoteEntity)
    @Query("Select * from notes Order by createdAt Desc")
    fun getAllNotes() : Flow<List<NoteEntity>>
    @Query("Select * from notes Where id= :id")
    suspend fun getNoteById(id: Long) : NoteEntity?
    @Query("Delete from notes where id= :id")
    suspend fun deleteNote(id: Long)

    //Links operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: LinkEntity) : Long

    @Query("select * from links order by createdAt desc")
    fun getAllLinks() : Flow<List<LinkEntity>>

    @Query("select * from links where id= :id")
    suspend fun getLinkById(id: Long) : LinkEntity?

    @Query("delete from links where id = :id")
    suspend fun deleteLink(id: Long)

    //files operations
    @Insert
    suspend fun insertFile(file: FileEntity) : Long

    @Query("select * from files order by createdAt desc")
    fun getAllFIles() : Flow<List<FileEntity>>

    @Query("select * from files where id= :id")
    suspend fun getFileById(id: Long ) : FileEntity?

    @Query("delete from files where id = :id")
    suspend fun deleteFile(id: Long)

    //class routine
    @Insert
    suspend fun insertRoutine(classRoutine: ClassRoutineEntity) : Long

    @Delete
    suspend fun deleteRoutine(classRoutine: ClassRoutineEntity)

    @Query("select * from class_routines")
    fun getAllRoutines() : Flow<List<ClassRoutineEntity>>

    @Query("select * from class_routines where id= :id")
    suspend fun getRoutineById(id: Long) : ClassRoutineEntity

    //commonOperations
    @Query("select * from notes where title like :query or content like :query")
    fun searchNotes(query : String) : Flow<List<NoteEntity>>

    @Query("select * from links where url like :query or title like :query")
    fun searchLinks(query : String) : Flow<List<LinkEntity>>

    @Query("select * from files where fileName like :query")
    fun searchFiles(query : String) : Flow<List<FileEntity>>


}