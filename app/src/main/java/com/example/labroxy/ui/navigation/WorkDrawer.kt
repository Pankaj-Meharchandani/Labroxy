package com.example.labroxy.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.labroxy.data.AppSettings
import com.example.labroxy.ui.DashboardData
import com.example.labroxy.ui.LoadState

@Composable
fun WorkDrawer(
    selected: WorkSection,
    state: LoadState<DashboardData>,
    settings: AppSettings,
    onSectionChange: (WorkSection) -> Unit,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(292.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                "Your work",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
            WorkSection.entries
                .filterNot { it == WorkSection.Notifications }
                .forEach { section ->
                    NavigationDrawerItem(
                        selected = selected == section,
                        onClick = { onSectionChange(section) },
                        icon = { Icon(section.icon, contentDescription = null) },
                        label = { Text(section.label, fontWeight = FontWeight.SemiBold) },
                        badge = { DrawerBadge(section, state) },
                        modifier = Modifier.height(46.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            unselectedContainerColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                            selectedBadgeColor = MaterialTheme.colorScheme.primary,
                            unselectedBadgeColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            Spacer(Modifier.weight(1f))
            NavigationDrawerItem(
                selected = false,
                onClick = onSignOut,
                icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
                label = { Text("Sign out", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.height(46.dp),
                shape = RoundedCornerShape(8.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun DrawerBadge(section: WorkSection, state: LoadState<DashboardData>) {
    val data = (state as? LoadState.Success)?.value ?: return
    val count = when (section) {
        WorkSection.Home -> null
        WorkSection.Projects -> data.projects.size
        WorkSection.Groups -> data.groups.size
        WorkSection.Assigned -> data.assignedWorkItems.size
        WorkSection.MergeRequests -> data.assignedMergeRequests.size
        WorkSection.Todos -> data.todos.size
        WorkSection.Notifications -> data.events.size
        WorkSection.Settings -> null
    } ?: return
    if (count > 0) {
        Text("$count", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HomeNavRow(section: WorkSection, count: Int, onSectionChange: (WorkSection) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSectionChange(section) },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(section.icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(14.dp))
            Text(section.label, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            Text("$count", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
        }
    }
}
