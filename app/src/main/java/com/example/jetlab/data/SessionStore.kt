package com.example.jetlab.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("jetlab_session")

data class GitLabSession(
    val host: String = "https://gitlab.com",
    val token: String = "",
    val isLoaded: Boolean = false
) {
    val isReady: Boolean get() = isLoaded && host.isNotBlank() && token.isNotBlank()
}

data class AppSettings(
    val themeMode: String = "system",
    val pushNotifications: Boolean = false
)

class SessionStore(private val context: Context) {
    private val hostKey = stringPreferencesKey("host")
    private val tokenKey = stringPreferencesKey("token")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val pushNotificationsKey = booleanPreferencesKey("push_notifications")

    val session: Flow<GitLabSession> = context.dataStore.data.map { prefs ->
        GitLabSession(
            host = prefs[hostKey] ?: "https://gitlab.com",
            token = prefs[tokenKey] ?: "",
            isLoaded = true
        )
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[themeModeKey] ?: "system",
            pushNotifications = prefs[pushNotificationsKey] ?: false
        )
    }

    suspend fun save(host: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[hostKey] = host.trim().removeSuffix("/")
            prefs[tokenKey] = token.trim()
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(tokenKey)
        }
    }

    suspend fun saveThemeMode(themeMode: String) {
        context.dataStore.edit { prefs ->
            prefs[themeModeKey] = themeMode
        }
    }

    suspend fun savePushNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[pushNotificationsKey] = enabled
        }
    }
}
