package com.podcast.app.ui.viewmodel

import com.podcast.app.domain.model.AppLanguage
import com.podcast.app.domain.model.AppSettings
import com.podcast.app.domain.usecase.GetSettingsUseCase
import com.podcast.app.domain.usecase.SaveSettingsUseCase
import com.podcast.app.ui.screens.settings.SettingsViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("SettingsViewModel")
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getSettingsUseCase:  GetSettingsUseCase
    private lateinit var saveSettingsUseCase: SaveSettingsUseCase
    private lateinit var settingsFlow:        MutableStateFlow<AppSettings>
    private lateinit var viewModel:           SettingsViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        settingsFlow        = MutableStateFlow(AppSettings())   // isDarkMode=false, language=ARABIC
        // 1. Create the mock with an explicit type
        getSettingsUseCase = mockk<GetSettingsUseCase>()
        // AppSettings() default: isDarkMode=false, language=ARABIC.
        settingsFlow        = MutableStateFlow(AppSettings())
        every { getSettingsUseCase.invoke() } returns settingsFlow
        saveSettingsUseCase = mockk(relaxed = true)

        // AppCompatDelegate.setApplicationLocales() is an Android framework call
        // that crashes on JVM. Mock it at the static level so it is a no-op.
        mockkStatic(androidx.appcompat.app.AppCompatDelegate::class)
        every {
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(any())
        } just Runs

        viewModel = SettingsViewModel(getSettingsUseCase, saveSettingsUseCase)
    }

    @AfterEach
    fun teardown() {
        unmockkStatic(androidx.appcompat.app.AppCompatDelegate::class)
        Dispatchers.resetMain()
    }

    // ─── Helper ──────────────────────────────────────────────────────────────
    //
    // stateIn(WhileSubscribed) only collects from the upstream repository flow
    // while there is at least one active downstream subscriber.  In unit tests
    // nobody holds a real subscription, so settings.value stays at the
    // initialValue = AppSettings() even after settingsFlow.value is updated.
    //
    // subscribeSettings() launches a long-lived collector in the test's
    // backgroundScope (which lives for the duration of the runTest block).
    // This activates the stateIn sharing, which then picks up upstream changes.
    //
    private fun TestScope.subscribeSettings() {
        backgroundScope.launch { viewModel.settings.collect {} }
    }

    // ─── isReady ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isReady is false before the first DataStore emission is processed")
    fun `isReady is false before first emission`() {
        assertFalse(viewModel.isReady.value)
    }

    @Test
    @DisplayName("isReady becomes true after the first DataStore value is collected")
    fun `isReady becomes true after first emission`() = runTest {
        advanceUntilIdle()
        assertTrue(viewModel.isReady.value)
    }

    @Test
    @DisplayName("isReady stays true after subsequent repository emissions")
    fun `isReady stays true after additional emissions`() = runTest {
        subscribeSettings()
        advanceUntilIdle()

        settingsFlow.value = AppSettings(isDarkMode = true, language = AppLanguage.ENGLISH)
        advanceUntilIdle()

        assertTrue(viewModel.isReady.value)
    }

    // ─── Initial state ────────────────────────────────────────────────────────

    @Test
    @DisplayName("initial settings match the domain defaults: isDarkMode=false, language=ARABIC")
    fun `initial settings are defaults`() = runTest {
        subscribeSettings()
        advanceUntilIdle()

        val state = viewModel.settings.first()
        assertFalse(state.isDarkMode)
        assertEquals(AppLanguage.ARABIC, state.language)
    }

    // ─── setDarkMode ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("setDarkMode(true) saves settings with isDarkMode = true")
    fun `setDarkMode true saves correct settings`() = runTest {
        subscribeSettings()
        advanceUntilIdle()

        viewModel.setDarkMode(true)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            saveSettingsUseCase(match { it.isDarkMode })
        }
    }

    @Test
    @DisplayName("setDarkMode(false) saves settings with isDarkMode = false")
    fun `setDarkMode false saves correct settings`() = runTest {
        subscribeSettings()
        settingsFlow.value = AppSettings(isDarkMode = true, language = AppLanguage.ARABIC)
        advanceUntilIdle()

        viewModel.setDarkMode(false)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            saveSettingsUseCase(match { !it.isDarkMode })
        }
    }

    @Test
    @DisplayName("setDarkMode preserves the current language selection")
    fun `setDarkMode does not change language`() = runTest {
        subscribeSettings()
        settingsFlow.value = AppSettings(isDarkMode = false, language = AppLanguage.ENGLISH)
        advanceUntilIdle()

        viewModel.setDarkMode(true)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            saveSettingsUseCase(match {
                it.isDarkMode && it.language == AppLanguage.ENGLISH
            })
        }
    }

    @Test
    @DisplayName("setDarkMode delegates to saveSettingsUseCase exactly once")
    fun `setDarkMode calls save exactly once`() = runTest {
        subscribeSettings()
        advanceUntilIdle()

        viewModel.setDarkMode(true)
        advanceUntilIdle()

        coVerify(exactly = 1) { saveSettingsUseCase(any()) }
    }

    // ─── setLanguage ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("setLanguage(ENGLISH) saves settings with language = ENGLISH")
    fun `setLanguage ENGLISH saves correctly`() = runTest {
        subscribeSettings()
        advanceUntilIdle()

        viewModel.setLanguage(AppLanguage.ENGLISH)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            saveSettingsUseCase(match { it.language == AppLanguage.ENGLISH })
        }
    }

    @Test
    @DisplayName("setLanguage(ARABIC) saves settings with language = ARABIC")
    fun `setLanguage ARABIC saves correctly`() = runTest {
        subscribeSettings()
        settingsFlow.value = AppSettings(isDarkMode = false, language = AppLanguage.ENGLISH)
        advanceUntilIdle()

        viewModel.setLanguage(AppLanguage.ARABIC)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            saveSettingsUseCase(match { it.language == AppLanguage.ARABIC })
        }
    }

    @Test
    @DisplayName("setLanguage preserves the current dark mode state")
    fun `setLanguage does not change dark mode`() = runTest {
        subscribeSettings()
        settingsFlow.value = AppSettings(isDarkMode = true, language = AppLanguage.ARABIC)
        advanceUntilIdle()

        viewModel.setLanguage(AppLanguage.ENGLISH)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            saveSettingsUseCase(match {
                it.language == AppLanguage.ENGLISH && it.isDarkMode
            })
        }
    }

    @Test
    @DisplayName("setLanguage invokes AppCompatDelegate.setApplicationLocales with the correct tag")
    fun `setLanguage applies locale via AppCompatDelegate`() = runTest {
        subscribeSettings()
        advanceUntilIdle()

        viewModel.setLanguage(AppLanguage.ENGLISH)
        advanceUntilIdle()

        verify(exactly = 1) {
            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                match { localeList ->
                    localeList.toLanguageTags() == AppLanguage.ENGLISH.tag
                }
            )
        }
    }

    @Test
    @DisplayName("setLanguage delegates to saveSettingsUseCase exactly once")
    fun `setLanguage calls save exactly once`() = runTest {
        subscribeSettings()
        advanceUntilIdle()

        viewModel.setLanguage(AppLanguage.ENGLISH)
        advanceUntilIdle()

        coVerify(exactly = 1) { saveSettingsUseCase(any()) }
    }

    // ─── StateFlow reflects repository updates ─────────────────────────────────

    @Test
    @DisplayName("settings StateFlow emits updated values when the repository flow changes")
    fun `settings flow emits repository updates`() = runTest {
        subscribeSettings()
        advanceUntilIdle()

        val updated = AppSettings(isDarkMode = true, language = AppLanguage.ENGLISH)
        settingsFlow.value = updated
        advanceUntilIdle()

        val collected = viewModel.settings.first()
        assertEquals(updated.isDarkMode, collected.isDarkMode)
        assertEquals(updated.language,   collected.language)
    }

    // ─── AppLanguage BCP-47 tags ───────────────────────────────────────────────

    @Test
    @DisplayName("AppLanguage.ENGLISH has BCP-47 tag 'en'")
    fun `AppLanguage ENGLISH tag is correct`() {
        assertEquals("en", AppLanguage.ENGLISH.tag)
    }

    @Test
    @DisplayName("AppLanguage.ARABIC has BCP-47 tag 'ar'")
    fun `AppLanguage ARABIC tag is correct`() {
        assertEquals("ar", AppLanguage.ARABIC.tag)
    }
}