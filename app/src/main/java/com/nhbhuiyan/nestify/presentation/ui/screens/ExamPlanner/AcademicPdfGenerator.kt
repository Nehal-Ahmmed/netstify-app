package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object AcademicPdfGenerator {
    
    fun generateTermPdf(
        context: Context,
        level: Int,
        term: Int,
        gpa: Float,
        courses: List<CourseGrade>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        var yPosition = 50f
        
        // Title
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("STATE UNIVERSITY OF TECHNOLOGY", 297f, yPosition, paint)
        yPosition += 25f
        
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("OFFICIAL TERM ACADEMIC TRANSCRIPT", 297f, yPosition, paint)
        yPosition += 40f
        
        // Metadata (Left Align)
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Student Name: Nehal Ahmed", 50f, yPosition, paint)
        canvas.drawText("Academic Term: Level $level Term $term", 350f, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Student ID: 2021-CSE-087", 50f, yPosition, paint)
        canvas.drawText("Batch / Session: Batch 48 / 2024-2025", 350f, yPosition, paint)
        yPosition += 40f

        // Table Header
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Code", 50f, yPosition, paint)
        canvas.drawText("Course Title", 130f, yPosition, paint)
        canvas.drawText("Credits", 380f, yPosition, paint)
        canvas.drawText("Grade", 440f, yPosition, paint)
        canvas.drawText("GP", 500f, yPosition, paint)
        yPosition += 10f
        canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
        yPosition += 20f

        // Draw Rows
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        courses.forEach { course ->
            canvas.drawText(course.subjectCode, 50f, yPosition, paint)
            
            // Draw truncated name if too long
            val displayName = if (course.subjectName.length > 32) course.subjectName.take(30) + ".." else course.subjectName
            canvas.drawText(displayName, 130f, yPosition, paint)
            
            canvas.drawText(course.credits.toString(), 380f, yPosition, paint)
            canvas.drawText(course.selectedGrade, 440f, yPosition, paint)
            
            val gp = AcademicGradingEngine.gradeToGp(course.selectedGrade)
            canvas.drawText(String.format("%.2f", gp), 500f, yPosition, paint)
            
            yPosition += 25f
        }
        
        yPosition += 15f
        canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
        yPosition += 25f

        // Summary result
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("TERM GRADE POINT AVERAGE (GPA):", 50f, yPosition, paint)
        canvas.drawText(String.format("%.2f", gpa), 500f, yPosition, paint)
        
        yPosition += 60f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawLine(380f, yPosition, 510f, yPosition, paint)
        yPosition += 15f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Controller of Examinations", 445f, yPosition, paint)

        pdfDocument.finishPage(page)

        // Save to cache directory for direct sharing, and downloads directory
        val cacheFile = File(context.cacheDir, "Nestify_L${level}T${term}_Transcript.pdf")
        val downloadsFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Nestify_L${level}T${term}_Transcript.pdf"
        )

        return try {
            FileOutputStream(cacheFile).use { pdfDocument.writeTo(it) }
            FileOutputStream(downloadsFile).use { pdfDocument.writeTo(it) }
            downloadsFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            pdfDocument.close()
        }
    }

    fun sharePdf(context: Context, file: File) {
        try {
            val fileUri = FileProvider.getUriForFile(
                context,
                "com.nhbhuiyan.nestify.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Academic Report"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing report: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
