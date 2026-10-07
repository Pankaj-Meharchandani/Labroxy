package com.example.labroxy.ui.screens.detail

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.labroxy.data.GitLabNote
import com.example.labroxy.data.GitLabSession
import com.example.labroxy.ui.DetailTarget
import com.example.labroxy.ui.LoadState
import com.example.labroxy.ui.WorkDetailData
import com.example.labroxy.ui.components.EmptyBlock
import com.example.labroxy.ui.components.ErrorBlock
import com.example.labroxy.ui.components.GitLabLabelChip
import com.example.labroxy.ui.components.LoadingBlock
import com.example.labroxy.ui.components.MetricChip
import com.example.labroxy.ui.util.ReplyTarget
import com.example.labroxy.ui.util.projectId
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorkDetailScreen(
    state: LoadState<WorkDetailData>,
    session: GitLabSession,
    onBack: () -> Unit,
    onIssueReferenceClick: (String?, Long, Long) -> Unit,
    onGitLabLinkClick: (String) -> Boolean,
    onComment: (String, String?) -> Unit,
    onUpdateIssue: (Boolean, String) -> Unit,
    onUserClick: (String) -> Unit,
    uploadMarkdown: String? = null,
    onUploadFile: (Long, Uri) -> Unit = { _, _ -> },
    onClearUpload: () -> Unit = {},
    onToggleReaction: (GitLabNote?, String) -> Unit = { _, _ -> },
    currentUserId: Long = 0
) {
    var comment by remember { mutableStateOf("") }
    var labels by remember { mutableStateOf("") }
    var replyTarget by remember { mutableStateOf<ReplyTarget?>(null) }
    val listState = rememberLazyListState()
    val data = (state as? LoadState.Success)?.value
    var activeNoteForReaction by remember { mutableStateOf<GitLabNote?>(null) }
    var detailReactionPickerActive by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(uploadMarkdown) {
        if (uploadMarkdown != null) {
            comment = if (comment.isBlank()) uploadMarkdown else "$comment\n$uploadMarkdown"
            onClearUpload()
        }
    }

    LaunchedEffect(data?.target, data?.discussions?.sumOf { it.notes.size }) {
        val targetData = data ?: return@LaunchedEffect
        val conversationItems = if (targetData.discussions.isEmpty()) 1 else targetData.discussions.size
        val itemCount = 1 + (if (targetData.target is DetailTarget.Issue) 1 else 0) + conversationItems
        if (itemCount > 0) {
            listState.scrollToItem(itemCount - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conversation", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (data != null) {
                CommentComposerBar(
                    projectId = data.target.projectId(),
                    comment = comment,
                    replyTarget = replyTarget,
                    onCommentChange = { comment = it },
                    onCancelReply = { replyTarget = null },
                    onSubmit = {
                        onComment(comment, replyTarget?.discussionId)
                        comment = ""
                        replyTarget = null
                    },
                    onUploadFile = onUploadFile
                )
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                LoadState.Loading -> item { LoadingBlock("Loading conversation") }
                is LoadState.Error -> item { ErrorBlock(state.message) }
                is LoadState.Success -> {
                    val loaded = state.value
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    loaded.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(loaded.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                
                                if (!loaded.description.isNullOrBlank()) {
                                    NoteBody(
                                        body = loaded.description,
                                        session = session,
                                        detail = loaded,
                                        onIssueReferenceClick = onIssueReferenceClick,
                                        onGitLabLinkClick = onGitLabLinkClick,
                                        onUserClick = onUserClick
                                    )
                                }
                                
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MetricChip(Icons.Outlined.Tag, loaded.state)
                                    loaded.labelDetails.forEach { GitLabLabelChip(it) }
                                }

                                if (loaded.assignees.isNotEmpty()) {
                                    DetailPeopleSection(
                                        title = "Assignees",
                                        users = loaded.assignees,
                                        onUserClick = onUserClick
                                    )
                                }

                                if (loaded.target is DetailTarget.Issue) {
                                    if (loaded.parentItem != null) {
                                        DetailRelationSection(
                                            title = "Parent",
                                            items = listOf(loaded.parentItem),
                                            onGitLabLinkClick = onGitLabLinkClick
                                        )
                                    } else {
                                        if (loaded.childItems.isNotEmpty()) {
                                            DetailRelationSection(
                                                title = "Child items",
                                                items = loaded.childItems,
                                                onGitLabLinkClick = onGitLabLinkClick
                                            )
                                        }
                                        if (loaded.linkedItems.isNotEmpty()) {
                                            DetailRelationSection(
                                                title = "Linked items",
                                                items = loaded.linkedItems,
                                                onGitLabLinkClick = onGitLabLinkClick
                                            )
                                        }
                                    }
                                }
                                
                                EmojiRow(
                                    awardEmoji = loaded.awardEmoji,
                                    onToggleReaction = { onToggleReaction(null, it) },
                                    onShowReactionPicker = { detailReactionPickerActive = true },
                                    currentUserId = currentUserId
                                )
                            }
                        }
                    }
                    if (loaded.target is DetailTarget.Issue) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = labels.ifBlank { loaded.labels.joinToString(",") },
                                    onValueChange = { labels = it },
                                    label = { Text("Labels, comma separated") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { onUpdateIssue(false, labels.ifBlank { loaded.labels.joinToString(",") }) }) {
                                        Text("Reopen")
                                    }
                                    Button(onClick = { onUpdateIssue(true, labels.ifBlank { loaded.labels.joinToString(",") }) }) {
                                        Text("Close / Save")
                                    }
                                }
                            }
                        }
                    }
                    if (loaded.discussions.isEmpty()) {
                        item { EmptyBlock("No conversation history yet.") }
                    } else {
                        items(loaded.discussions, key = { it.id }) { discussion ->
                            DiscussionCard(
                                discussion = discussion,
                                session = session,
                                detail = loaded,
                                onIssueReferenceClick = onIssueReferenceClick,
                                onGitLabLinkClick = onGitLabLinkClick,
                                onUserClick = onUserClick,
                                onReply = { note ->
                                    replyTarget = ReplyTarget(
                                        discussionId = discussion.id,
                                        authorName = note.author?.name ?: note.author?.username ?: "comment"
                                    )
                                },
                                onToggleReaction = onToggleReaction,
                                onShowReactionPicker = { activeNoteForReaction = it },
                                currentUserId = currentUserId
                            )
                        }
                    }
                }
            }
        }
    }

    if (activeNoteForReaction != null || detailReactionPickerActive) {
        EmojiPickerSheet(
            sheetState = sheetState,
            onDismiss = { 
                activeNoteForReaction = null
                detailReactionPickerActive = false
            },
            onEmojiSelected = { emojiName ->
                if (detailReactionPickerActive) {
                    onToggleReaction(null, emojiName)
                } else {
                    activeNoteForReaction?.let { onToggleReaction(it, emojiName) }
                }
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        activeNoteForReaction = null
                        detailReactionPickerActive = false
                    }
                }
            }
        )
    }
}
