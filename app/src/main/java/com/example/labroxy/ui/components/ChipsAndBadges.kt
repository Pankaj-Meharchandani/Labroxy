package com.example.labroxy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.labroxy.data.GitLabLabel
import com.example.labroxy.ui.util.CommentAttachment
import com.example.labroxy.ui.util.gitLabColor
import com.example.labroxy.ui.util.readableOn

@Composable
fun MetricChip(icon: ImageVector, text: String) {
    AssistChip(
        onClick = {},
        leadingIcon = { Icon(icon, null, Modifier.size(16.dp)) },
        label = { Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = Modifier.widthIn(min = 0.dp, max = 180.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LabelRow(labels: List<String>) {
    if (labels.isEmpty()) return
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        labels.take(4).forEach { label ->
            AssistChip(onClick = {}, label = { Text(label) })
        }
    }
}

@Composable
fun GitLabLabelChip(label: GitLabLabel) {
    val background = gitLabColor(label.color, MaterialTheme.colorScheme.secondaryContainer)
    val content = gitLabColor(label.textColor, readableOn(background))
    AssistChip(
        onClick = {},
        leadingIcon = {
            Icon(
                Icons.Outlined.Tag,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = content
            )
        },
        label = {
            Text(
                label.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = content
            )
        },
        border = BorderStroke(1.dp, content.copy(alpha = 0.35f)),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = background,
            labelColor = content,
            leadingIconContentColor = content
        ),
        modifier = Modifier.widthIn(min = 0.dp, max = 220.dp)
    )
}

@Composable
fun CommentFileChip(attachment: CommentAttachment) {
    val uriHandler = LocalUriHandler.current

    AssistChip(
        onClick = { uriHandler.openUri(attachment.openUrl) },
        leadingIcon = { Icon(Icons.Outlined.InsertDriveFile, null, Modifier.size(16.dp)) },
        label = { Text(attachment.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        modifier = Modifier.widthIn(max = 220.dp)
    )
}

@Composable
fun CommentImagePreview(attachment: CommentAttachment, token: String) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val request = remember(attachment.url, token) {
        ImageRequest.Builder(context)
            .data(attachment.url)
            .addHeader("PRIVATE-TOKEN", token)
            .crossfade(true)
            .decoderFactory(SvgDecoder.Factory())
            .build()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(attachment.openUrl) },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        SubcomposeAsyncImage(
            model = request,
            contentDescription = attachment.label,
            contentScale = ContentScale.Fit,
            loading = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 2.dp)
                }
            },
            error = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.InsertDriveFile,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        attachment.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 280.dp)
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}
