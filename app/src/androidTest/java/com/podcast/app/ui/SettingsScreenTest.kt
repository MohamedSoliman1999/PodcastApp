package com.podcast.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.podcast.app.domain.model.AppLanguage
import com.podcast.app.domain.model.AppSettings
import com.podcast.app.ui.screens.settings.DarkModeToggleButton
import com.podcast.app.ui.screens.settings.LanguagePickerDialog
import com.podcast.app.ui.screens.settings.SettingsContent
import com.podcast.app.ui.theme.PodcastTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingsScreenTest {

    @get:Rule(order = 0) val hiltRule    = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createComposeRule()

    @Before fun setup() = hiltRule.inject()

    private val defaultSettings = AppSettings(isDarkMode = false, language = AppLanguage.ARABIC)
    private val darkSettings    = AppSettings(isDarkMode = true,  language = AppLanguage.ARABIC)
    private val englishSettings = AppSettings(isDarkMode = false, language = AppLanguage.ENGLISH)

    // ── SettingsContent structural tests ──────────────────────────────────────

    @Test
    fun settingsScreen_is_displayed() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SettingsContent(
                    settings         = defaultSettings,
                    onDarkModeToggle = {},
                    onLanguageSelect = {}
                )
            }
        }
        composeRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun settingsScreen_shows_dark_mode_row() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SettingsContent(
                    settings         = defaultSettings,
                    onDarkModeToggle = {},
                    onLanguageSelect = {}
                )
            }
        }
        composeRule.onNodeWithText("Dark Mode").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_shows_language_row() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SettingsContent(
                    settings         = defaultSettings,
                    onDarkModeToggle = {},
                    onLanguageSelect = {}
                )
            }
        }
        composeRule.onNodeWithText("Language").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_language_row_shows_current_language_arabic() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SettingsContent(
                    settings         = defaultSettings,
                    onDarkModeToggle = {},
                    onLanguageSelect = {}
                )
            }
        }
        composeRule.onNodeWithText("Arabic").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_language_row_shows_current_language_english() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SettingsContent(
                    settings         = englishSettings,
                    onDarkModeToggle = {},
                    onLanguageSelect = {}
                )
            }
        }
        composeRule.onNodeWithText("English").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_tapping_language_row_opens_picker_dialog() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SettingsContent(
                    settings         = defaultSettings,
                    onDarkModeToggle = {},
                    onLanguageSelect = {}
                )
            }
        }
        composeRule.onNodeWithText("Language").performClick()
        composeRule.onAllNodesWithText("Arabic").onFirst().assertIsDisplayed()
        composeRule.onAllNodesWithText("English").onFirst().assertIsDisplayed()
    }

    // ── DarkModeToggleButton ───────────────────────────────────────────────────

    @Test
    fun darkModeToggle_shows_light_and_dark_segments() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                DarkModeToggleButton(isDarkMode = false, onToggle = {})
            }
        }
        composeRule.onNodeWithText("Light").assertIsDisplayed()
        composeRule.onNodeWithText("Dark").assertIsDisplayed()
    }

    @Test
    fun darkModeToggle_clicking_dark_segment_invokes_callback_with_true() {
        var received: Boolean? = null
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                DarkModeToggleButton(isDarkMode = false, onToggle = { received = it })
            }
        }
        composeRule.onNodeWithText("Dark").performClick()
        assert(received == true) { "Expected onToggle(true) but received $received" }
    }

    @Test
    fun darkModeToggle_clicking_light_segment_invokes_callback_with_false() {
        var received: Boolean? = null
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                DarkModeToggleButton(isDarkMode = true, onToggle = { received = it })
            }
        }
        composeRule.onNodeWithText("Light").performClick()
        assert(received == false) { "Expected onToggle(false) but received $received" }
    }

    // ── LanguagePickerDialog ───────────────────────────────────────────────────

    @Test
    fun languageDialog_shows_both_options() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                LanguagePickerDialog(
                    currentLanguage  = AppLanguage.ARABIC,
                    onLanguageSelect = {},
                    onDismiss        = {}
                )
            }
        }
        composeRule.onNodeWithText("Arabic").assertIsDisplayed()
        composeRule.onNodeWithText("English").assertIsDisplayed()
    }

    @Test
    fun languageDialog_selecting_english_invokes_callback() {
        var selected: AppLanguage? = null
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                LanguagePickerDialog(
                    currentLanguage  = AppLanguage.ARABIC,
                    onLanguageSelect = { selected = it },
                    onDismiss        = {}
                )
            }
        }
        composeRule.onNodeWithText("English").performClick()
        assert(selected == AppLanguage.ENGLISH) { "Expected ENGLISH but got $selected" }
    }

    @Test
    fun languageDialog_selecting_arabic_invokes_callback() {
        var selected: AppLanguage? = null
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                LanguagePickerDialog(
                    currentLanguage  = AppLanguage.ENGLISH,
                    onLanguageSelect = { selected = it },
                    onDismiss        = {}
                )
            }
        }
        composeRule.onNodeWithText("Arabic").performClick()
        assert(selected == AppLanguage.ARABIC) { "Expected ARABIC but got $selected" }
    }

    @Test
    fun languageDialog_cancel_button_invokes_dismiss() {
        var dismissed = false
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                LanguagePickerDialog(
                    currentLanguage  = AppLanguage.ARABIC,
                    onLanguageSelect = {},
                    onDismiss        = { dismissed = true }
                )
            }
        }
        composeRule.onNodeWithText("Cancel").performClick()
        assert(dismissed) { "onDismiss was not called after Cancel" }
    }

    @Test
    fun languageDialog_cancel_button_is_visible() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                LanguagePickerDialog(
                    currentLanguage  = AppLanguage.ARABIC,
                    onLanguageSelect = {},
                    onDismiss        = {}
                )
            }
        }
        composeRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    // ── Dark/Light mode rendering ─────────────────────────────────────────────

    @Test
    fun settingsScreen_renders_correctly_in_dark_mode() {
        composeRule.setContent {
            PodcastTheme(darkTheme = true) {
                SettingsContent(
                    settings         = darkSettings,
                    onDarkModeToggle = {},
                    onLanguageSelect = {}
                )
            }
        }
        composeRule.onRoot().assertIsDisplayed()
        composeRule.onNodeWithText("Dark Mode").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_renders_correctly_in_light_mode() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SettingsContent(
                    settings         = defaultSettings,
                    onDarkModeToggle = {},
                    onLanguageSelect = {}
                )
            }
        }
        composeRule.onRoot().assertIsDisplayed()
        composeRule.onNodeWithText("Dark Mode").assertIsDisplayed()
    }
}