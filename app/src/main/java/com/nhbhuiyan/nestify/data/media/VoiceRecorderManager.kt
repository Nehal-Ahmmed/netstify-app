package com.nhbhuiyan.nestify.data.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class VoiceRecorderManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFilePath: String? = null

    fun startRecording(fileName: String) {
        val audioDir = File(context.filesDir, "audio_records")
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }

        val outputFile = File(audioDir, "$fileName.3gp")
        outputFilePath = outputFile.absolutePath

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFilePath)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                // Handle exception
            }
        }
    }

    fun stopRecording(): String? {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // Handle exception
        } finally {
            mediaRecorder = null
        }
        return outputFilePath
    }
}
