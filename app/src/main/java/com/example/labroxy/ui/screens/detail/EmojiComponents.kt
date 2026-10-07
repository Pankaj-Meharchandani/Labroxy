package com.example.labroxy.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labroxy.data.GitLabAwardEmoji

val emojiMap = mapOf(
    "thumbsup" to "👍",
    "thumbsdown" to "👎",
    "saluting_face" to "🫡",
    "smile" to "😄",
    "tada" to "🎉",
    "confused" to "😕",
    "heart" to "❤️",
    "rocket" to "🚀",
    "eyes" to "👀",
    "laughing" to "😆",
    "sweat_smile" to "😅",
    "thinking" to "🤔",
    "cry" to "😢",
    "facepalm" to "🤦",
    "ok_hand" to "👌",
    "fire" to "🔥",
    "clap" to "👏",
    "joy" to "😂",
    "heart_eyes" to "😍",
    "pill" to "💊",
    "hammer" to "🔨",
    "white_check_mark" to "✅",
    "x" to "❌",
    "sunny" to "☀️",
    "moon" to "🌙",
    "star" to "⭐",
    "party_popper" to "🎉",
    "pray" to "🙏",
    "grin" to "😁",
    "rolling_on_the_floor_laughing" to "🤣",
    "blush" to "😊",
    "innocent" to "😇",
    "star_struck" to "🤩",
    "kissing_heart" to "😘",
    "zany_face" to "🤪",
    "shushing_face" to "🤫",
    "money_mouth_face" to "🤑",
    "hugging_face" to "🤗",
    "sleeping" to "😴",
    "sunglasses" to "😎",
    "neutral_face" to "😐",
    "expressionless" to "😑",
    "grimacing" to "😬",
    "pensive" to "😔",
    "sob" to "😭",
    "angry" to "😠",
    "mask" to "😷"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmojiRow(
    awardEmoji: List<GitLabAwardEmoji>,
    onToggleReaction: (String) -> Unit,
    onShowReactionPicker: () -> Unit,
    currentUserId: Long,
    pinnedEmojis: List<String> = emptyList()
) {
    val grouped = awardEmoji.groupBy { it.name }
    val allEmojiNames = (pinnedEmojis + grouped.keys).distinct()
    
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        allEmojiNames.forEach { name ->
            val awards = grouped[name] ?: emptyList()
            val hasMyAward = awards.any { it.user.id == currentUserId }
            Surface(
                onClick = { onToggleReaction(name) },
                shape = RoundedCornerShape(8.dp),
                color = if (hasMyAward) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    if (hasMyAward) 2.dp else 1.dp,
                    if (hasMyAward) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = emojiMap[name] ?: name, fontSize = 16.sp)
                    Text(
                        text = "${awards.size}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (hasMyAward) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        Surface(
            onClick = onShowReactionPicker,
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Icon(
                imageVector = Icons.Outlined.SentimentSatisfied,
                contentDescription = "Add reaction",
                modifier = Modifier
                    .padding(6.dp)
                    .size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPickerSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onEmojiSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredEmojis = remember(searchQuery) {
        if (searchQuery.isBlank()) emojiMap.toList()
        else emojiMap.filter { it.key.contains(searchQuery, ignoreCase = true) }.toList()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Add reaction",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close")
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )
            
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
            ) {
                items(filteredEmojis) { (name, emoji) ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { onEmojiSelected(name) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}
