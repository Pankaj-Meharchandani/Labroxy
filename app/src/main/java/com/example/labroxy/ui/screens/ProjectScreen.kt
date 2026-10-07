package com.example.labroxy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.labroxy.data.GitLabIssue
import com.example.labroxy.data.GitLabMergeRequest
import com.example.labroxy.ui.LoadState
import com.example.labroxy.ui.ProjectData
import com.example.labroxy.ui.components.CommitRow
import com.example.labroxy.ui.components.ErrorBlock
import com.example.labroxy.ui.components.ProjectHero
import com.example.labroxy.ui.components.boardItems
import com.example.labroxy.ui.components.issueItems
import com.example.labroxy.ui.components.mrItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    state: LoadState<ProjectData>,
    onBack: () -> Unit,
    onIssueClick: (GitLabIssue) -> Unit,
    onMergeRequestClick: (GitLabMergeRequest) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Issues", "MRs", "Commits", "Boards")

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
                                    2 -> Icons.Outlined.History
                                    else -> Icons.Outlined.AccountTree
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
                    0 -> issueItems(state.value.issues, "This project has no open issues.", onIssueClick)
                    1 -> mrItems(state.value.mergeRequests, onMergeRequestClick)
                    2 -> items(state.value.commits, key = { it.id }) { CommitRow(it) }
                    3 -> boardItems(state.value.boards)
                }
            }
        }
    }
}
