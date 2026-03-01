package com.podcast.app.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.podcast.app.R
import com.podcast.app.ui.theme.PodcastTheme

// ─── Only the brand accent is fixed — it is the same in both modes ────────────
// Everything else comes from MaterialTheme.colorScheme so light/dark mode works.
private val PlayerAccent = Color(0xFFE5383B)

@Composable
fun MiniPlayerBar(
    state: PlayerState,
    onPlayPause: () -> Unit,
    onSkipForward: () -> Unit,
    onSeek: (Float) -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Read theme tokens once at composition time — they change when theme changes
    val background    = MaterialTheme.colorScheme.surface         // dark: #1A1A1A  light: #FFFFFF
    val trackColor    = MaterialTheme.colorScheme.surfaceVariant   // dark: #242424  light: #F0F0F0
    val contentColor  = MaterialTheme.colorScheme.onSurface        // dark: #E8E8E8  light: #1A1A1A

    var barWidthPx   by remember { mutableFloatStateOf(1f) }
    var isDragging   by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }
    val displayProgress = if (isDragging) dragProgress else state.progress

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(background)
            .clickable(onClick = onExpand)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Thumbnail
            AsyncImage(
                model              = state.thumbnailUrl,
                contentDescription = state.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = painterResource(R.drawable.ic_podcast_placeholder),
                error       = painterResource(R.drawable.ic_podcast_placeholder)
            )

            // Title + remaining time
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text     = state.title,
                    style    = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color    = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text  = state.remainingLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    color = PlayerAccent
                )
            }

            QueueBadgeButton(
                count        = state.queueCount,
                contentColor = contentColor,
                onClick      = {}
            )

            SkipButton(
                seconds      = state.skipSeconds,
                contentColor = contentColor,
                onClick      = onSkipForward
            )

            // Play / Pause
            if (state.isBuffering) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(32.dp),
                    color       = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter            = painterResource(
                        if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    tint               = contentColor,
                    modifier           = Modifier
                        .size(32.dp)
                        .clickable(onClick = onPlayPause)
                )
            }
        }

        // Progress bar — draggable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.BottomCenter)
                .background(trackColor)
                .onSizeChanged { barWidthPx = it.width.toFloat().coerceAtLeast(1f) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isDragging   = true
                            dragProgress = (offset.x / barWidthPx).coerceIn(0f, 1f)
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragProgress = (dragProgress + dragAmount / barWidthPx).coerceIn(0f, 1f)
                        },
                        onDragEnd    = { onSeek(dragProgress); isDragging = false },
                        onDragCancel = { isDragging = false }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(displayProgress)
                    .fillMaxHeight()
                    .background(PlayerAccent)
            )
        }
    }
}

// ─── Queue badge ──────────────────────────────────────────────────────────────

@Composable
private fun QueueBadgeButton(
    count: Int,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier         = Modifier.size(36.dp).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter            = painterResource(R.drawable.ic_queue),
            contentDescription = "Queue",
            tint               = contentColor,
            modifier           = Modifier.size(20.dp)
        )
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(PlayerAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = count.toString(),
                    color      = Color.White,
                    fontSize   = 8.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 8.sp
                )
            }
        }
    }
}

// ─── Skip button ──────────────────────────────────────────────────────────────

