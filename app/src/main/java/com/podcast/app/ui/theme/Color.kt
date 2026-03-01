package com.podcast.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val PodcastGold = Color(0xFFF5C842)
val PodcastGoldLight = Color(0xFFFFD966)
val PodcastGoldDark = Color(0xFFD4A017)

val DarkBackground = Color(0xFF0F0F0F)
val DarkSurface = Color(0xFF1A1A1A)
val DarkSurfaceVariant = Color(0xFF242424)
val DarkCardBackground = Color(0xFF1E1E1E)
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkOnSurface = Color(0xFFE8E8E8)
val DarkOnSurfaceVariant = Color(0xFF999999)
val DarkOutline = Color(0xFF333333)
val DarkTabSelected = Color(0xFF1A1A1A)
val DarkTabUnselected = Color(0xFF2A2A2A)


val LightBackground = Color(0xFFF5F5F5)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0F0F0)
val LightCardBackground = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF0F0F0F)
val LightOnSurface = Color(0xFF1A1A1A)
val LightOnSurfaceVariant = Color(0xFF666666)
val LightOutline = Color(0xFFE0E0E0)

val DarkColorScheme = darkColorScheme(
    primary = PodcastGold,
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFF3D2E00),
    onPrimaryContainer = PodcastGoldLight,
    secondary = Color(0xFF444444),
    onSecondary = Color(0xFFFFFFFF),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = Color(0xFFCF6679)
)

val LightColorScheme = lightColorScheme(
    primary = PodcastGoldDark,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFF0C0),
    onPrimaryContainer = Color(0xFF3D2E00),
    secondary = Color(0xFF888888),
    onSecondary = Color(0xFFFFFFFF),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = Color(0xFFB3261E)
)
