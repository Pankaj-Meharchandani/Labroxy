/**
 * File: UserScreen.kt
 *
 * What it does:
 * Renders user profile view showing profile card and tabs for assigned issues and merge requests.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.LabroxyApp: Displayed when `Screen.User` is active.
 * - com.example.labroxy.ui.UserData: Displays user profile info, assigned issues, and merge requests.
 * - com.example.labroxy.ui.components.WorkItemCards: Renders `UserHeroCard`.
 *
 * Features / Functions:
 * - Top app bar with back navigation button.
 * - User hero header card (`UserHeroCard`).
 * - Bottom navigation bar tabs: Assigned Issues, Merge Requests.
 */
package com.example.labroxy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MergeType
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
import com.example.labroxy.ui.UserData
import com.example.labroxy.ui.components.ErrorBlock
import com.example.labroxy.ui.components.UserHeroCard
import com.example.labroxy.ui.components.issueItems
import com.example.labroxy.ui.components.mrItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    state: LoadState<UserData>,
    onBack: () -> Unit,
    onIssueClick: (GitLabIssue) -> Unit,
    onMergeRequestClick: (GitLabMergeRequest) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Assigned Issues", "Merge Requests")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile", fontWeight = FontWeight.SemiBold) },
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
                                    else -> Icons.AutoMirrored.Outlined.MergeType
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
            is LoadState.Success -> {
                val data = state.value
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        UserHeroCard(data.user)
                    }
                    when (tab) {
                        0 -> issueItems(data.issues, "No open issues assigned to this user.", onIssueClick)
                        1 -> mrItems(data.mergeRequests, onMergeRequestClick)
                    }
                }
            }
        }
    }
}
