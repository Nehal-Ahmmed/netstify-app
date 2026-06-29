package com.nhbhuiyan.nestify

import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.nhbhuiyan.nestify.domain.manager.AppSettingManager
import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import com.nhbhuiyan.nestify.presentation.navigation.NavGraph
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var appSettingManager: AppSettingManager

    @Inject
    lateinit var contentRepository: ContentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle incoming intent (e.g., Share)
        handleIntent(intent)

        // Add exception handler
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Log.e("CRASH", "App crashed: ${exception.message}", exception)
        }

        setContent {
            val themeMode by appSettingManager.themeMode.collectAsState(initial = "system")
            val darktheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            NestifyTheme(darkTheme = darktheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (sharedText != null) {
                val domain = try {
                    java.net.URL(sharedText).host
                } catch (e: Exception) {
                    "unknown"
                }
                val link = Link(
                    url = sharedText,
                    domain = domain,
                    title = "Shared Link",
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now()
                )
                lifecycleScope.launch {
                    try {
                        contentRepository.createLink(link)
                        Toast.makeText(this@MainActivity, "Link saved to Nestify!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Failed to save link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}