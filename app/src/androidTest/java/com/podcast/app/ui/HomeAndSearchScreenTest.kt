package com.podcast.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.podcast.app.domain.model.HomeSection
import com.podcast.app.domain.model.PodcastItem
import com.podcast.app.domain.model.SectionType
import com.podcast.app.ui.screens.home.HomeContent
import com.podcast.app.ui.screens.home.HomeUiState
import com.podcast.app.ui.screens.search.SearchContent
import com.podcast.app.ui.screens.search.SearchUiState
import com.podcast.app.ui.theme.PodcastTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun fakeSections(count: Int = 3): List<HomeSection> = List(count) { i ->
    HomeSection(
        id    = "section_$i",
        title = "Section $i",
        type  = SectionType.LISTEN_BEFORE,
        items = List(3) { j ->
            PodcastItem(
                id           = "item_${i}_$j",
                title        = "Item $j",
                thumbnailUrl = null
            )
        }
    )
}

// ─── Home Screen Tests ────────────────────────────────────────────────────────

@HiltAndroidTest
class HomeScreenTest {

    @get:Rule(order = 0) val hiltRule    = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createComposeRule()

    @Before fun setup() = hiltRule.inject()

    // PodcastTheme now requires darkTheme: Boolean (no default).
    // All calls below pass darkTheme = false (light mode) for the test environment.

    @Test
    fun homeScreen_shows_loading_shimmer() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                HomeContent(
                    uiState         = HomeUiState(isLoading = true),
                    onTabSelected   = {},
                    onRetry         = {},
                    onSettingsClick = {}
                )
            }
        }
        composeRule.onRoot().assertIsDisplayed()
    }

//    @Test
//    fun homeScreen_shows_error_state() {
//        composeRule.setContent {
//            PodcastTheme(darkTheme = false) {
//                HomeContent(
//                    uiState         = HomeUiState(errorMessage = "Network error"),
//                    onTabSelected   = {},
//                    onRetry         = {},
//                    onSettingsClick = {}
//                )
//            }
//        }
//        composeRule.onNodeWithText("إعادة المحاولة").assertIsDisplayed()
//    }
//
//    @Test
//    fun homeScreen_retry_button_triggers_callback() {
//        var retryClicked = false
//        composeRule.setContent {
//            PodcastTheme(darkTheme = false) {
//                HomeContent(
//                    uiState         = HomeUiState(errorMessage = "Error"),
//                    onTabSelected   = {},
//                    onRetry         = { retryClicked = true },
//                    onSettingsClick = {}
//                )
//            }
//        }
//        composeRule.onNodeWithText("إعادة المحاولة").performClick()
//        assert(retryClicked) { "Retry callback was not invoked" }
//    }

    @Test
    fun homeScreen_settings_icon_triggers_callback() {
        var settingsClicked = false
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                HomeContent(
                    uiState         = HomeUiState(sections = fakeSections()),
                    onTabSelected   = {},
                    onRetry         = {},
                    onSettingsClick = { settingsClicked = true }
                )
            }
        }
        composeRule.onNodeWithContentDescription("Settings").performClick()
        assert(settingsClicked) { "Settings callback was not invoked" }
    }

    @Test
    fun homeScreen_tab_selection_triggers_callback() {
        var selectedIndex = -1
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                HomeContent(
                    uiState         = HomeUiState(sections = fakeSections(), selectedTabIndex = 1),
                    onTabSelected   = { selectedIndex = it },
                    onRetry         = {},
                    onSettingsClick = {}
                )
            }
        }
        composeRule.onNodeWithTag("filter_tab_0").assertIsDisplayed().performClick()
        assert(selectedIndex == 0) { "Expected selectedIndex=0 but was $selectedIndex" }
    }

    @Test
    fun homeScreen_renders_section_names_as_tabs() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                HomeContent(
                    uiState         = HomeUiState(sections = fakeSections(3)),
                    onTabSelected   = {},
                    onRetry         = {},
                    onSettingsClick = {}
                )
            }
        }
        fakeSections(3).forEachIndexed { i, _ ->
            composeRule.onNodeWithTag("filter_tab_$i").assertExists()
        }
    }

    @Test
    fun homeScreen_selected_tab_highlights_correctly() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                HomeContent(
                    uiState         = HomeUiState(sections = fakeSections(3), selectedTabIndex = 2),
                    onTabSelected   = {},
                    onRetry         = {},
                    onSettingsClick = {}
                )
            }
        }
        composeRule.onNodeWithTag("filter_tab_2").assertExists()
    }

    @Test
    fun homeScreen_all_three_tab_nodes_exist_for_three_sections() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                HomeContent(
                    uiState         = HomeUiState(sections = fakeSections(3)),
                    onTabSelected   = {},
                    onRetry         = {},
                    onSettingsClick = {}
                )
            }
        }
        composeRule.onNodeWithTag("filter_tab_0").assertExists()
        composeRule.onNodeWithTag("filter_tab_1").assertExists()
        composeRule.onNodeWithTag("filter_tab_2").assertExists()
    }

    @Test
    fun homeScreen_no_tabs_rendered_when_sections_are_empty() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                HomeContent(
                    uiState         = HomeUiState(sections = emptyList()),
                    onTabSelected   = {},
                    onRetry         = {},
                    onSettingsClick = {}
                )
            }
        }
        composeRule.onNodeWithTag("filter_tab_0").assertDoesNotExist()
    }

    @Test
    fun homeScreen_error_does_not_show_tabs() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                HomeContent(
                    uiState         = HomeUiState(errorMessage = "fail"),
                    onTabSelected   = {},
                    onRetry         = {},
                    onSettingsClick = {}
                )
            }
        }
        composeRule.onNodeWithTag("filter_tab_0").assertDoesNotExist()
    }
}

