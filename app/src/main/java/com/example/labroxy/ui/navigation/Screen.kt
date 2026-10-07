package com.example.labroxy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MergeType
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen {
    Loading,
    SignIn,
    Dashboard,
    Project,
    Group,
    Detail,
    User,
    About
}

enum class WorkSection(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Outlined.Home),
    Projects("Projects", Icons.Outlined.Folder),
    Groups("Groups", Icons.Outlined.AccountTree),
    Assigned("Assigned", Icons.AutoMirrored.Outlined.Assignment),
    MergeRequests("Merge requests", Icons.AutoMirrored.Outlined.MergeType),
    Todos("To-Do List", Icons.Outlined.TaskAlt),
    Notifications("Notifications", Icons.Outlined.History),
    Settings("Settings", Icons.Outlined.Settings)
}
