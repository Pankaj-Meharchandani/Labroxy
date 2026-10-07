/**
 * File: MainActivity.kt
 *
 * What it does:
 * Root Activity and main entry point of the Labroxy Android application. Handles app launch, intent
 * handling (deep links via ACTION_VIEW and shared text via ACTION_SEND), URL extraction from text content,
 * and initializes the Jetpack Compose UI content tree via LabroxyApp.
 *
 * Touchpoints:
 * - AndroidManifest.xml: Declared as the main launcher activity and handler for deep link intent filters.
 * - com.example.labroxy.ui.LabroxyApp: Instantiates and hosts the top-level Compose app component.
 * - Android System Intents: Receives VIEW and SEND intents for deep linking and text sharing.
 *
 * Features / Functions:
 * - Application lifecycle initialization (onCreate, onNewIntent).
 * - Direct deep-link URL parsing and routing into the app's detail views.
 * - Processing shared text containing GitLab URLs ("Share to Labroxy").
 */
package com.example.labroxy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.labroxy.ui.LabroxyApp

class MainActivity : ComponentActivity() {
    private var deepLinkUrl by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            LabroxyApp(deepLinkUrl = deepLinkUrl)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        deepLinkUrl = when (intent?.action) {
            Intent.ACTION_VIEW -> intent.dataString
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                    text?.let { extractUrl(it) }
                } else null
            }
            else -> null
        }
    }

    private fun extractUrl(text: String): String? {
        val pattern = Regex("""https?://[^\s]+""")
        return pattern.find(text)?.value
    }
}
