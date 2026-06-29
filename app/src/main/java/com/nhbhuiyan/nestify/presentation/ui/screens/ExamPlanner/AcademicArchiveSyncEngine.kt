package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.content.Context
import android.net.Uri
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.data.local.entity.TermReportEntity
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

// Moshi-compatible data structures for Personal Data Backup
data class ClassTestMarkArchive(
    val testIndex: Int,
    val marks: Float
)

data class SubjectArchive(
    val code: String,
    val name: String,
    val credits: Float,
    val level: Int,
    val term: Int,
    val examDate: String,
    val finalGrade: String,
    val attendanceMarks: Float,
    val classTestMarks: List<ClassTestMarkArchive>
)

data class TermReportArchive(
    val level: Int,
    val term: Int,
    val gpa: Float,
    val pdfFileName: String?,
    val timestamp: Long
)

data class PersonalDataArchive(
    val schemaVersion: Int = 2,
    val level: Int,
    val term: Int,
    val subjects: List<SubjectArchive>,
    val termReport: TermReportArchive?
)

object AcademicArchiveSyncEngine {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val archiveAdapter = moshi.adapter(PersonalDataArchive::class.java)

    /**
     * Resolves the actual PDF file path on the system.
     * Looks in the direct path, as well as relatively within the app's cache directory.
     */
    fun resolvePdfFile(context: Context, path: String): File? {
        if (path.isEmpty()) return null
        val fileDirect = File(path)
        if (fileDirect.exists() && fileDirect.isFile) return fileDirect
        
        val fileName = path.substringAfter("cache/")
        val fileInCache = File(context.cacheDir, fileName)
        if (fileInCache.exists() && fileInCache.isFile) return fileInCache
        
        val fileInCacheDirect = File(context.cacheDir, path)
        if (fileInCacheDirect.exists() && fileInCacheDirect.isFile) return fileInCacheDirect
        
        return null
    }

    /**
     * Serializes academic records into a JSON string and archives it with any associated
     * PDF report into a ZIP file output stream.
     */
    fun exportToZip(
        context: Context,
        outputStream: OutputStream,
        level: Int,
        term: Int,
        subjects: List<SubjectEntity>,
        ctMarksMap: Map<Long, List<ClassTestMarkEntity>>,
        termReport: TermReportEntity?
    ): Boolean {
        return try {
            val subjectArchives = subjects.map { subject ->
                val ctMarks = ctMarksMap[subject.id] ?: emptyList()
                val ctArchives = ctMarks.map { ct ->
                    ClassTestMarkArchive(testIndex = ct.testIndex, marks = ct.marks)
                }
                SubjectArchive(
                    code = subject.code,
                    name = subject.name,
                    credits = subject.credits,
                    level = subject.level,
                    term = subject.term,
                    examDate = subject.examDate,
                    finalGrade = subject.finalGrade,
                    attendanceMarks = subject.attendanceMarks,
                    classTestMarks = ctArchives
                )
            }

            var pdfFileName: String? = null
            var pdfFileToZip: File? = null
            if (termReport != null && termReport.pdfLocalPath.isNotEmpty()) {
                val resolved = resolvePdfFile(context, termReport.pdfLocalPath)
                if (resolved != null && resolved.exists() && resolved.isFile) {
                    pdfFileName = resolved.name
                    pdfFileToZip = resolved
                }
            }

            val reportArchive = termReport?.let {
                TermReportArchive(
                    level = it.level,
                    term = it.term,
                    gpa = it.gpa,
                    pdfFileName = pdfFileName,
                    timestamp = it.timestamp
                )
            }

            val archivePayload = PersonalDataArchive(
                level = level,
                term = term,
                subjects = subjectArchives,
                termReport = reportArchive
            )

            val jsonString = archiveAdapter.indent("    ").toJson(archivePayload)

            ZipOutputStream(outputStream).use { zipOut ->
                // 1. Write the main JSON payload
                val jsonEntry = ZipEntry("PersonalArchive.json")
                zipOut.putNextEntry(jsonEntry)
                zipOut.write(jsonString.toByteArray(Charsets.UTF_8))
                zipOut.closeEntry()

                // 2. Write the PDF file if it exists
                if (pdfFileToZip != null && pdfFileName != null) {
                    val pdfEntry = ZipEntry(pdfFileName)
                    zipOut.putNextEntry(pdfEntry)
                    pdfFileToZip.inputStream().use { input ->
                        input.copyTo(zipOut)
                    }
                    zipOut.closeEntry()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Unzips the provided input stream into a temporary directory, parses the JSON payload,
     * extracts any PDF report to the permanent cache, and deletes the temporary directory.
     */
    fun importFromZip(
        context: Context,
        inputStream: InputStream
    ): PersonalDataArchive? {
        var archive: PersonalDataArchive? = null
        var tempDir: File? = null
        try {
            val timestamp = System.currentTimeMillis()
            tempDir = File(context.cacheDir, "temp_restore_$timestamp")
            if (!tempDir.exists()) {
                tempDir.mkdirs()
            }

            ZipInputStream(inputStream).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    val destFile = File(tempDir, entry.name)
                    // Security validation: prevent Zip Slip vulnerability
                    if (!destFile.canonicalPath.startsWith(tempDir.canonicalPath)) {
                        throw SecurityException("Zip entry is outside the target directory: ${entry.name}")
                    }

                    if (entry.isDirectory) {
                        destFile.mkdirs()
                    } else {
                        destFile.parentFile?.mkdirs()
                        destFile.outputStream().use { output ->
                            zipIn.copyTo(output)
                        }
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            val jsonFile = File(tempDir, "PersonalArchive.json")
            if (!jsonFile.exists()) {
                android.util.Log.e("AcademicArchiveSyncEngine", "PersonalArchive.json not found in backup zip!")
                return null
            }

            val jsonString = jsonFile.readText(Charsets.UTF_8)
            archive = archiveAdapter.fromJson(jsonString)

            if (archive == null) return null

            // If a PDF report is present, copy it from the temp folder to the permanent cache directory
            val reportArchive = archive.termReport
            if (reportArchive != null && reportArchive.pdfFileName != null) {
                val extractedPdf = File(tempDir, reportArchive.pdfFileName)
                if (extractedPdf.exists() && extractedPdf.isFile) {
                    val finalPdf = File(context.cacheDir, reportArchive.pdfFileName)
                    extractedPdf.copyTo(finalPdf, overwrite = true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            archive = null
        } finally {
            try {
                // Ensure the secure temporary directory is recursively deleted
                tempDir?.deleteRecursively()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return archive
    }
}
