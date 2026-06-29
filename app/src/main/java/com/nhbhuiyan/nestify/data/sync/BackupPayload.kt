package com.nhbhuiyan.nestify.data.sync

import com.nhbhuiyan.nestify.data.local.entity.*

data class BackupPayload(
    val notes: List<NoteEntity> = emptyList(),
    val links: List<LinkEntity> = emptyList(),
    val files: List<FileEntity> = emptyList(),
    val fileFolders: List<FileFolderEntity> = emptyList(),
    val linkFolders: List<LinkFolderEntity> = emptyList(),
    val media: List<MediaEntity> = emptyList(),
    val libraryItems: List<LibraryItemEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val schedules: List<ScheduleEntity> = emptyList(),
    val attachments: List<AttachmentEntity> = emptyList(),
    val profile: ProfileEntity? = null,
    val projectPlans: List<ProjectPlanEntity> = emptyList(),
    val myProjects: List<MyProjectEntity> = emptyList()
)
