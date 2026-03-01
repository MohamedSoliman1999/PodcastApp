package com.podcast.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.podcast.app.R
import com.podcast.app.ui.navigation.Screen
import com.podcast.app.ui.theme.PodcastTheme

data class BottomNavItem(
    val screen: Screen,
    val iconRes: Int,
    val labelRes: Int
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, R.drawable.ic_home, R.string.nav_home),
    BottomNavItem(Screen.Search, R.drawable.ic_search, R.string.nav_search),
    BottomNavItem(Screen.Library, R.drawable.ic_library, R.string.nav_library),
    BottomNavItem(Screen.Settings, R.drawable.ic_settings, R.string.nav_settings)
)

@Composable
fun PodcastBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = stringResource(item.labelRes),
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}


@Composable
fun MiniPlayerBar(
    episodeTitle: String,
    isPlaying: Boolean,
    thumbnailUrl: String?,
    onPlayPause: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PodcastThumbnail(
                imageUrl = thumbnailUrl,
                contentDescription = episodeTitle,
                modifier = Modifier.size(40.dp),
                cornerRadius = 6.dp
            )
            Text(
                text = episodeTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            IconButton(onClick = onPlayPause, modifier = Modifier.size(40.dp)) {
                Icon(
                    painter = painterResource(
                        if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(name = "Bottom Bar - EN Light", locale = "en")
@Composable
private fun BottomBarEnLightPreview() {
    PodcastTheme(darkTheme = false,{
        PodcastBottomBar(currentRoute = Screen.Home.route, onNavigate = {})
    })
}

@Preview(name = "Bottom Bar - EN Dark", locale = "en")
@Composable
private fun BottomBarEnDarkPreview() {
    PodcastTheme(darkTheme = true,{
        PodcastBottomBar(currentRoute = Screen.Home.route, onNavigate = {})
    })
}

@Preview(name = "Bottom Bar - AR Light", locale = "ar")
@Composable
private fun BottomBarArLightPreview() {
    PodcastTheme(darkTheme = false,{
        PodcastBottomBar(currentRoute = Screen.Home.route, onNavigate = {})
    })
}

@Preview(name = "Bottom Bar - AR Dark", locale = "ar")
@Composable
private fun BottomBarArDarkPreview() {
    PodcastTheme(darkTheme = true,{
        PodcastBottomBar(currentRoute = Screen.Home.route, onNavigate = {})
    })
}
