package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.content.Context
import android.net.Uri
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity
import com.nhbhuiyan.nestify.data.local.entity.ClassTestMarkEntity
import com.nhbhuiyan.nestify.data.local.entity.SyllabusTopicEntity

object AcademicArchiveSyncEngine {

    /**
     * Compile academic records into a single unified JSON structure using Room entities
     */
    fun exportSemesterPackage(
        level: Int,
        term: Int,
        gpa: Float,
        subjects: List<SubjectEntity>,
        ctMarksMap: Map<Long, List<ClassTestMarkEntity>>,
        topicsMap: Map<Long, List<SyllabusTopicEntity>>
    ): String {
        try {
            val root = JSONObject()
            root.put("schema_version", 2)
            root.put("level", level)
            root.put("term", term)
            root.put("term_gpa", gpa.toDouble())
            root.put("timestamp", System.currentTimeMillis())

            // Courses Array
            val coursesArray = JSONArray()
            subjects.forEach { subject ->
                val courseObj = JSONObject()
                courseObj.put("code", subject.code)
                courseObj.put("name", subject.name)
                courseObj.put("credits", subject.credits.toDouble())
                courseObj.put("grade", subject.finalGrade)
                courseObj.put("attendance_marks", subject.attendanceMarks.toDouble())
                courseObj.put("exam_date", subject.examDate)

                // Nested Class Test Marks Array
                val ctArray = JSONArray()
                val ctMarks = ctMarksMap[subject.id] ?: emptyList()
                ctMarks.forEach { ct ->
                    val ctObj = JSONObject()
                    ctObj.put("test_index", ct.testIndex)
                    ctObj.put("marks", ct.marks.toDouble())
                    ctArray.put(ctObj)
                }
                courseObj.put("class_test_marks", ctArray)

                // Nested Syllabus Topics Array
                val topicsArray = JSONArray()
                val topics = topicsMap[subject.id] ?: emptyList()
                topics.forEach { topic ->
                    val topicObj = JSONObject()
                    topicObj.put("title", topic.title)
                    topicObj.put("section", topic.section)
                    topicObj.put("is_completed", topic.isCompleted)
                    topicObj.put("is_revised", topic.isRevised)
                    topicObj.put("priority", topic.priority)
                    topicsArray.put(topicObj)
                }
                courseObj.put("syllabus_topics", topicsArray)

                coursesArray.put(courseObj)
            }
            root.put("courses", coursesArray)

            return root.toString(4) // 4 spaces indentation
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * Write package content to a user-selected Storage Access Framework target URI
     */
    fun writePackageToUri(context: Context, uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(content)
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error writing package: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Read package content from a user-selected SAF target URI
     */
    fun readPackageFromUri(context: Context, uri: Uri): String {
        val stringBuilder = StringBuilder()
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line).append("\n")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error reading package: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return stringBuilder.toString()
    }
}