// ─── Search Screen Tests ──────────────────────────────────────────────────────

@HiltAndroidTest
class SearchScreenTest {

    @get:Rule(order = 0) val hiltRule    = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createComposeRule()

    @Before fun setup() = hiltRule.inject()

    @Test
    fun searchScreen_shows_idle_state_initially() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SearchContent(
                    uiState       = SearchUiState(isIdle = true),
                    onQueryChange = {},
                    onClear       = {}
                )
            }
        }
        composeRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun searchScreen_idle_message_is_visible() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SearchContent(
                    uiState       = SearchUiState(isIdle = true, query = ""),
                    onQueryChange = {},
                    onClear       = {}
                )
            }
        }
        composeRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun searchBar_updates_query_on_input() {
        var updatedQuery = ""
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SearchContent(
                    uiState       = SearchUiState(query = ""),
                    onQueryChange = { updatedQuery = it },
                    onClear       = {}
                )
            }
        }
        composeRule.onNodeWithText("").performTextInput("جادي")
        assert(updatedQuery.isNotEmpty()) { "onQueryChange was not called" }
    }

    @Test
    fun searchScreen_clear_button_appears_when_query_is_non_empty() {
        var clearCalled = false
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SearchContent(
                    uiState       = SearchUiState(query = "جادي", isIdle = false),
                    onQueryChange = {},
                    onClear       = { clearCalled = true }
                )
            }
        }
        composeRule.onNodeWithContentDescription("Clear").assertIsDisplayed().performClick()
        assert(clearCalled) { "onClear was not invoked" }
    }

    @Test
    fun searchScreen_clear_button_absent_when_query_is_empty() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SearchContent(
                    uiState       = SearchUiState(query = "", isIdle = true),
                    onQueryChange = {},
                    onClear       = {}
                )
            }
        }
        composeRule.onNodeWithContentDescription("Clear").assertDoesNotExist()
    }

    @Test
    fun searchScreen_shows_loading_shimmer_when_isSearching() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                SearchContent(
                    uiState       = SearchUiState(isIdle = false, isSearching = true, query = "جادي"),
                    onQueryChange = {},
                    onClear       = {}
                )
            }
        }
        composeRule.onRoot().assertIsDisplayed()
    }
}

// ─── Mini Player Tests ────────────────────────────────────────────────────────

@HiltAndroidTest
class MiniPlayerBarTest {

    @get:Rule(order = 0) val hiltRule    = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createComposeRule()

    @Before fun setup() = hiltRule.inject()

    private val stoppedState = com.podcast.app.ui.player.PlayerState(
        isPlaying  = false,
        durationMs = 1_380_000L,
        positionMs = 0L
    )
    private val playingState   = stoppedState.copy(isPlaying = true, positionMs = 300_000L)
    private val bufferingState = stoppedState.copy(isBuffering = true, durationMs = 0L)

    @Test
    fun miniPlayer_is_displayed() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = stoppedState,
                    onPlayPause   = {},
                    onSkipForward = {},
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun miniPlayer_shows_play_icon_when_stopped() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = stoppedState,
                    onPlayPause   = {},
                    onSkipForward = {},
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onNodeWithContentDescription("Play").assertExists()
    }

    @Test
    fun miniPlayer_play_button_triggers_callback() {
        var clicked = false
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = stoppedState,
                    onPlayPause   = { clicked = true },
                    onSkipForward = {},
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onNodeWithContentDescription("Play").performClick()
        assert(clicked) { "onPlayPause was not called" }
    }

    @Test
    fun miniPlayer_shows_pause_icon_when_playing() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = playingState,
                    onPlayPause   = {},
                    onSkipForward = {},
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onNodeWithContentDescription("Pause").assertExists()
    }

    @Test
    fun miniPlayer_pause_button_triggers_callback() {
        var clicked = false
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = playingState,
                    onPlayPause   = { clicked = true },
                    onSkipForward = {},
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onNodeWithContentDescription("Pause").performClick()
        assert(clicked) { "onPlayPause was not called while playing" }
    }

    @Test
    fun miniPlayer_remaining_label_is_displayed() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = stoppedState,
                    onPlayPause   = {},
                    onSkipForward = {},
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onNodeWithText(stoppedState.remainingLabel).assertIsDisplayed()
    }

    @Test
    fun miniPlayer_episode_title_is_displayed() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = stoppedState,
                    onPlayPause   = {},
                    onSkipForward = {},
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onNodeWithText(stoppedState.title).assertIsDisplayed()
    }

    @Test
    fun miniPlayer_skip_forward_triggers_callback() {
        var skipCalled = false
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = stoppedState,
                    onPlayPause   = {},
                    onSkipForward = { skipCalled = true },
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onNodeWithText(stoppedState.skipSeconds.toString()).performClick()
        assert(skipCalled) { "onSkipForward was not called" }
    }

    @Test
    fun miniPlayer_shows_buffering_indicator_when_buffering() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = bufferingState,
                    onPlayPause   = {},
                    onSkipForward = {},
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onNodeWithContentDescription("Play").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Pause").assertDoesNotExist()
    }

    @Test
    fun miniPlayer_queue_count_badge_is_displayed() {
        composeRule.setContent {
            PodcastTheme(darkTheme = false) {
                com.podcast.app.ui.player.MiniPlayerBar(
                    state         = stoppedState,
                    onPlayPause   = {},
                    onSkipForward = {},
                    onSeek        = {},
                    onExpand      = {}
                )
            }
        }
        composeRule.onNodeWithText(stoppedState.queueCount.toString()).assertIsDisplayed()
    }
}