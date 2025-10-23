package com.nhbhuiyan.nestify

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nhbhuiyan.nestify.presentation.navigation.NavGraph
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Add exception handler
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e("CRASH", "App crashed: ${exception.message}", exception)
            // You can also save this to a file or send to server
        }

        setContent {
            NestifyTheme {

                NavGraph()
                //NotesScreen()
            }
        }
    }
}