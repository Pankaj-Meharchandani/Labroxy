package com.example.labroxy.ui.util

import android.net.Uri
import com.example.labroxy.ui.DetailTarget
import com.example.labroxy.ui.WorkDetailData

data class CommentAttachment(
    val url: String,
    val openUrl: String,
    val label: String,
    val isImage: Boolean
)

data class RawCommentAttachment(
    val url: String,
    val label: String?,
    val isImageHint: Boolean
)

data class ResolvedCommentAttachment(
    val loadUrl: String,
    val openUrl: String
)

data class IssueReference(
    val projectPath: String?,
    val projectId: Long,
    val issueIid: Long,
    val label: String
)

data class ReplyTarget(
    val discussionId: String,
    val authorName: String
)

sealed class NoteSegment {
    data class Text(val text: String) : NoteSegment()
    data class Image(val attachment: CommentAttachment) : NoteSegment()
}

val markdownImagePattern = Regex("""!\[([^\]]*)]\(([^)\s]+)(?:\s+"[^"]*")?\)(?:\{[^}]*\})?""")
val markdownLinkPattern = Regex("""(?<!!)\[([^\]]*)]\(([^)\s]+)(?:\s+"[^"]*")?\)""")
val htmlImagePattern = Regex("""<img\b[^>]*\bsrc=(["'])(.*?)\1[^>]*>""", RegexOption.IGNORE_CASE)
val htmlTagPattern = Regex("""<[^>]*>""")
val bareUrlPattern = Regex("""https?://[^\s)]+""")
val sameProjectIssuePattern = Regex("""(?<![\w/])#(\d+)""")
val crossProjectIssuePattern = Regex("""(?<![\w/.-])([A-Za-z0-9_.-]+(?:/[A-Za-z0-9_.-]+)+)#(\d+)""")
val issueUrlPattern = Regex("""(?:https?://[^/\s)]+/)?([A-Za-z0-9_.-]+(?:/[A-Za-z0-9_.-]+)+)/-/issues/(\d+)""")

fun String.stripHtmlTags(): String = replace(htmlTagPattern, "").trim()

fun extractCommentAttachments(
    body: String,
    host: String,
    detailWebUrl: String?,
    projectId: Long?
): List<CommentAttachment> {
    val markdownImages = markdownImagePattern.findAll(body).mapNotNull { match ->
        val label = match.groups[1]?.value?.takeIf { it.isNotBlank() }
        val url = match.groups[2]?.value ?: return@mapNotNull null
        RawCommentAttachment(url = url, label = label, isImageHint = true)
    }
    val markdownLinks = markdownLinkPattern.findAll(body).mapNotNull { match ->
        val label = match.groups[1]?.value?.takeIf { it.isNotBlank() }
        val url = match.groups[2]?.value ?: return@mapNotNull null
        RawCommentAttachment(url = url, label = label, isImageHint = false)
    }
    val htmlImages = htmlImagePattern.findAll(body).mapNotNull { match ->
        val url = match.groups[2]?.value ?: return@mapNotNull null
        RawCommentAttachment(url = url, label = null, isImageHint = true)
    }
    val bareUrls = bareUrlPattern.findAll(body).map {
        RawCommentAttachment(url = it.value.trimEnd('.', ',', ')'), label = null, isImageHint = false)
    }

    return (markdownImages + markdownLinks + htmlImages + bareUrls)
        .mapNotNull { raw ->
            val resolved = resolveAttachmentUrl(raw.url, host, detailWebUrl, projectId) ?: return@mapNotNull null
            raw to resolved
        }
        .distinctBy { (_, resolved) -> resolved.loadUrl.normalizeHttpUrl() }
        .map { (raw, resolved) ->
            val normalizedUrl = resolved.loadUrl.normalizeHttpUrl()
            CommentAttachment(
                url = normalizedUrl,
                openUrl = resolved.openUrl.normalizeHttpUrl(),
                label = raw.label
                    ?: resolved.openUrl.substringBefore('?').substringBefore('#').substringAfterLast('/').ifBlank { "Attachment" },
                isImage = raw.isImageHint || normalizedUrl.isPreviewableImageUrl()
            )
        }
        .toList()
}

