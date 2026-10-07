package com.example.labroxy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.TaskAlt
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.labroxy.data.GitLabGroup
import com.example.labroxy.data.GitLabIssue
import com.example.labroxy.data.GitLabProject
import com.example.labroxy.ui.GroupData
import com.example.labroxy.ui.LoadState
import com.example.labroxy.ui.components.ErrorBlock
import com.example.labroxy.ui.components.ListCard
import com.example.labroxy.ui.components.LoadingBlock
import com.example.labroxy.ui.components.groupItems
import com.example.labroxy.ui.components.issueItems
import com.example.labroxy.ui.components.projectItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    state: LoadState<GroupData>,
    onBack: () -> Unit,
    onProjectClick: (GitLabProject) -> Unit,
    onGroupClick: (GitLabGroup) -> Unit,
    onIssueClick: (GitLabIssue) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Projects", "Subgroups", "Issues")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group", fontWeight = FontWeight.SemiBold) },
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
                                    0 -> Icons.Outlined.Folder
                                    1 -> Icons.Outlined.AccountTree
                                    else -> Icons.Outlined.TaskAlt
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                LoadState.Loading -> item { LoadingBlock("Loading group projects") }
                is LoadState.Error -> item { ErrorBlock(state.message) }
                is LoadState.Success -> {
                    item {
                        ListCard(
                            icon = Icons.Outlined.AccountTree,
                            title = state.value.group.name,
                            meta = state.value.group.fullPath
                        )
                    }
                    when (tab) {
                        0 -> projectItems(state.value.projects, onProjectClick)
                        1 -> groupItems(state.value.subgroups, onGroupClick)
                        2 -> issueItems(state.value.issues, "This group has no open issues.", onIssueClick)
                    }
                }
            }
        }
    }
}
