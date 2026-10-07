package com.example.labroxy.ui.screens.detail

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.labroxy.data.GitLabDiscussion
import com.example.labroxy.data.GitLabNote
import com.example.labroxy.data.GitLabSession
import com.example.labroxy.data.GitLabUser
import com.example.labroxy.ui.WorkDetailData
import com.example.labroxy.ui.components.ClickableCommentText
import com.example.labroxy.ui.components.CommentFileChip
import com.example.labroxy.ui.components.CommentImagePreview
import com.example.labroxy.ui.components.EmptyBlock
import com.example.labroxy.ui.components.ListCard
import com.example.labroxy.ui.components.UserAvatar
import com.example.labroxy.ui.util.CommentAttachment
import com.example.labroxy.ui.util.NoteSegment
import com.example.labroxy.ui.util.ReplyTarget
import com.example.labroxy.ui.util.compactGitLabDate
import com.example.labroxy.ui.util.extractCommentAttachments
import com.example.labroxy.ui.util.extractIssueReferences
import com.example.labroxy.ui.util.normalizeHttpUrl
import com.example.labroxy.ui.util.projectId
import com.example.labroxy.ui.util.resolveAttachmentUrl
import com.example.labroxy.ui.util.stripHtmlTags

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteBody(
    body: String,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val attachments = remember(body, session.host, detail.webUrl, detail.target) {
        extractCommentAttachments(body, session.host, detail.webUrl, detail.target.projectId())
    }
    val issueReferences = remember(body, detail.target, detail.webUrl) {
        extractIssueReferences(body, detail)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val segments = remember(body) {
            val s = mutableListOf<NoteSegment>()
            val pattern = Regex("""(!\[[^\]]*]\([^)\s]+(?:\s+"[^"]*")?\)(?:\{[^}]*\})?)|(<img\b[^>]*\bsrc=(["'])(.*?)\3[^>]*>)""", RegexOption.IGNORE_CASE)
            var lastIndex = 0
            pattern.findAll(body).forEach { result ->
                if (result.range.first > lastIndex) {
                    val text = body.substring(lastIndex, result.range.first).stripHtmlTags()
                    if (text.isNotBlank()) s.add(NoteSegment.Text(text))
                }
                val rawUrl = result.groups[2]?.value ?: result.groups[4]?.value ?: result.value.substringAfter("(").substringBefore(")")
                val resolved = resolveAttachmentUrl(rawUrl, session.host, detail.webUrl, detail.target.projectId())
                if (resolved != null) {
                    val normalizedUrl = resolved.loadUrl.normalizeHttpUrl()
                    s.add(
                        NoteSegment.Image(
                            CommentAttachment(
                        url = normalizedUrl,
                        openUrl = resolved.openUrl.normalizeHttpUrl(),
                        label = resolved.openUrl.substringBefore('?').substringBefore('#').substringAfterLast('/').ifBlank { "Image" },
                        isImage = true
                    )
                        )
                    )
                }
                lastIndex = result.range.last + 1
            }
            if (lastIndex < body.length) {
                val text = body.substring(lastIndex).stripHtmlTags()
                if (text.isNotBlank()) s.add(NoteSegment.Text(text))
            }
            s
        }

        segments.forEach { segment ->
            when (segment) {
                is NoteSegment.Text -> {
                    ClickableCommentText(
                        text = segment.text,
                        onLinkClick = { url ->
                            if (!onGitLabLinkClick(url)) {
                                uriHandler.openUri(url)
                            }
                        },
                        onUserClick = onUserClick
                    )
                }
                is NoteSegment.Image -> {
                    CommentImagePreview(segment.attachment, session.token)
                }
            }
        }

        val nonImageAttachments = attachments.filterNot { it.isImage }
        if (nonImageAttachments.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                nonImageAttachments.forEach { attachment ->
                    CommentFileChip(attachment)
                }
            }
        }
        if (issueReferences.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                issueReferences.forEach { reference ->
                    AssistChip(
                        onClick = {
                            onIssueReferenceClick(
                                reference.projectPath,
                                reference.projectId,
                                reference.issueIid
                            )
                        },
                        leadingIcon = { Icon(Icons.Outlined.TaskAlt, null, Modifier.size(16.dp)) },
                        label = {
                            Text(
                                reference.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.widthIn(max = 260.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentComposerBar(
    projectId: Long,
    comment: String,
    replyTarget: ReplyTarget?,
    onCommentChange: (String) -> Unit,
    onCancelReply: () -> Unit,
    onSubmit: () -> Unit,
    onUploadFile: (Long, Uri) -> Unit
) {
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onUploadFile(projectId, it) }
    }
    val context = LocalContext.current

    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (replyTarget != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Replying to ${replyTarget.authorName}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    TextButton(onClick = onCancelReply) {
                        Text("Cancel")
                    }
                }
            }
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                label = { Text(if (replyTarget == null) "Add a comment" else "Add a reply") },
                minLines = 1,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { filePicker.launch("*/*") }) {
                    Icon(Icons.Outlined.AttachFile, "Attach file")
                }
                IconButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = clipboard.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                        val uri = clip.getItemAt(0).uri
                        if (uri != null) {
                            onUploadFile(projectId, uri)
                        } else {
                            Toast.makeText(context, "No image in clipboard", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Icon(Icons.Outlined.ContentPaste, "Paste image")
                }
                Button(
                    onClick = onSubmit,
                    enabled = comment.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (replyTarget == null) "Comment" else "Reply")
                }
            }
        }
    }
}

