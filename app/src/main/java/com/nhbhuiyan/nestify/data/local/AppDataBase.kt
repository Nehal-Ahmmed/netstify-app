package com.nhbhuiyan.nestify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nhbhuiyan.nestify.data.local.Dao.ContentDao
import com.nhbhuiyan.nestify.data.local.Dao.ProfileDao
import com.nhbhuiyan.nestify.data.local.entity.Converters
import com.nhbhuiyan.nestify.data.local.entity.FileEntity
import com.nhbhuiyan.nestify.data.local.entity.FileFolderEntity
import com.nhbhuiyan.nestify.data.local.entity.LinkEntity
import com.nhbhuiyan.nestify.data.local.entity.NoteEntity
import com.nhbhuiyan.nestify.data.local.entity.MediaEntity
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import com.nhbhuiyan.nestify.data.local.entity.ProfileEntity
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import com.nhbhuiyan.nestify.data.local.Dao.ExamPlannerDao

@Database(
    entities = [
        NoteEntity::class,
        LinkEntity::class,
        FileEntity::class,
        FileFolderEntity::class,
        MediaEntity::class,
        LibraryItemEntity::class,
        com.nhbhuiyan.nestify.data.local.entity.LinkFolderEntity::class,
        com.nhbhuiyan.nestify.data.local.entity.CategoryEntity::class,
        com.nhbhuiyan.nestify.data.local.entity.ScheduleEntity::class,
        com.nhbhuiyan.nestify.data.local.entity.AttachmentEntity::class,
        ProfileEntity::class,
        com.nhbhuiyan.nestify.data.local.entity.ProjectPlanEntity::class,
        com.nhbhuiyan.nestify.data.local.entity.MyProjectEntity::class,
        SubjectEntity::class,
        ClassTestMarkEntity::class,
        SyllabusTopicEntity::class,
        TermReportEntity::class,
        com.nhbhuiyan.nestify.data.local.entity.PYQEntity::class
    ],
    version = 20, // v20: add firestoreId to syllabus_topics and pyqs tables
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun scheduleDao(): com.nhbhuiyan.nestify.data.local.Dao.ScheduleDao
    abstract fun mediaDao(): com.nhbhuiyan.nestify.data.local.Dao.MediaDao
    abstract fun libraryItemDao(): com.nhbhuiyan.nestify.data.local.Dao.LibraryItemDao
    abstract fun profileDao(): ProfileDao
    abstract fun projectPlanDao(): com.nhbhuiyan.nestify.data.local.Dao.ProjectPlanDao
    abstract fun myProjectDao(): com.nhbhuiyan.nestify.data.local.Dao.MyProjectDao
    abstract fun examPlannerDao(): ExamPlannerDao
}