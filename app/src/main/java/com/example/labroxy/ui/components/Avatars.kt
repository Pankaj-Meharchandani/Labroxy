package com.example.labroxy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.labroxy.data.GitLabUser

@Composable
fun UserAvatar(user: GitLabUser, size: Int = 80, onClick: (() -> Unit)? = null) {
    var hasError by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .border(
                width = if (size > 50) 2.dp else 1.dp,
                color = if (size > 50) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (!user.avatarUrl.isNullOrBlank() && !hasError) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = user.name,
                contentScale = ContentScale.Crop,
                onError = { hasError = true },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                user.name.take(1).uppercase(),
                color = Color.White,
                style = if (size > 50) {
                    MaterialTheme.typography.headlineLarge
                } else if (size > 36) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProjectAvatar(name: String) {
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
