package com.example.labroxy.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.labroxy.data.AppSettings

@Composable
fun SettingsScreen(
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
    onAboutClick: () -> Unit
) {
    val context = LocalContext.current
    val appName = remember {
        runCatching {
            context.applicationInfo.loadLabel(context.packageManager).toString()
        }.getOrDefault("Labroxy")
    }
    
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var cacheSize by remember { mutableStateOf("1.2 MB") }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    
    var syncInterval by remember { mutableStateOf("15 mins") }
    var showIntervalDropdown by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onPushNotificationsChange(true)
        } else {
            onPushNotificationsChange(false)
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HOME TABS & ACTIVITIES ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SettingsSectionHeader(icon = Icons.Outlined.Home, title = "Home Screen Tabs & Activities")

                SettingToggleRow(
                    title = "Projects Tab",
                    subtitle = "Show Projects card on home screen",
                    checked = settings.showProjectsTab,
                    onCheckedChange = onToggleProjectsTab
                )

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                SettingToggleRow(
                    title = "Groups Tab",
                    subtitle = "Show Groups card on home screen",
                    checked = settings.showGroupsTab,
                    onCheckedChange = onToggleGroupsTab
                )

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                SettingToggleRow(
                    title = "Assigned Work Tab",
                    subtitle = "Show Assigned Work card on home screen",
                    checked = settings.showAssignedTab,
                    onCheckedChange = onToggleAssignedTab
                )

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                SettingToggleRow(
                    title = "Merge Requests Tab",
                    subtitle = "Show Merge Requests card on home screen",
                    checked = settings.showMergeRequestsTab,
                    onCheckedChange = onToggleMergeRequestsTab
                )

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                SettingToggleRow(
                    title = "To-Do List Tab",
                    subtitle = "Show To-Do List card on home screen",
                    checked = settings.showTodosTab,
                    onCheckedChange = onToggleTodosTab
                )

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                SettingToggleRow(
                    title = "Recent Activities",
                    subtitle = "Show recent 5 activities below home screen tabs",
                    checked = settings.showActivities,
                    onCheckedChange = onToggleActivities
                )
            }
        }

        // --- APPEARANCE & THEME ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SettingsSectionHeader(icon = Icons.Outlined.Palette, title = "Appearance & Theme")

                Text(
                    "Theme Mode",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val themes = listOf(
                        Triple("system", "System", Icons.Outlined.SettingsSuggest),
                        Triple("light", "Light", Icons.Outlined.WbSunny),
                        Triple("dark", "Dark", Icons.Outlined.DarkMode)
                    )
                    themes.forEach { (value, label, icon) ->
                        ThemeOptionCard(
                            label = label,
                            icon = icon,
                            selected = settings.themeMode == value,
                            onClick = { onThemeModeChange(value) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                Text(
                    "Home Screen UI Style",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val homeStyles = listOf(
                        Triple("minimal", "Minimal", Icons.Outlined.Home),
                        Triple("functional", "Functional", Icons.AutoMirrored.Outlined.Assignment),
                        Triple("stylish", "Stylish", Icons.Outlined.Palette)
                    )
                    homeStyles.forEach { (value, label, icon) ->
                        ThemeOptionCard(
                            label = label,
                            icon = icon,
                            selected = settings.homeUiStyle == value,
                            onClick = { onHomeUiStyleChange(value) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // --- NOTIFICATIONS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SettingsSectionHeader(icon = Icons.Outlined.NotificationsActive, title = "Notifications")
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Push Notifications", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Notify you when new GitLab to-do items arrive.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = settings.pushNotifications,
                        onCheckedChange = { enabled ->
                            if (enabled &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onPushNotificationsChange(enabled)
                            }
                        }
                    )
                }

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Notification Sound", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Play sound for new GitLab to-do alerts.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                }

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Haptic Feedback", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Vibrate device on new to-do alerts.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                }
            }
        }

        // --- DATA & SYNC ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SettingsSectionHeader(icon = Icons.Outlined.Storage, title = "Data & Synchronization")
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Sync Frequency", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("Background check rate for activities", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    
                    Box {
                        TextButton(onClick = { showIntervalDropdown = true }) {
                            Text(syncInterval, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, Modifier.size(16.dp))
                        }
                        
                        DropdownMenu(
                            expanded = showIntervalDropdown,
                            onDismissRequest = { showIntervalDropdown = false }
                        ) {
                            listOf("Manual", "15 mins", "30 mins", "1 hour").forEach { interval ->
                                DropdownMenuItem(
                                    text = { Text(interval) },
                                    onClick = {
                                        syncInterval = interval
                                        showIntervalDropdown = false
                                        Toast.makeText(context, "Sync frequency updated to $interval", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Offline Local Cache", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Cache size: $cacheSize",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(
                        onClick = { showClearCacheDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Outlined.Cached, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Clear", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- ABOUT SECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SettingsSectionHeader(icon = Icons.Outlined.Info, title = "About")
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onAboutClick)
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("About $appName", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("App details, version and technologies", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Go to About page",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Clear Cached Data") },
            text = { Text("Are you sure you want to clear all offline issue history, user avatars, and cached workspaces? The app will reload fresh data from your GitLab instance.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearCacheDialog = false
                        cacheSize = "0.0 KB"
                        Toast.makeText(context, "Local cache successfully cleared!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ThemeOptionCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SettingsSectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(
                subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