fun resolveAttachmentUrl(
    rawUrl: String,
    host: String,
    detailWebUrl: String?,
    projectId: Long?
): ResolvedCommentAttachment? {
    val cleaned = rawUrl.trim().trim('<', '>')
    if (cleaned.isBlank()) return null
    val normalizedHost = host.trim().removeSuffix("/")
    val projectBaseUrl = detailWebUrl?.toGitLabProjectBaseUrl()
    val webUrl = when {
        cleaned.startsWith("http://") || cleaned.startsWith("https://") -> cleaned
        cleaned.startsWith("/uploads/") && projectBaseUrl != null -> projectBaseUrl + cleaned
        cleaned.startsWith("uploads/") && projectBaseUrl != null -> "$projectBaseUrl/$cleaned"
        cleaned.startsWith("/") -> normalizedHost + cleaned
        cleaned.startsWith("uploads/") -> "$normalizedHost/$cleaned"
        else -> null
    } ?: return null

    val uploadPath = webUrl.gitLabUploadPath()
    val loadUrl = if (projectId != null && uploadPath != null) {
        "$normalizedHost/api/v4/projects/$projectId/uploads/$uploadPath"
    } else {
        webUrl
    }
    return ResolvedCommentAttachment(loadUrl = loadUrl, openUrl = webUrl)
}

fun String.normalizeHttpUrl(): String {
    val uri = runCatching { Uri.parse(this) }.getOrNull() ?: return this
    val scheme = uri.scheme ?: return this
    val authority = uri.encodedAuthority ?: return this
    if (scheme != "http" && scheme != "https") return this
    val encodedPath = Uri.encode(uri.path.orEmpty(), "/")
    val query = uri.encodedQuery?.let { "?$it" }.orEmpty()
    val fragment = uri.encodedFragment?.let { "#$it" }.orEmpty()
    return "$scheme://$authority$encodedPath$query$fragment"
}

fun extractIssueReferences(body: String, detail: WorkDetailData): List<IssueReference> {
    val projectId = (detail.target as? DetailTarget.Issue)?.projectId
        ?: (detail.target as? DetailTarget.MergeRequest)?.projectId
        ?: return emptyList()

    val linkedIssues = issueUrlPattern.findAll(body).mapNotNull { match ->
        val projectPath = match.groups[1]?.value ?: return@mapNotNull null
        val issueIid = match.groups[2]?.value?.toLongOrNull() ?: return@mapNotNull null
        IssueReference(
            projectPath = projectPath,
            projectId = projectId,
            issueIid = issueIid,
            label = "Open $projectPath#$issueIid"
        )
    }
    val crossProjectIssues = crossProjectIssuePattern.findAll(body).mapNotNull { match ->
        val projectPath = match.groups[1]?.value ?: return@mapNotNull null
        val issueIid = match.groups[2]?.value?.toLongOrNull() ?: return@mapNotNull null
        IssueReference(
            projectPath = projectPath,
            projectId = projectId,
            issueIid = issueIid,
            label = "Open $projectPath#$issueIid"
        )
    }
    val shorthandIssues = sameProjectIssuePattern.findAll(body).mapNotNull { match ->
        val issueIid = match.groups[1]?.value?.toLongOrNull() ?: return@mapNotNull null
        IssueReference(
            projectPath = null,
            projectId = projectId,
            issueIid = issueIid,
            label = "Open #$issueIid"
        )
    }

    return (linkedIssues + crossProjectIssues + shorthandIssues)
        .distinctBy { "${it.projectPath.orEmpty()}#${it.issueIid}" }
        .toList()
}

fun String.toGitLabProjectBaseUrl(): String =
    substringBefore("/-/issues/")
        .substringBefore("/-/merge_requests/")
        .trimEnd('/')

fun String.gitLabUploadPath(): String? {
    val path = runCatching { Uri.parse(this).encodedPath }.getOrNull() ?: return null
    val marker = "/uploads/"
    val markerIndex = path.indexOf(marker)
    if (markerIndex < 0) return null
    val uploadPath = path.substring(markerIndex + marker.length)
    if (uploadPath.count { it == '/' } < 1) return null
    return uploadPath
}

fun DetailTarget.projectId(): Long =
    when (this) {
        is DetailTarget.Issue -> projectId
        is DetailTarget.MergeRequest -> projectId
    }

fun String.isPreviewableImageUrl(): Boolean {
    val path = substringBefore('?').substringBefore('#').lowercase()
    return listOf(".png", ".jpg", ".jpeg", ".gif", ".webp", ".svg").any { path.endsWith(it) }
}
