package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import kotlinx.datetime.Instant
import com.nhbhuiyan.nestify.data.local.AppDataBase
import com.nhbhuiyan.nestify.data.local.entity.NoteEntity
import com.nhbhuiyan.nestify.data.local.entity.LinkEntity
import com.nhbhuiyan.nestify.data.local.entity.ScheduleEntity
import com.nhbhuiyan.nestify.data.local.entity.CategoryEntity
import com.nhbhuiyan.nestify.data.local.entity.MyProjectEntity
import com.nhbhuiyan.nestify.data.local.entity.ProjectPlanEntity
import com.nhbhuiyan.nestify.data.local.entity.ProfileEntity
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream

class InstantAdapter {
    @ToJson
    fun toJson(instant: Instant): String {
        return instant.toString()
    }

    @FromJson
    fun fromJson(value: String): Instant {
        return Instant.parse(value)
    }
}

object GoogleDriveSyncManager {

    // Single canonical root folder for all Nestify Drive data (ZIP backups + structured sync).
    // Previously two divergent literals ("Nestify Backups" here vs "Nestify" in syncAllDataToDrive)
    // meant uploads and the structured sync used different folders.
    private const val ROOT_FOLDER = "Nestify"
    private const val FOLDER_MIME_TYPE = "application/vnd.google-apps.folder"

    private val moshi = Moshi.Builder()
        .add(InstantAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val noteListAdapter = moshi.adapter<List<NoteEntity>>(
        Types.newParameterizedType(List::class.java, NoteEntity::class.java)
    )
    private val linkListAdapter = moshi.adapter<List<LinkEntity>>(
        Types.newParameterizedType(List::class.java, LinkEntity::class.java)
    )
    private val scheduleListAdapter = moshi.adapter<List<ScheduleEntity>>(
        Types.newParameterizedType(List::class.java, ScheduleEntity::class.java)
    )
    private val categoryListAdapter = moshi.adapter<List<CategoryEntity>>(
        Types.newParameterizedType(List::class.java, CategoryEntity::class.java)
    )
    private val myProjectListAdapter = moshi.adapter<List<MyProjectEntity>>(
        Types.newParameterizedType(List::class.java, MyProjectEntity::class.java)
    )
    private val projectPlanListAdapter = moshi.adapter<List<ProjectPlanEntity>>(
        Types.newParameterizedType(List::class.java, ProjectPlanEntity::class.java)
    )
    private val profileListAdapter = moshi.adapter<List<ProfileEntity>>(
        Types.newParameterizedType(List::class.java, ProfileEntity::class.java)
    )
    private val subjectListAdapter = moshi.adapter<List<SubjectEntity>>(
        Types.newParameterizedType(List::class.java, SubjectEntity::class.java)
    )
    private val classTestMarkListAdapter = moshi.adapter<List<ClassTestMarkEntity>>(
        Types.newParameterizedType(List::class.java, ClassTestMarkEntity::class.java)
    )
    private val syllabusTopicListAdapter = moshi.adapter<List<SyllabusTopicEntity>>(
        Types.newParameterizedType(List::class.java, SyllabusTopicEntity::class.java)
    )
    private val termReportListAdapter = moshi.adapter<List<TermReportEntity>>(
        Types.newParameterizedType(List::class.java, TermReportEntity::class.java)
    )

    /**
     * Gets the Google SignIn Client configured for requesting Drive access.
     */
    fun getSignInClient(context: Context): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        return GoogleSignIn.getClient(context, signInOptions)
    }

