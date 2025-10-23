package com.nhbhuiyan.nestify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nhbhuiyan.nestify.data.local.Dao.ContentDao
import com.nhbhuiyan.nestify.data.local.entity.ClassRoutineEntity
import com.nhbhuiyan.nestify.data.local.entity.Converters
import com.nhbhuiyan.nestify.data.local.entity.FileEntity
import com.nhbhuiyan.nestify.data.local.entity.LinkEntity
import com.nhbhuiyan.nestify.data.local.entity.NoteEntity

@Database(
    entities = [
        NoteEntity::class,
    LinkEntity :: class,
    FileEntity::class,
    ClassRoutineEntity::class //created later
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun contentDao() : ContentDao
}