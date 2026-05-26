package com.example.jetlab.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("jetlab_session")

data class GitLabSession(
    val host: String = "https://gitlab.com",
    val token: String = ""
) {
    val isReady: Boolean get() = host.isNotBlank() && token.isNotBlank()
}

class SessionStore(private val context: Context) {
    private val hostKey = stringPreferencesKey("host")
    private val tokenKey = stringPreferencesKey("token")

    val session: Flow<GitLabSession> = context.dataStore.data.map { prefs ->
        GitLabSession(
            host = prefs[hostKey] ?: "https://gitlab.com",
            token = prefs[tokenKey] ?: ""
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
}
