/**
 * File: ChipsAndBadges.kt
 *
 * What it does:
 * Reusable chip and badge composables for rendering metrics, GitLab labels, file attachment buttons, and image previews.
 *
 * Touchpoints:
 * - com.example.labroxy.ui.components.WorkItemCards: Renders label lists (`LabelRow`) and star/fork metrics (`MetricChip`).
 * - com.example.labroxy.ui.screens.detail.DiscussionComponents: Renders comment attachments (`CommentFileChip`, `CommentImagePreview`).
 * - com.example.labroxy.ui.util.ColorUtils: Computes contrast colors for `GitLabLabelChip`.
 *
 * Features / Functions:
 * - `MetricChip`: Assist chip displaying icon and count/text.
 * - `LabelRow`: Flow row of label chips.
 * - `GitLabLabelChip`: Styled label chip using custom hex background and text colors.
 * - `CommentFileChip`: Interactive attachment chip for opening external files.
 * - `CommentImagePreview`: SubcomposeAsyncImage card preview for comment image attachments with SVG support.
 */
package com.example.labroxy.ui.components

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.labroxy.data.GitLabLabel
import com.example.labroxy.ui.util.CommentAttachment
import com.example.labroxy.ui.util.gitLabColor
import com.example.labroxy.ui.util.readableOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

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
    var viewerOpen by remember(attachment.url) { mutableStateOf(false) }
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
            .clickable { viewerOpen = true },
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

    if (viewerOpen) {
        FullscreenImageViewer(
            request = request,
            contentDescription = attachment.label,
            downloadUrl = attachment.url,
            token = token,
            onDismiss = { viewerOpen = false }
        )
    }
}

@Composable
private fun FullscreenImageViewer(
    request: ImageRequest,
    contentDescription: String,
    downloadUrl: String,
    token: String,
    onDismiss: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val overlayColor = if (isDark) Color.Black else Color.White
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor.copy(alpha = 0.8f))
                .clipToBounds()
        ) {
            SubcomposeAsyncImage(
                model = request,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                loading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Unable to load image",
                            color = if (isDark) Color.White else Color.Black
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offsetX += pan.x
                            offsetY += pan.y
                            if (scale == 1f) {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            downloadImage(context, downloadUrl, token, contentDescription)
                        }
                    },
                    modifier = Modifier.background(
                        if (isDark) Color.White.copy(alpha = 0.18f)
                        else Color.Black.copy(alpha = 0.12f),
                        RoundedCornerShape(50)
                    )
                ) {
                    Icon(
                        Icons.Outlined.Download,
                        contentDescription = "Download image",
                        tint = if (isDark) Color.White else Color.Black
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.background(
                        if (isDark) Color.White.copy(alpha = 0.18f)
                        else Color.Black.copy(alpha = 0.12f),
                        RoundedCornerShape(50)
                    )
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close image",
                        tint = if (isDark) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

private suspend fun downloadImage(context: Context, url: String, token: String, label: String) {
    try {
        withContext(Dispatchers.IO) {
            val connection = (java.net.URL(url).openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 30_000
                readTimeout = 60_000
                if (token.isNotBlank()) setRequestProperty("PRIVATE-TOKEN", token)
            }
            try {
                val responseCode = connection.responseCode
                if (responseCode !in 200..299) error("Server returned HTTP $responseCode")

                val serverMimeType = connection.contentType?.substringBefore(';')?.takeIf { it.isNotBlank() }
                val urlPathName = java.net.URL(url).path.substringAfterLast('/').takeIf { it.isNotBlank() }
                    ?.let { java.net.URLDecoder.decode(it, "UTF-8") }
                val headerFileName = connection.getHeaderField("Content-Disposition")
                    ?.let { header ->
                        Regex("filename\\*=UTF-8''([^;]+)|filename=\\\"?([^;\\\"]+)\\\"?", RegexOption.IGNORE_CASE)
                            .find(header)
                            ?.let { match -> match.groups[1]?.value ?: match.groups[2]?.value }
                    }
                val sourceName = headerFileName ?: urlPathName ?: label
                val fileHint = "$sourceName $url".lowercase(Locale.ROOT)
                val inferredMimeType = when {
                    fileHint.endsWith(".jpg") || fileHint.endsWith(".jpeg") -> "image/jpeg"
                    fileHint.endsWith(".png") -> "image/png"
                    fileHint.endsWith(".gif") -> "image/gif"
                    fileHint.endsWith(".webp") -> "image/webp"
                    fileHint.endsWith(".svg") -> "image/svg+xml"
                    else -> null
                }
                val mimeType = serverMimeType?.takeIf { it.startsWith("image/", ignoreCase = true) }
                    ?: inferredMimeType
                    ?: "image/png"
                val extension = when (mimeType.lowercase(Locale.ROOT)) {
                    "image/jpeg" -> ".jpg"
                    "image/png" -> ".png"
                    "image/gif" -> ".gif"
                    "image/webp" -> ".webp"
                    "image/svg+xml" -> ".svg"
                    else -> ""
                }
                val baseName = sourceName.substringAfterLast('/').substringBefore('?')
                    .replace(Regex("[^a-zA-Z0-9._-]"), "_")
                    .ifBlank { "labroxy_image" }
                val fileName = if (baseName.contains('.')) baseName else baseName + extension

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Labroxy")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                    val resolver = context.contentResolver
                    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                        ?: error("Unable to create image file")
                    try {
                        resolver.openOutputStream(uri)?.use { output ->
                            connection.inputStream.use { input -> input.copyTo(output) }
                        } ?: error("Unable to open image file")
                        resolver.update(uri, ContentValues().apply {
                            put(MediaStore.Images.Media.IS_PENDING, 0)
                        }, null, null)
                    } catch (error: Throwable) {
                        resolver.delete(uri, null, null)
                        throw error
                    }
                } else {
                    val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        ?: error("Pictures directory unavailable")
                    directory.mkdirs()
                    java.io.File(directory, fileName).outputStream().use { output ->
                        connection.inputStream.use { input -> input.copyTo(output) }
                    }
                }
            } finally {
                connection.disconnect()
            }
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "download Successful", Toast.LENGTH_SHORT).show()
        }
    } catch (error: Throwable) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Download failed: ${error.message ?: "unknown error"}", Toast.LENGTH_SHORT).show()
        }
    }
}