@Composable
fun DiscussionCard(
    discussion: GitLabDiscussion,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit,
    onReply: (GitLabNote) -> Unit,
    onToggleReaction: (GitLabNote?, String) -> Unit,
    onShowReactionPicker: (GitLabNote) -> Unit,
    currentUserId: Long
) {
    val firstNote = discussion.notes.firstOrNull()
    if (firstNote == null) {
        EmptyBlock("Empty discussion")
        return
    }

    if (firstNote.system) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            discussion.notes.forEach { note ->
                SystemNoteRow(
                    note = note,
                    session = session,
                    detail = detail,
                    onIssueReferenceClick = onIssueReferenceClick,
                    onGitLabLinkClick = onGitLabLinkClick,
                    onUserClick = onUserClick
                )
            }
        }
        return
    }

    val replies = discussion.notes.drop(1)

    ListCard(
        icon = Icons.Outlined.History,
        user = firstNote.author,
        title = firstNote.author?.name ?: firstNote.author?.username ?: "GitLab",
        meta = firstNote.createdAt ?: "",
        onUserClick = onUserClick
    ) {
        DiscussionNoteBody(
            note = firstNote,
            session = session,
            detail = detail,
            onIssueReferenceClick = onIssueReferenceClick,
            onGitLabLinkClick = onGitLabLinkClick,
            onUserClick = onUserClick,
            onReply = { onReply(firstNote) },
            onToggleReaction = onToggleReaction,
            onShowReactionPicker = onShowReactionPicker,
            currentUserId = currentUserId
        )
        if (replies.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                replies.forEach { reply ->
                    DiscussionReply(
                        note = reply,
                        session = session,
                        detail = detail,
                        onIssueReferenceClick = onIssueReferenceClick,
                        onGitLabLinkClick = onGitLabLinkClick,
                        onUserClick = onUserClick,
                        onReply = { onReply(reply) },
                        onToggleReaction = onToggleReaction,
                        onShowReactionPicker = onShowReactionPicker,
                        currentUserId = currentUserId
                    )
                }
            }
        }
    }
}

@Composable
fun SystemNoteRow(
    note: GitLabNote,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit
) {
    val author = note.author
    val authorName = author?.name ?: author?.username ?: "GitLab"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier.width(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f))
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(42.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
            )
        }

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.34f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        authorName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = author?.username != null) {
                                author?.username?.let { onUserClick(it) }
                            }
                    )
                    Text(
                        compactGitLabDate(note.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                NoteBody(
                    body = note.body,
                    session = session,
                    detail = detail,
                    onIssueReferenceClick = onIssueReferenceClick,
                    onGitLabLinkClick = onGitLabLinkClick,
                    onUserClick = onUserClick
                )
            }
        }
    }
}

@Composable
fun DiscussionNoteBody(
    note: GitLabNote,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit,
    onReply: () -> Unit,
    onToggleReaction: (GitLabNote?, String) -> Unit,
    onShowReactionPicker: (GitLabNote) -> Unit,
    currentUserId: Long
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NoteBody(
            body = note.body,
            session = session,
            detail = detail,
            onIssueReferenceClick = onIssueReferenceClick,
            onGitLabLinkClick = onGitLabLinkClick,
            onUserClick = onUserClick
        )
        EmojiRow(
            awardEmoji = note.awardEmoji,
            onToggleReaction = { onToggleReaction(note, it) },
            onShowReactionPicker = { onShowReactionPicker(note) },
            currentUserId = currentUserId
        )
        if (!note.system) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                TextButton(
                    onClick = onReply,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFC6D26))
                ) {
                    Text("Reply", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DiscussionReply(
    note: GitLabNote,
    session: GitLabSession,
    detail: WorkDetailData,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onUserClick: (String) -> Unit,
    onReply: () -> Unit,
    onToggleReaction: (GitLabNote?, String) -> Unit,
    onShowReactionPicker: (GitLabNote) -> Unit,
    currentUserId: Long
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        UserAvatar(
            user = note.author ?: GitLabUser(id = 0, username = "gitlab", name = "GitLab"),
            size = 32,
            onClick = { note.author?.username?.let { onUserClick(it) } }
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    note.author?.name ?: note.author?.username ?: "GitLab",
                    modifier = Modifier.weight(1f).clickable { note.author?.username?.let { onUserClick(it) } },
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    note.createdAt ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            NoteBody(
                body = note.body,
                session = session,
                detail = detail,
                onIssueReferenceClick = onIssueReferenceClick,
                onGitLabLinkClick = onGitLabLinkClick,
                onUserClick = onUserClick
            )
            EmojiRow(
                awardEmoji = note.awardEmoji,
                onToggleReaction = { onToggleReaction(note, it) },
                onShowReactionPicker = { onShowReactionPicker(note) },
                currentUserId = currentUserId
            )
            if (!note.system) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    TextButton(
                        onClick = onReply,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFC6D26))
                    ) {
                        Text("Reply", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
