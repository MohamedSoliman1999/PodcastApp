package com.podcast.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podcast.app.R
import com.podcast.app.domain.model.AppLanguage
import com.podcast.app.domain.model.AppSettings
import com.podcast.app.ui.theme.PodcastTheme

// ─── Screen entry point ───────────────────────────────────────────────────────

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    SettingsContent(
        settings         = settings,
        onDarkModeToggle = viewModel::setDarkMode,
        onLanguageSelect = viewModel::setLanguage
    )
}

// ─── SettingsContent (stateless) ──────────────────────────────────────────────

@Composable
fun SettingsContent(
    settings: AppSettings,
    onDarkModeToggle: (Boolean) -> Unit,
    onLanguageSelect: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Page title ────────────────────────────────────────────────────────
        Text(
            text     = stringResource(R.string.settings_title),
            style    = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color    = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // ── Dark mode row ─────────────────────────────────────────────────────
        SettingsRow(
            iconRes  = R.drawable.ic_dark_mode,
            label    = stringResource(R.string.settings_dark_mode),
            trailing = {
                DarkModeToggleButton(
                    isDarkMode = settings.isDarkMode,
                    onToggle   = onDarkModeToggle
                )
            }
        )

        HorizontalDivider(
            color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // ── Language row ──────────────────────────────────────────────────────
        SettingsRow(
            iconRes  = R.drawable.ic_language,
            label    = stringResource(R.string.settings_language),
            trailing = {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text  = stringResource(
                            if (settings.language == AppLanguage.ARABIC) R.string.language_arabic
                            else R.string.language_english
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        painter            = painterResource(R.drawable.ic_chevron_start),
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier           = Modifier.size(16.dp)
                    )
                }
            },
            onClick = { showLanguageDialog = true }
        )
    }

    // ── Language picker dialog ────────────────────────────────────────────────
    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentLanguage  = settings.language,
            onLanguageSelect = { lang ->
                onLanguageSelect(lang)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

// ─── Generic settings row ─────────────────────────────────────────────────────

@Composable
private fun SettingsRow(
    iconRes:  Int,
    label:    String,
    trailing: @Composable () -> Unit,
    onClick:  (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier              = Modifier.weight(1f)
        ) {
            Icon(
                painter            = painterResource(iconRes),
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(22.dp)
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        trailing()
    }
}

// ─── Dark mode ToggleButton ───────────────────────────────────────────────────
//
// A two-segment toggle: [☀ Light] [🌙 Dark]
// The active segment has a filled primary background; the inactive one is ghost.

@Composable
internal fun DarkModeToggleButton(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)

    Row(
        modifier = modifier
            .height(36.dp)
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape
            )
    ) {
        // ── Light segment ──────────────────────────────────────────────────
        ToggleSegment(
            iconRes     = R.drawable.ic_sun,
            label       = stringResource(R.string.toggle_light),
            isSelected  = !isDarkMode,
            onClick     = { onToggle(false) },
            isLeading   = true
        )

        // Vertical divider between the two segments
        VerticalDivider(
            color    = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        // ── Dark segment ───────────────────────────────────────────────────
        ToggleSegment(
            iconRes    = R.drawable.ic_dark_mode,
            label      = stringResource(R.string.toggle_dark),
            isSelected = isDarkMode,
            onClick    = { onToggle(true) },
            isLeading  = false
        )
    }
}

@Composable
private fun ToggleSegment(
    iconRes:    Int,
    label:      String,
    isSelected: Boolean,
    onClick:    () -> Unit,
    isLeading:  Boolean,
    modifier:   Modifier = Modifier
) {
    val bgColor    = if (isSelected) MaterialTheme.colorScheme.primary
    else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

    val segmentShape = when {
        isLeading  -> RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
        else       -> RoundedCornerShape(topEnd   = 50.dp, bottomEnd   = 50.dp)
    }

    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(segmentShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter            = painterResource(iconRes),
            contentDescription = null,
            tint               = contentColor,
            modifier           = Modifier.size(15.dp)
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor
        )
    }
}

// ─── Language Picker Dialog ───────────────────────────────────────────────────

@Composable
internal fun LanguagePickerDialog(
    currentLanguage:  AppLanguage,
    onLanguageSelect: (AppLanguage) -> Unit,
    onDismiss:        () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape            = RoundedCornerShape(20.dp),
        title = {
            Text(
                text  = stringResource(R.string.settings_language),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                AppLanguage.entries.forEach { language ->
                    LanguageRadioOption(
                        label      = stringResource(
                            if (language == AppLanguage.ARABIC) R.string.language_arabic
                            else R.string.language_english
                        ),
                        isSelected = language == currentLanguage,
                        onClick    = { onLanguageSelect(language) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text  = stringResource(R.string.dialog_cancel),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

// ─── Language radio option ────────────────────────────────────────────────────

@Composable
private fun LanguageRadioOption(
    label:      String,
    isSelected: Boolean,
    onClick:    () -> Unit,
    modifier:   Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
        RadioButton(
            selected = isSelected,
            onClick  = onClick,
            colors   = RadioButtonDefaults.colors(
                selectedColor   = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Preview data
// ═════════════════════════════════════════════════════════════════════════════

private val settingsEnLight = AppSettings(isDarkMode = false, language = AppLanguage.ENGLISH)
private val settingsEnDark  = AppSettings(isDarkMode = true,  language = AppLanguage.ENGLISH)
private val settingsArLight = AppSettings(isDarkMode = false, language = AppLanguage.ARABIC)
private val settingsArDark  = AppSettings(isDarkMode = true,  language = AppLanguage.ARABIC)

// ═════════════════════════════════════════════════════════════════════════════
// Previews: SettingsContent
// ═════════════════════════════════════════════════════════════════════════════
// ═════════════════════════════════════════════════════════════════════════════
// Previews: SettingsContent
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "SettingsContent – EN Light", locale = "en", showBackground = true)
@Composable
private fun SettingsContentEnLightPreview() {
    PodcastTheme(darkTheme = false) {
        SettingsContent(settings = settingsEnLight, onDarkModeToggle = {}, onLanguageSelect = {})
    }
}

@Preview(name = "SettingsContent – EN Dark", locale = "en", showBackground = true)
@Composable
private fun SettingsContentEnDarkPreview() {
    PodcastTheme(darkTheme = true) {
        SettingsContent(settings = settingsEnDark, onDarkModeToggle = {}, onLanguageSelect = {})
    }
}

@Preview(name = "SettingsContent – AR Light", locale = "ar", showBackground = true)
@Composable
private fun SettingsContentArLightPreview() {
    PodcastTheme(darkTheme = false) {
        SettingsContent(settings = settingsArLight, onDarkModeToggle = {}, onLanguageSelect = {})
    }
}

@Preview(name = "SettingsContent – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun SettingsContentArDarkPreview() {
    PodcastTheme(darkTheme = true) {
        SettingsContent(settings = settingsArDark, onDarkModeToggle = {}, onLanguageSelect = {})
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: DarkModeToggleButton
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "DarkModeToggleButton – EN Light | Off", locale = "en", showBackground = true)
@Composable
private fun ToggleButtonEnLightOffPreview() {
    PodcastTheme(darkTheme = false) {
        Surface { Box(Modifier.padding(16.dp)) {
            DarkModeToggleButton(isDarkMode = false, onToggle = {})
        }}
    }
}

@Preview(name = "DarkModeToggleButton – EN Dark | On", locale = "en", showBackground = true)
@Composable
private fun ToggleButtonEnDarkOnPreview() {
    PodcastTheme(darkTheme = true) {
        Surface { Box(Modifier.padding(16.dp)) {
            DarkModeToggleButton(isDarkMode = true, onToggle = {})
        }}
    }
}

@Preview(name = "DarkModeToggleButton – AR Light | Off", locale = "ar", showBackground = true)
@Composable
private fun ToggleButtonArLightOffPreview() {
    PodcastTheme(darkTheme = false) {
        Surface { Box(Modifier.padding(16.dp)) {
            DarkModeToggleButton(isDarkMode = false, onToggle = {})
        }}
    }
}

@Preview(name = "DarkModeToggleButton – AR Dark | On", locale = "ar", showBackground = true)
@Composable
private fun ToggleButtonArDarkOnPreview() {
    PodcastTheme(darkTheme = true) {
        Surface { Box(Modifier.padding(16.dp)) {
            DarkModeToggleButton(isDarkMode = true, onToggle = {})
        }}
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: LanguagePickerDialog
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "LanguagePickerDialog – EN Light", locale = "en", showBackground = true)
@Composable
private fun LangDialogEnLightPreview() {
    PodcastTheme(darkTheme = false) {
        LanguagePickerDialog(currentLanguage = AppLanguage.ENGLISH, onLanguageSelect = {}, onDismiss = {})
    }
}

@Preview(name = "LanguagePickerDialog – EN Dark", locale = "en", showBackground = true)
@Composable
private fun LangDialogEnDarkPreview() {
    PodcastTheme(darkTheme = true) {
        LanguagePickerDialog(currentLanguage = AppLanguage.ENGLISH, onLanguageSelect = {}, onDismiss = {})
    }
}

@Preview(name = "LanguagePickerDialog – AR Light", locale = "ar", showBackground = true)
@Composable
private fun LangDialogArLightPreview() {
    PodcastTheme(darkTheme = false) {
        LanguagePickerDialog(currentLanguage = AppLanguage.ARABIC, onLanguageSelect = {}, onDismiss = {})
    }
}

@Preview(name = "LanguagePickerDialog – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun LangDialogArDarkPreview() {
    PodcastTheme(darkTheme = true) {
        LanguagePickerDialog(currentLanguage = AppLanguage.ARABIC, onLanguageSelect = {}, onDismiss = {})
    }
}