    /**
     * Builds the Drive service API client.
     */
    private fun getDriveService(context: Context, account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE)
        )
        credential.selectedAccount = account.account

        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
        .setApplicationName("Nestify")
        .build()
    }

    /**
     * Uploads the given file to Google Drive under the Nestify root folder.
     * Returns the uploaded File ID if successful, or null on failure.
     */
    suspend fun uploadToDrive(
        context: Context,
        account: GoogleSignInAccount,
        fileToUpload: File,
        mimeType: String = "application/zip"
    ): String? = withContext(Dispatchers.IO) {
        try {
            val driveService = getDriveService(context, account)

            // 1. Check if the Nestify root folder exists, create it if not
            var folderId = getFolderId(driveService, ROOT_FOLDER)
            if (folderId == null) {
                folderId = createFolder(driveService, ROOT_FOLDER)
            }

            // 2. Prepare file metadata
            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = fileToUpload.name
                if (folderId != null) {
                    parents = listOf(folderId)
                }
            }

            // 3. Prepare file content
            val mediaContent = FileContent(mimeType, fileToUpload)

            // 4. Upload file
            val file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()

            file.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFolderId(driveService: Drive, folderName: String, parentId: String? = null): String? {
        var query = "mimeType='$FOLDER_MIME_TYPE' and name='$folderName' and trashed=false"
        if (parentId != null) {
            query += " and '$parentId' in parents"
        }
        val result = driveService.files().list()
            .setQ(query)
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()
        return result.files.firstOrNull()?.id
    }

    private fun createFolder(driveService: Drive, folderName: String, parentId: String? = null): String? {
        val folderMetadata = com.google.api.services.drive.model.File().apply {
            name = folderName
            mimeType = FOLDER_MIME_TYPE
            if (parentId != null) {
                parents = listOf(parentId)
            }
        }
        val folder = driveService.files().create(folderMetadata)
            .setFields("id")
            .execute()
        return folder.id
    }

    private fun getFileIdInFolder(driveService: Drive, fileName: String, parentId: String): String? {
        val query = "name='$fileName' and '$parentId' in parents and trashed=false"
        val result = driveService.files().list()
            .setQ(query)
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()
        return result.files.firstOrNull()?.id
    }

    private fun uploadOrUpdateFile(
        driveService: Drive,
        fileName: String,
        mimeType: String,
        contentFile: File,
        parentId: String
    ): String? {
        val existingFileId = getFileIdInFolder(driveService, fileName, parentId)
        val mediaContent = FileContent(mimeType, contentFile)

        return if (existingFileId != null) {
            val fileMetadata = com.google.api.services.drive.model.File()
            val file = driveService.files().update(existingFileId, fileMetadata, mediaContent)
                .setFields("id")
                .execute()
            file.id
        } else {
            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = fileName
                parents = listOf(parentId)
            }
            val file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()
            file.id
        }
    }

    /**
     * Performs a full structured sync of all user databases to a central "Nestify" directory in Google Drive.
     */
    suspend fun syncAllDataToDrive(
        context: Context,
        account: GoogleSignInAccount,
        database: AppDataBase,
        onProgress: (String) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            onProgress("Connecting to Drive...")
            val driveService = getDriveService(context, account)

            onProgress("Creating Nestify root folder...")
            var rootFolderId = getFolderId(driveService, ROOT_FOLDER)
            if (rootFolderId == null) {
                rootFolderId = createFolder(driveService, ROOT_FOLDER)
            }
            if (rootFolderId == null) return@withContext false

            onProgress("Setting up folder structure...")
            val notesFolderId = getFolderId(driveService, "Notes", rootFolderId) ?: createFolder(driveService, "Notes", rootFolderId)
            val linksFolderId = getFolderId(driveService, "Links", rootFolderId) ?: createFolder(driveService, "Links", rootFolderId)
            val schedulesFolderId = getFolderId(driveService, "Schedules", rootFolderId) ?: createFolder(driveService, "Schedules", rootFolderId)
            val projectsFolderId = getFolderId(driveService, "Projects", rootFolderId) ?: createFolder(driveService, "Projects", rootFolderId)
            val profileFolderId = getFolderId(driveService, "Profile", rootFolderId) ?: createFolder(driveService, "Profile", rootFolderId)
            val academicFolderId = getFolderId(driveService, "Academic", rootFolderId) ?: createFolder(driveService, "Academic", rootFolderId)

            if (notesFolderId == null || linksFolderId == null || schedulesFolderId == null ||
                projectsFolderId == null || profileFolderId == null || academicFolderId == null
            ) return@withContext false

            onProgress("Syncing Notes...")
            val notes = database.contentDao().getAllNotes().first()
            val notesJson = noteListAdapter.indent("  ").toJson(notes)
            val notesFile = writeStringToTempFile(context, "notes.json", notesJson)
            uploadOrUpdateFile(driveService, "notes.json", "application/json", notesFile, notesFolderId)
            notesFile.delete()

            notes.forEach { note ->
                val safeTitle = note.title.replace(Regex("[\\\\/:*?\"<>|]"), "_")
                if (safeTitle.isNotEmpty()) {
                    val noteContentFile = writeStringToTempFile(context, "$safeTitle.txt", note.content)
                    uploadOrUpdateFile(driveService, "$safeTitle.txt", "text/plain", noteContentFile, notesFolderId)
                    noteContentFile.delete()
                }
            }

            onProgress("Syncing Links...")
            val links = database.contentDao().getAllLinks().first()
            val linksJson = linkListAdapter.indent("  ").toJson(links)
            val linksFile = writeStringToTempFile(context, "links.json", linksJson)
            uploadOrUpdateFile(driveService, "links.json", "application/json", linksFile, linksFolderId)
            linksFile.delete()

            onProgress("Syncing Schedules...")
            val schedules = database.scheduleDao().getAllScheduleItems()
            val schedulesJson = scheduleListAdapter.indent("  ").toJson(schedules)
            val schedulesFile = writeStringToTempFile(context, "schedules.json", schedulesJson)
            uploadOrUpdateFile(driveService, "schedules.json", "application/json", schedulesFile, schedulesFolderId)
            schedulesFile.delete()

            val categories = database.scheduleDao().getAllCategories().first()
            val categoriesJson = categoryListAdapter.indent("  ").toJson(categories)
            val categoriesFile = writeStringToTempFile(context, "categories.json", categoriesJson)
            uploadOrUpdateFile(driveService, "categories.json", "application/json", categoriesFile, schedulesFolderId)
            categoriesFile.delete()

            onProgress("Syncing Projects...")
            val projects = database.myProjectDao().getAllMyProjects().first()
            val projectsJson = myProjectListAdapter.indent("  ").toJson(projects)
            val projectsFile = writeStringToTempFile(context, "projects.json", projectsJson)
            uploadOrUpdateFile(driveService, "projects.json", "application/json", projectsFile, projectsFolderId)
            projectsFile.delete()

            val plans = database.projectPlanDao().getAllProjectPlans().first()
            val plansJson = projectPlanListAdapter.indent("  ").toJson(plans)
            val plansFile = writeStringToTempFile(context, "plans.json", plansJson)
            uploadOrUpdateFile(driveService, "plans.json", "application/json", plansFile, projectsFolderId)
            plansFile.delete()

            onProgress("Syncing Profile...")
            val profile = database.profileDao().getProfile()
            val profileList = if (profile != null) listOf(profile) else emptyList()
            val profileJson = profileListAdapter.indent("  ").toJson(profileList)
            val profileFile = writeStringToTempFile(context, "profile.json", profileJson)
            uploadOrUpdateFile(driveService, "profile.json", "application/json", profileFile, profileFolderId)
            profileFile.delete()

            onProgress("Syncing Academic...")
            val subjects = database.examPlannerDao().getAllSubjects().first()
            val subjectsJson = subjectListAdapter.indent("  ").toJson(subjects)
            val subjectsFile = writeStringToTempFile(context, "subjects.json", subjectsJson)
            uploadOrUpdateFile(driveService, "subjects.json", "application/json", subjectsFile, academicFolderId)
            subjectsFile.delete()

            val termReports = database.examPlannerDao().getAllTermReports().first()
            val reportsJson = termReportListAdapter.indent("  ").toJson(termReports)
            val reportsFile = writeStringToTempFile(context, "term_reports.json", reportsJson)
            uploadOrUpdateFile(driveService, "term_reports.json", "application/json", reportsFile, academicFolderId)
            reportsFile.delete()

            val allCTMarks = mutableListOf<ClassTestMarkEntity>()
            val allSyllabusTopics = mutableListOf<SyllabusTopicEntity>()
            subjects.forEach { subject ->
                val ctMarks = database.examPlannerDao().getCTMarksForSubject(subject.id).first()
                allCTMarks.addAll(ctMarks)
                val topics = database.examPlannerDao().getSyllabusTopicsForSubject(subject.id).first()
                allSyllabusTopics.addAll(topics)
            }

            val ctJson = classTestMarkListAdapter.indent("  ").toJson(allCTMarks)
            val ctFile = writeStringToTempFile(context, "class_tests.json", ctJson)
            uploadOrUpdateFile(driveService, "class_tests.json", "application/json", ctFile, academicFolderId)
            ctFile.delete()

            val syllabusJson = syllabusTopicListAdapter.indent("  ").toJson(allSyllabusTopics)
            val syllabusFile = writeStringToTempFile(context, "syllabus_topics.json", syllabusJson)
            uploadOrUpdateFile(driveService, "syllabus_topics.json", "application/json", syllabusFile, academicFolderId)
            syllabusFile.delete()

            termReports.forEach { report ->
                if (report.pdfLocalPath.isNotEmpty()) {
                    val resolved = AcademicArchiveSyncEngine.resolvePdfFile(context, report.pdfLocalPath)
                    if (resolved != null && resolved.exists() && resolved.isFile) {
                        uploadOrUpdateFile(driveService, resolved.name, "application/pdf", resolved, academicFolderId)
                    }
                }
            }

            onProgress("Sync Completed!")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Restores all data from the structured Google Drive backup folder.
     */
    suspend fun restoreAllFromDrive(
        context: Context,
        account: GoogleSignInAccount,
        database: AppDataBase,
        onProgress: (String) -> Unit
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            onProgress("Connecting to Drive...")
            val driveService = getDriveService(context, account)

            val rootFolderId = getFolderId(driveService, ROOT_FOLDER)
            if (rootFolderId == null) {
                onProgress("No Nestify backup folder found.")
                return@withContext false
            }

            val notesFolderId = getFolderId(driveService, "Notes", rootFolderId)
            val linksFolderId = getFolderId(driveService, "Links", rootFolderId)
            val schedulesFolderId = getFolderId(driveService, "Schedules", rootFolderId)
            val projectsFolderId = getFolderId(driveService, "Projects", rootFolderId)
            val profileFolderId = getFolderId(driveService, "Profile", rootFolderId)
            val academicFolderId = getFolderId(driveService, "Academic", rootFolderId)

            onProgress("Preparing Database...")
            database.clearAllTables()

            if (profileFolderId != null) {
                onProgress("Restoring Profile...")
                val profileId = getFileIdInFolder(driveService, "profile.json", profileFolderId)
                if (profileId != null) {
                    val jsonStr = downloadFileContent(driveService, profileId)
                    val profileList = profileListAdapter.fromJson(jsonStr)
                    profileList?.firstOrNull()?.let {
                        database.profileDao().insertOrUpdate(it)
                    }
                }
            }

            if (notesFolderId != null) {
                onProgress("Restoring Notes...")
                val notesFileId = getFileIdInFolder(driveService, "notes.json", notesFolderId)
                if (notesFileId != null) {
                    val jsonStr = downloadFileContent(driveService, notesFileId)
                    val notes = noteListAdapter.fromJson(jsonStr)
                    notes?.forEach {
                        database.contentDao().insertNote(it)
                    }
                }
            }

            if (linksFolderId != null) {
                onProgress("Restoring Links...")
                val linksFileId = getFileIdInFolder(driveService, "links.json", linksFolderId)
                if (linksFileId != null) {
                    val jsonStr = downloadFileContent(driveService, linksFileId)
                    val links = linkListAdapter.fromJson(jsonStr)
                    links?.forEach {
                        database.contentDao().insertLink(it)
                    }
                }
            }

            if (schedulesFolderId != null) {
                onProgress("Restoring Schedules...")
                val categoriesFileId = getFileIdInFolder(driveService, "categories.json", schedulesFolderId)
                if (categoriesFileId != null) {
                    val jsonStr = downloadFileContent(driveService, categoriesFileId)
                    val categories = categoryListAdapter.fromJson(jsonStr)
                    categories?.forEach {
                        database.scheduleDao().insertCategory(it)
                    }
                }

                val schedulesFileId = getFileIdInFolder(driveService, "schedules.json", schedulesFolderId)
                if (schedulesFileId != null) {
                    val jsonStr = downloadFileContent(driveService, schedulesFileId)
                    val schedules = scheduleListAdapter.fromJson(jsonStr)
                    schedules?.forEach {
                        database.scheduleDao().insertScheduleItem(it)
                    }
                }
            }

            if (projectsFolderId != null) {
                onProgress("Restoring Projects...")
                val projectsFileId = getFileIdInFolder(driveService, "projects.json", projectsFolderId)
                if (projectsFileId != null) {
                    val jsonStr = downloadFileContent(driveService, projectsFileId)
                    val projects = myProjectListAdapter.fromJson(jsonStr)
                    projects?.forEach {
                        database.myProjectDao().insertMyProject(it)
                    }
                }

                val plansFileId = getFileIdInFolder(driveService, "plans.json", projectsFolderId)
                if (plansFileId != null) {
                    val jsonStr = downloadFileContent(driveService, plansFileId)
                    val plans = projectPlanListAdapter.fromJson(jsonStr)
                    plans?.forEach {
                        database.projectPlanDao().insertProjectPlan(it)
                    }
                }
            }

            if (academicFolderId != null) {
                onProgress("Restoring Academic...")
                val subjectsFileId = getFileIdInFolder(driveService, "subjects.json", academicFolderId)
                if (subjectsFileId != null) {
                    val jsonStr = downloadFileContent(driveService, subjectsFileId)
                    val subjects = subjectListAdapter.fromJson(jsonStr)
                    subjects?.forEach {
                        database.examPlannerDao().insertSubject(it)
                    }
                }

                val ctFileId = getFileIdInFolder(driveService, "class_tests.json", academicFolderId)
                if (ctFileId != null) {
                    val jsonStr = downloadFileContent(driveService, ctFileId)
                    val ctMarks = classTestMarkListAdapter.fromJson(jsonStr)
                    ctMarks?.forEach {
                        database.examPlannerDao().insertCTMark(it)
                    }
                }

                val syllabusFileId = getFileIdInFolder(driveService, "syllabus_topics.json", academicFolderId)
                if (syllabusFileId != null) {
                    val jsonStr = downloadFileContent(driveService, syllabusFileId)
                    val topics = syllabusTopicListAdapter.fromJson(jsonStr)
                    topics?.forEach {
                        database.examPlannerDao().insertSyllabusTopic(it)
                    }
                }

                val reportsFileId = getFileIdInFolder(driveService, "term_reports.json", academicFolderId)
                if (reportsFileId != null) {
                    val jsonStr = downloadFileContent(driveService, reportsFileId)
                    val reports = termReportListAdapter.fromJson(jsonStr)
                    reports?.forEach {
                        database.examPlannerDao().insertTermReport(it)
                        if (it.pdfLocalPath.isNotEmpty()) {
                            val resolvedName = it.pdfLocalPath.substringAfter("cache/")
                            val pdfFileId = getFileIdInFolder(driveService, resolvedName, academicFolderId)
                            if (pdfFileId != null) {
                                val destinationFile = File(context.cacheDir, resolvedName)
                                downloadFileToLocal(driveService, pdfFileId, destinationFile)
                            }
                        }
                    }
                }
            }

            onProgress("Restore Completed Successfully!")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun downloadFileContent(driveService: Drive, fileId: String): String {
        val outputStream = ByteArrayOutputStream()
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream)
        return outputStream.toString("UTF-8")
    }

    private fun downloadFileToLocal(driveService: Drive, fileId: String, destFile: File) {
        FileOutputStream(destFile).use { outputStream ->
            driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream)
        }
    }

    private fun writeStringToTempFile(context: Context, fileName: String, content: String): File {
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { output ->
            output.write(content.toByteArray(Charsets.UTF_8))
        }
        return file
    }
}
