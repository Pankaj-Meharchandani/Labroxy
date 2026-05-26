@file:OptIn(ExperimentalLayoutApi::class)

package com.example.jetlab.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetlab.data.GitLabCommit
import com.example.jetlab.data.GitLabIssue
import com.example.jetlab.data.GitLabMergeRequest
import com.example.jetlab.data.GitLabProject

private enum class Screen { SignIn, Dashboard, Project }

@Composable
fun JetLabApp(viewModel: JetLabViewModel = viewModel()) {
    val session by viewModel.session.collectAsState()
    val dashboard by viewModel.dashboard.collectAsState()
    val project by viewModel.project.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    var screen by remember { mutableStateOf(if (session.isReady) Screen.Dashboard else Screen.SignIn) }

    LaunchedEffect(session.isReady) {
        screen = if (session.isReady) Screen.Dashboard else Screen.SignIn
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        AnimatedContent(targetState = screen, label = "screen") { target ->
            when (target) {
                Screen.SignIn -> SignInScreen(
                    defaultHost = session.host,
                    onConnect = viewModel::saveSession
                )
                Screen.Dashboard -> DashboardScreen(
                    state = dashboard,
                    query = query,
                    onQueryChange = viewModel::setSearchQuery,
                    onProjectClick = {
                        viewModel.loadProject(it.id)
                        screen = Screen.Project
                    },
                    onSignOut = viewModel::signOut
                )
                Screen.Project -> ProjectScreen(
                    state = project,
                    onBack = { screen = Screen.Dashboard }
                )
            }
        }
    }
}

@Composable
private fun SignInScreen(defaultHost: String, onConnect: (String, String) -> Unit) {
    var host by remember { mutableStateOf(defaultHost) }
    var token by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFEFE5), Color(0xFFFAF9F6), Color(0xFFE9F1F1))
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.AccountTree,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(18.dp))
        Text("JetLab", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Text(
            "A calm, fast GitLab cockpit for projects, reviews, issues, and recent code activity.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))
        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("GitLab host") },
                    leadingIcon = { Icon(Icons.Outlined.Code, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Personal access token") },
                    leadingIcon = { Icon(Icons.Outlined.Key, null) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onConnect(host, token) },
                    enabled = host.isNotBlank() && token.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Connect")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreen(
    state: LoadState<DashboardData>,
    query: String,
    onQueryChange: (String) -> Unit,
    onProjectClick: (GitLabProject) -> Unit,
    onSignOut: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projects", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Sign out")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    label = { Text("Search your GitLab projects") },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            when (state) {
                LoadState.Loading -> item { LoadingBlock("Loading GitLab workspace") }
                is LoadState.Error -> item { ErrorBlock(state.message) }
                is LoadState.Success -> {
                    item { WelcomeBlock(state.value) }
                    items(state.value.projects, key = { it.id }) { project ->
                        ProjectCard(project, onClick = { onProjectClick(project) })
                    }
                    if (state.value.projects.isEmpty()) {
                        item { EmptyBlock("No projects matched that search.") }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectScreen(state: LoadState<ProjectData>, onBack: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Issues", "MRs", "Commits")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Outlined.TaskAlt
                                    1 -> Icons.AutoMirrored.Outlined.MergeType
                                    else -> Icons.Outlined.History
                                },
                                contentDescription = label
                            )
                        },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        when (state) {
            LoadState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is LoadState.Error -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                ErrorBlock(state.message)
            }
            is LoadState.Success -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { ProjectHero(state.value.project, state.value.branches.size) }
                when (tab) {
                    0 -> items(state.value.issues, key = { it.id }) { IssueRow(it) }
                    1 -> items(state.value.mergeRequests, key = { it.id }) { MergeRequestRow(it) }
                    2 -> items(state.value.commits, key = { it.id }) { CommitRow(it) }
                }
            }
        }
    }
}

@Composable
private fun WelcomeBlock(data: DashboardData) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Welcome, ${data.user.name}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "${data.projects.size} active projects sorted by recent activity",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProjectCard(project: GitLabProject, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProjectAvatar(project.name)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(project.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(project.pathWithNamespace, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            project.description?.takeIf { it.isNotBlank() }?.let {
                Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricChip(Icons.Outlined.Star, "${project.starCount}")
                MetricChip(Icons.Outlined.AccountTree, "${project.forksCount}")
                MetricChip(Icons.Outlined.TaskAlt, "${project.openIssuesCount} open")
                project.defaultBranch?.let { MetricChip(Icons.Outlined.Tag, it) }
            }
        }
    }
}

@Composable
private fun ProjectHero(project: GitLabProject, branchCount: Int) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F1))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(project.pathWithNamespace, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Text(project.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            project.description?.takeIf { it.isNotBlank() }?.let { Text(it) }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricChip(Icons.Outlined.TaskAlt, "${project.openIssuesCount} issues")
                MetricChip(Icons.AutoMirrored.Outlined.MergeType, "$branchCount branches")
                MetricChip(Icons.Outlined.Star, "${project.starCount} stars")
            }
        }
    }
}

@Composable
private fun IssueRow(issue: GitLabIssue) {
    ListCard(icon = Icons.Outlined.TaskAlt, title = "#${issue.iid} ${issue.title}", meta = "${issue.state} by ${issue.author?.username ?: "unknown"}") {
        LabelRow(issue.labels)
    }
}

@Composable
private fun MergeRequestRow(mr: GitLabMergeRequest) {
    ListCard(
        icon = Icons.AutoMirrored.Outlined.MergeType,
        title = "!${mr.iid} ${mr.title}",
        meta = "${mr.sourceBranch} into ${mr.targetBranch}"
    ) {
        Text(mr.mergeStatus ?: mr.state, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CommitRow(commit: GitLabCommit) {
    ListCard(
        icon = Icons.Outlined.History,
        title = commit.title,
        meta = "${commit.shortId} by ${commit.authorName}"
    )
}

@Composable
private fun ListCard(icon: ImageVector, title: String, meta: String, content: @Composable () -> Unit = {}) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(meta, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                content()
            }
        }
    }
}

@Composable
private fun LabelRow(labels: List<String>) {
    if (labels.isEmpty()) return
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        labels.take(4).forEach { label ->
            AssistChip(onClick = {}, label = { Text(label) })
        }
    }
}

@Composable
private fun ProjectAvatar(name: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MetricChip(icon: ImageVector, text: String) {
    AssistChip(onClick = {}, leadingIcon = { Icon(icon, null, Modifier.size(16.dp)) }, label = { Text(text) })
}

@Composable
private fun LoadingBlock(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorBlock(message: String) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECEB))) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.ErrorOutline, null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(10.dp))
            Text(message)
        }
    }
}

@Composable
private fun EmptyBlock(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Outlined.Folder, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
