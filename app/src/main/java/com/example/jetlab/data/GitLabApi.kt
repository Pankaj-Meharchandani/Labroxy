package com.example.jetlab.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import java.net.URLEncoder
import kotlinx.serialization.json.Json

class GitLabApi(
    private val host: String,
    private val tokenProvider: () -> String
) {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 45_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 45_000
        }
        install(DefaultRequest) {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
    }

    suspend fun currentUser(): GitLabUser = get("user")

    suspend fun getUserByUsername(username: String): GitLabUser? =
        getList<GitLabUser>("users") {
            parameter("username", username)
        }.firstOrNull()

    suspend fun projects(query: String): List<GitLabProject> =
        getList("projects") {
            parameter("membership", true)
            parameter("order_by", if (query.isBlank()) "last_activity_at" else "similarity")
            parameter("sort", "desc")
            parameter("per_page", 100)
            if (query.isNotBlank()) parameter("search", query)
        }

    suspend fun groups(query: String): List<GitLabGroup> =
        getList("groups") {
            parameter("order_by", "name")
            parameter("sort", "asc")
            parameter("per_page", 100)
            if (query.isNotBlank()) parameter("search", query)
        }

    suspend fun todos(): List<GitLabTodo> =
        getList("todos") {
            parameter("state", "pending")
            parameter("per_page", 100)
        }

    suspend fun assignedIssues(userId: Long, state: String = "opened"): List<GitLabIssue> =
        getList("issues") {
            parameter("scope", "all")
            parameter("state", state)
            parameter("assignee_id", userId)
            parameter("order_by", "updated_at")
            parameter("sort", "desc")
            parameter("per_page", 100)
        }

    suspend fun assignedMergeRequests(userId: Long): List<GitLabMergeRequest> =
        getList("merge_requests") {
            parameter("scope", "all")
            parameter("state", "opened")
            parameter("assignee_id", userId)
            parameter("order_by", "updated_at")
            parameter("sort", "desc")
            parameter("per_page", 100)
        }

    suspend fun events(): List<GitLabEvent> =
        getList("events") {
            parameter("per_page", 50)
        }

    suspend fun project(projectId: Long): GitLabProject = get("projects/$projectId")

    suspend fun project(projectPath: String): GitLabProject =
        get("projects/${projectPath.gitLabPathEncoded()}")

    suspend fun groupProjects(groupId: Long): List<GitLabProject> =
        getList("groups/$groupId/projects") {
            parameter("include_subgroups", true)
            parameter("with_shared", true)
            parameter("order_by", "name")
            parameter("sort", "asc")
            parameter("simple", false)
            parameter("per_page", 100)
        }

    suspend fun subgroups(groupId: Long): List<GitLabGroup> =
        getList("groups/$groupId/subgroups") {
            parameter("order_by", "name")
            parameter("sort", "asc")
            parameter("per_page", 100)
        }

    suspend fun groupIssues(groupId: Long): List<GitLabIssue> =
        getList("groups/$groupId/issues") {
            parameter("membership", true)
            parameter("scope", "all")
            parameter("state", "opened")
            parameter("order_by", "updated_at")
            parameter("sort", "desc")
            parameter("per_page", 100)
        }

    suspend fun issue(projectId: Long, issueIid: Long): GitLabIssue =
        get("projects/$projectId/issues/$issueIid")

    suspend fun mergeRequest(projectId: Long, mergeRequestIid: Long): GitLabMergeRequest =
        get("projects/$projectId/merge_requests/$mergeRequestIid")

    suspend fun issueNotes(projectId: Long, issueIid: Long): List<GitLabNote> =
        getList("projects/$projectId/issues/$issueIid/notes") {
            parameter("sort", "asc")
            parameter("per_page", 100)
        }

    suspend fun mergeRequestNotes(projectId: Long, mergeRequestIid: Long): List<GitLabNote> =
        getList("projects/$projectId/merge_requests/$mergeRequestIid/notes") {
            parameter("sort", "asc")
            parameter("per_page", 100)
        }

    suspend fun addIssueNote(projectId: Long, issueIid: Long, body: String): GitLabNote =
        post("projects/$projectId/issues/$issueIid/notes", mapOf("body" to body))

    suspend fun addMergeRequestNote(projectId: Long, mergeRequestIid: Long, body: String): GitLabNote =
        post("projects/$projectId/merge_requests/$mergeRequestIid/notes", mapOf("body" to body))

    suspend fun updateIssue(projectId: Long, issueIid: Long, stateEvent: String?, labels: String?): GitLabIssue =
        put("projects/$projectId/issues/$issueIid") {
            stateEvent?.takeIf { it.isNotBlank() }?.let { parameter("state_event", it) }
            labels?.let { parameter("labels", it) }
        }

    suspend fun issues(projectId: Long): List<GitLabIssue> =
        getList("projects/$projectId/issues") {
            parameter("state", "opened")
            parameter("order_by", "updated_at")
            parameter("sort", "desc")
            parameter("per_page", 30)
        }

    suspend fun mergeRequests(projectId: Long): List<GitLabMergeRequest> =
        getList("projects/$projectId/merge_requests") {
            parameter("state", "opened")
            parameter("order_by", "updated_at")
            parameter("sort", "desc")
            parameter("per_page", 30)
        }

    suspend fun commits(projectId: Long): List<GitLabCommit> =
        getList("projects/$projectId/repository/commits") {
            parameter("per_page", 30)
        }

    suspend fun branches(projectId: Long): List<GitLabBranch> =
        getList("projects/$projectId/repository/branches") {
            parameter("per_page", 50)
        }

    suspend fun boards(projectId: Long): List<GitLabBoard> =
        getList("projects/$projectId/boards") {
            parameter("per_page", 50)
        }

    private suspend inline fun <reified T> get(
        path: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): T {
        val normalizedHost = host.trim().removeSuffix("/")
        val url = "$normalizedHost/api/v4/$path"
        return client.get(url) {
            header("PRIVATE-TOKEN", tokenProvider())
            block()
        }.body()
    }

    private suspend inline fun <reified T> post(path: String, body: Any): T {
        val normalizedHost = host.trim().removeSuffix("/")
        val url = "$normalizedHost/api/v4/$path"
        return client.post(url) {
            header("PRIVATE-TOKEN", tokenProvider())
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    private suspend inline fun <reified T> put(
        path: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): T {
        val normalizedHost = host.trim().removeSuffix("/")
        val url = "$normalizedHost/api/v4/$path"
        return client.put(url) {
            header("PRIVATE-TOKEN", tokenProvider())
            block()
        }.body()
    }

    private suspend inline fun <reified T> getList(
        path: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): List<T> {
        val normalizedHost = host.trim().removeSuffix("/")
        val url = "$normalizedHost/api/v4/$path"
        val items = mutableListOf<T>()
        var page = 1

        do {
            try {
                val response = client.get(url) {
                    header("PRIVATE-TOKEN", tokenProvider())
                    block()
                    parameter("page", page)
                }
                items += response.body<List<T>>()
                page = response.headers["X-Next-Page"]?.toIntOrNull() ?: 0
            } catch (error: Throwable) {
                if (items.isEmpty()) throw error
                page = 0
            }
        } while (page > 0)

        return items
    }
}

private fun String.gitLabPathEncoded(): String =
    URLEncoder.encode(this, Charsets.UTF_8.name()).replace("+", "%20")
