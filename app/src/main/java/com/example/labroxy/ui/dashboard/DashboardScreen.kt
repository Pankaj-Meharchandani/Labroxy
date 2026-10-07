/**
 * File: DashboardScreen.kt
 *
 * What it does:
 * Renders top-level dashboard layout with navigation drawer integration, top app bar, search input, and section switcher.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.LabroxyApp: Hosted inside top-level screen container when `Screen.Dashboard` is active.
 * - com.example.labroxy.ui.navigation.WorkDrawer: Integrates navigation drawer sheet.
 * - com.example.labroxy.ui.components.WorkItemLists: Renders list content for Projects, Groups, Assigned, MRs, To-Dos, and Notifications.
 * - com.example.labroxy.ui.screens.SettingsScreen: Displays settings when `WorkSection.Settings` is selected.
 *
 * Features / Functions:
 * - ModalNavigationDrawer scaffold setup.
 * - Section header title and search text field.
 * - Section routing for Home, Projects, Groups, Assigned, MergeRequests, Todos, Notifications, and Settings.
 */
package com.example.labroxy.ui.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.labroxy.data.AppSettings
import com.example.labroxy.data.GitLabEvent
import com.example.labroxy.data.GitLabGroup
import com.example.labroxy.data.GitLabIssue
import com.example.labroxy.data.GitLabMergeRequest
import com.example.labroxy.data.GitLabProject
import com.example.labroxy.data.GitLabTodo
import com.example.labroxy.ui.DashboardData
import com.example.labroxy.ui.LoadState
import com.example.labroxy.ui.components.ErrorBlock
import com.example.labroxy.ui.components.LoadingBlock
import com.example.labroxy.ui.components.assignedItems
import com.example.labroxy.ui.components.groupItems
import com.example.labroxy.ui.components.mrItems
import com.example.labroxy.ui.components.notificationItems
import com.example.labroxy.ui.components.projectItems
import com.example.labroxy.ui.components.todoItems
import com.example.labroxy.ui.navigation.WorkDrawer
import com.example.labroxy.ui.navigation.WorkSection
import com.example.labroxy.ui.screens.SettingsScreen
import com.example.labroxy.ui.util.filteredGroups
import com.example.labroxy.ui.util.filteredMergeRequests
import com.example.labroxy.ui.util.filteredProjects
import com.example.labroxy.ui.util.filteredTodos
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: LoadState<DashboardData>,
    section: WorkSection,
    query: String,
    onSectionChange: (WorkSection) -> Unit,
    onQueryChange: (String) -> Unit,
    onProjectClick: (GitLabProject) -> Unit,
    onGroupClick: (GitLabGroup) -> Unit,
    onIssueClick: (GitLabIssue) -> Unit,
    onMergeRequestClick: (GitLabMergeRequest) -> Unit,
    onTodoClick: (GitLabTodo) -> Unit,
    onEventClick: (GitLabEvent) -> Unit,
    settings: AppSettings,
    onThemeModeChange: (String) -> Unit,
    onHomeUiStyleChange: (String) -> Unit,
    onPushNotificationsChange: (Boolean) -> Unit,
    onToggleProjectsTab: (Boolean) -> Unit,
    onToggleGroupsTab: (Boolean) -> Unit,
    onToggleAssignedTab: (Boolean) -> Unit,
    onToggleMergeRequestsTab: (Boolean) -> Unit,
    onToggleTodosTab: (Boolean) -> Unit,
    onToggleNotificationsTab: (Boolean) -> Unit,
    onToggleActivities: (Boolean) -> Unit,
    onAboutClick: () -> Unit,
    onSignOut: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var assignedTab by remember { mutableIntStateOf(0) }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            WorkDrawer(
                selected = section,
                state = state,
                settings = settings,
                onSectionChange = {
                    onSectionChange(it)
                    scope.launch { drawerState.close() }
                },
                onSignOut = onSignOut
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(section.label, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Outlined.Menu, contentDescription = "Open navigation")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onSectionChange(WorkSection.Notifications) }) {
                            Icon(Icons.Outlined.NotificationsNone, contentDescription = "Notifications")
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
                if (section !in listOf(WorkSection.Home, WorkSection.Settings)) {
                    item {
                        OutlinedTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            label = { Text("Search ${section.label.lowercase()}") },
                            leadingIcon = { Icon(Icons.Outlined.Search, null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                when (state) {
                    LoadState.Loading -> item { LoadingBlock("Loading GitLab workspace") }
                    is LoadState.Error -> item { ErrorBlock(state.message) }
                    is LoadState.Success -> {
                        val data = state.value
                        when (section) {
                            WorkSection.Home -> homeItems(
                                data = data,
                                settings = settings,
                                onSectionChange = onSectionChange,
                                onEventClick = onEventClick,
                                onIssueClick = onIssueClick,
                                onTodoClick = onTodoClick
                            )
                            WorkSection.Projects -> projectItems(data.projects.filteredProjects(query), onProjectClick)
                            WorkSection.Groups -> groupItems(data.groups.filteredGroups(query), onGroupClick)
                            WorkSection.Assigned -> assignedItems(
                                data = data,
                                query = query,
                                selectedTab = assignedTab,
                                onTabChange = { assignedTab = it },
                                onClick = onIssueClick
                            )
                            WorkSection.MergeRequests -> mrItems(data.assignedMergeRequests.filteredMergeRequests(query), onMergeRequestClick)
                            WorkSection.Todos -> todoItems(data.todos.filteredTodos(query), onTodoClick)
                            WorkSection.Notifications -> notificationItems(
                                todos = data.todos.filteredTodos(query),
                                onTodoClick = onTodoClick
                            )
                            WorkSection.Settings -> item {
                                SettingsScreen(
                                    settings = settings,
                                    onThemeModeChange = onThemeModeChange,
                                    onHomeUiStyleChange = onHomeUiStyleChange,
                                    onPushNotificationsChange = onPushNotificationsChange,
                                    onToggleProjectsTab = onToggleProjectsTab,
                                    onToggleGroupsTab = onToggleGroupsTab,
                                    onToggleAssignedTab = onToggleAssignedTab,
                                    onToggleMergeRequestsTab = onToggleMergeRequestsTab,
                                    onToggleTodosTab = onToggleTodosTab,
                                    onToggleNotificationsTab = onToggleNotificationsTab,
                                    onToggleActivities = onToggleActivities,
                                    onAboutClick = onAboutClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