@Composable
private fun SkipButton(
    seconds: Int,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(1.5.dp, contentColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = seconds.toString(),
            color      = contentColor,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 11.sp
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Preview data
// ═════════════════════════════════════════════════════════════════════════════

private val previewStopped   = PlayerState(isPlaying = false, positionMs = 0L,       durationMs = 1_380_000L, queueCount = 4)
private val previewPlaying   = PlayerState(isPlaying = true,  positionMs = 360_000L, durationMs = 1_380_000L, queueCount = 4)
private val previewMidway    = PlayerState(isPlaying = true,  positionMs = 690_000L, durationMs = 1_380_000L, queueCount = 2)
private val previewBuffering = PlayerState(isPlaying = false, positionMs = 0L,       durationMs = 0L,         queueCount = 4, isBuffering = true)
private val previewAr        = PlayerState(isPlaying = false, positionMs = 200_000L, durationMs = 1_380_000L, queueCount = 4,
    title = "السفر الصامت وضوضاء الإعلانات", podcastName = "جادي")

// ═════════════════════════════════════════════════════════════════════════════
// Previews: MiniPlayerBar
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "MiniPlayerBar – EN Light | Stopped", locale = "en", showBackground = true)
@Composable
private fun MiniPlayerEnLightStoppedPreview() {
    PodcastTheme(darkTheme = false) {
        MiniPlayerBar(state = previewStopped, onPlayPause = {}, onSkipForward = {}, onSeek = {}, onExpand = {})
    }
}

@Preview(name = "MiniPlayerBar – EN Dark | Playing", locale = "en", showBackground = true)
@Composable
private fun MiniPlayerEnDarkPlayingPreview() {
    PodcastTheme(darkTheme = true) {
        MiniPlayerBar(state = previewPlaying, onPlayPause = {}, onSkipForward = {}, onSeek = {}, onExpand = {})
    }
}

@Preview(name = "MiniPlayerBar – AR Light | Midway", locale = "ar", showBackground = true)
@Composable
private fun MiniPlayerArLightMidwayPreview() {
    PodcastTheme(darkTheme = false) {
        MiniPlayerBar(state = previewAr.copy(positionMs = previewMidway.positionMs), onPlayPause = {}, onSkipForward = {}, onSeek = {}, onExpand = {})
    }
}

@Preview(name = "MiniPlayerBar – AR Dark | Playing", locale = "ar", showBackground = true)
@Composable
private fun MiniPlayerArDarkPlayingPreview() {
    PodcastTheme(darkTheme = true) {
        MiniPlayerBar(state = previewAr.copy(isPlaying = true), onPlayPause = {}, onSkipForward = {}, onSeek = {}, onExpand = {})
    }
}

@Preview(name = "MiniPlayerBar – EN Light | Buffering", locale = "en", showBackground = true)
@Composable
private fun MiniPlayerEnLightBufferingPreview() {
    PodcastTheme(darkTheme = false) {
        MiniPlayerBar(state = previewBuffering, onPlayPause = {}, onSkipForward = {}, onSeek = {}, onExpand = {})
    }
}

@Preview(name = "MiniPlayerBar – EN Dark | Buffering", locale = "en", showBackground = true)
@Composable
private fun MiniPlayerEnDarkBufferingPreview() {
    PodcastTheme(darkTheme = true) {
        MiniPlayerBar(state = previewBuffering, onPlayPause = {}, onSkipForward = {}, onSeek = {}, onExpand = {})
    }
}

@Preview(name = "MiniPlayerBar – AR Light | Buffering", locale = "ar", showBackground = true)
@Composable
private fun MiniPlayerArLightBufferingPreview() {
    PodcastTheme(darkTheme = false) {
        MiniPlayerBar(state = previewBuffering.copy(title = "السفر الصامت"), onPlayPause = {}, onSkipForward = {}, onSeek = {}, onExpand = {})
    }
}

@Preview(name = "MiniPlayerBar – AR Dark | Buffering", locale = "ar", showBackground = true)
@Composable
private fun MiniPlayerArDarkBufferingPreview() {
    PodcastTheme(darkTheme = true) {
        MiniPlayerBar(state = previewBuffering.copy(title = "السفر الصامت"), onPlayPause = {}, onSkipForward = {}, onSeek = {}, onExpand = {})
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: QueueBadgeButton
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "QueueBadgeButton – EN Light | Count 4", locale = "en", showBackground = true)
@Composable
private fun QueueBadgeEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { Box(Modifier.padding(16.dp)) { QueueBadgeButton(count = 4, contentColor = MaterialTheme.colorScheme.onSurface, onClick = {}) } } }
}

@Preview(name = "QueueBadgeButton – EN Dark | Count 4", locale = "en", showBackground = true)
@Composable
private fun QueueBadgeEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { Box(Modifier.padding(16.dp)) { QueueBadgeButton(count = 4, contentColor = MaterialTheme.colorScheme.onSurface, onClick = {}) } } }
}

@Preview(name = "QueueBadgeButton – AR Light | Count 4", locale = "ar", showBackground = true)
@Composable
private fun QueueBadgeArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { Box(Modifier.padding(16.dp)) { QueueBadgeButton(count = 4, contentColor = MaterialTheme.colorScheme.onSurface, onClick = {}) } } }
}

@Preview(name = "QueueBadgeButton – AR Dark | Count 0", locale = "ar", showBackground = true)
@Composable
private fun QueueBadgeArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { Box(Modifier.padding(16.dp)) { QueueBadgeButton(count = 0, contentColor = MaterialTheme.colorScheme.onSurface, onClick = {}) } } }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: SkipButton
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "SkipButton – EN Light", locale = "en", showBackground = true)
@Composable
private fun SkipButtonEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { Box(Modifier.padding(16.dp)) { SkipButton(seconds = 15, contentColor = MaterialTheme.colorScheme.onSurface, onClick = {}) } } }
}

@Preview(name = "SkipButton – EN Dark", locale = "en", showBackground = true)
@Composable
private fun SkipButtonEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { Box(Modifier.padding(16.dp)) { SkipButton(seconds = 15, contentColor = MaterialTheme.colorScheme.onSurface, onClick = {}) } } }
}

@Preview(name = "SkipButton – AR Light", locale = "ar", showBackground = true)
@Composable
private fun SkipButtonArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { Box(Modifier.padding(16.dp)) { SkipButton(seconds = 15, contentColor = MaterialTheme.colorScheme.onSurface, onClick = {}) } } }
}

@Preview(name = "SkipButton – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun SkipButtonArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { Box(Modifier.padding(16.dp)) { SkipButton(seconds = 15, contentColor = MaterialTheme.colorScheme.onSurface, onClick = {}) } } }
}