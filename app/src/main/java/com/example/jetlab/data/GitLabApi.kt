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
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
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
            requestTimeoutMillis = 20_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 20_000
        }
        install(DefaultRequest) {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
    }

    suspend fun currentUser(): GitLabUser = get("user")

    suspend fun projects(query: String): List<GitLabProject> =
        get("projects") {
            parameter("membership", true)
            parameter("order_by", "last_activity_at")
            parameter("sort", "desc")
            parameter("simple", false)
            parameter("per_page", 40)
            if (query.isNotBlank()) parameter("search", query)
        }

    suspend fun project(projectId: Long): GitLabProject = get("projects/$projectId")

    suspend fun issues(projectId: Long): List<GitLabIssue> =
        get("projects/$projectId/issues") {
            parameter("state", "opened")
            parameter("order_by", "updated_at")
            parameter("sort", "desc")
            parameter("per_page", 30)
        }

    suspend fun mergeRequests(projectId: Long): List<GitLabMergeRequest> =
        get("projects/$projectId/merge_requests") {
            parameter("state", "opened")
            parameter("order_by", "updated_at")
            parameter("sort", "desc")
            parameter("per_page", 30)
        }

    suspend fun commits(projectId: Long): List<GitLabCommit> =
        get("projects/$projectId/repository/commits") {
            parameter("per_page", 30)
        }

    suspend fun branches(projectId: Long): List<GitLabBranch> =
        get("projects/$projectId/repository/branches") {
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
}
