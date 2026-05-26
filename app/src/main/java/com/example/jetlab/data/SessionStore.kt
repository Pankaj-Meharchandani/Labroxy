package com.example.jetlab.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

data class CachedDashboard(
    val projects: List<GitLabProject> = emptyList(),
    val groups: List<GitLabGroup> = emptyList(),
    val workItems: List<GitLabIssue> = emptyList(),
    val assignedWorkItems: List<GitLabIssue> = emptyList(),
    val assignedMergeRequests: List<GitLabMergeRequest> = emptyList(),
    val todos: List<GitLabTodo> = emptyList(),
    val events: List<GitLabEvent> = emptyList()
)

class SessionStore(private val context: Context) {
    private val hostKey = stringPreferencesKey("host")
    private val tokenKey = stringPreferencesKey("token")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val pushNotificationsKey = booleanPreferencesKey("push_notifications")
    private val projectsCacheKey = stringPreferencesKey("cache_projects")
    private val groupsCacheKey = stringPreferencesKey("cache_groups")
    private val workItemsCacheKey = stringPreferencesKey("cache_work_items")
    private val assignedWorkItemsCacheKey = stringPreferencesKey("cache_assigned_work_items")
    private val assignedMergeRequestsCacheKey = stringPreferencesKey("cache_assigned_merge_requests")
    private val todosCacheKey = stringPreferencesKey("cache_todos")
    private val eventsCacheKey = stringPreferencesKey("cache_events")
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

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

    val cachedDashboard: Flow<CachedDashboard> = context.dataStore.data.map { prefs ->
        CachedDashboard(
            projects = decodeList(prefs[projectsCacheKey], GitLabProject.serializer()),
            groups = decodeList(prefs[groupsCacheKey], GitLabGroup.serializer()),
            workItems = decodeList(prefs[workItemsCacheKey], GitLabIssue.serializer()),
            assignedWorkItems = decodeList(prefs[assignedWorkItemsCacheKey], GitLabIssue.serializer()),
            assignedMergeRequests = decodeList(prefs[assignedMergeRequestsCacheKey], GitLabMergeRequest.serializer()),
            todos = decodeList(prefs[todosCacheKey], GitLabTodo.serializer()),
            events = decodeList(prefs[eventsCacheKey], GitLabEvent.serializer())
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

    suspend fun saveProjects(projects: List<GitLabProject>) {
        context.dataStore.edit { it[projectsCacheKey] = json.encodeToString(projects) }
    }

    suspend fun saveGroups(groups: List<GitLabGroup>) {
        context.dataStore.edit { it[groupsCacheKey] = json.encodeToString(groups) }
    }

    suspend fun saveWorkItems(workItems: List<GitLabIssue>) {
        context.dataStore.edit { it[workItemsCacheKey] = json.encodeToString(workItems) }
    }

    suspend fun saveAssignedWorkItems(workItems: List<GitLabIssue>) {
        context.dataStore.edit { it[assignedWorkItemsCacheKey] = json.encodeToString(workItems) }
    }

    suspend fun saveAssignedMergeRequests(mergeRequests: List<GitLabMergeRequest>) {
        context.dataStore.edit { it[assignedMergeRequestsCacheKey] = json.encodeToString(mergeRequests) }
    }

    suspend fun saveTodos(todos: List<GitLabTodo>) {
        context.dataStore.edit { it[todosCacheKey] = json.encodeToString(todos) }
    }

    suspend fun saveEvents(events: List<GitLabEvent>) {
        context.dataStore.edit { it[eventsCacheKey] = json.encodeToString(events) }
    }

    private fun <T> decodeList(value: String?, serializer: kotlinx.serialization.KSerializer<T>): List<T> =
        runCatching {
            if (value.isNullOrBlank()) emptyList() else json.decodeFromString(ListSerializer(serializer), value)
        }.getOrDefault(emptyList())
}
