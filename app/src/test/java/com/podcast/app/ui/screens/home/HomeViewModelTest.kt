package com.podcast.app.ui.screens.home

import com.podcast.app.domain.model.HomeSection
import com.podcast.app.domain.model.SectionType
import com.podcast.app.domain.usecase.GetHomeSectionsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("HomeViewModel")
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var useCase: GetHomeSectionsUseCase
    private lateinit var viewModel: HomeViewModel

    private val fakeSections = listOf(
        HomeSection("1", "Section A", SectionType.LISTEN_BEFORE, emptyList()),
        HomeSection("2", "Section B", SectionType.NEW_EPISODES,  emptyList()),
        HomeSection("3", "Section C", SectionType.TEAM_PICKS,    emptyList())
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk()
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    // ─── Initial / loading state ───────────────────────────────────────────────

    @Test
    @DisplayName("isLoading is true immediately after construction, before the use-case emits")
    fun `init sets isLoading true before use-case emits`() {
        // Use a flow that never emits so we can observe the intermediate state.
        every { useCase() } returns flow { /* never emits */ }

        // With UnconfinedTestDispatcher the coroutine body in loadHome() runs until
        // the first suspension point (collecting the flow). At that point isLoading
        // has already been set to true, but no result has arrived yet.
        viewModel = HomeViewModel(useCase)

        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.sections.isEmpty())
    }

    @Test
    @DisplayName("exposes loaded sections and clears isLoading on success")
    fun `init loads sections successfully`() = runTest {
        every { useCase() } returns flowOf(Result.success(fakeSections))

        viewModel = HomeViewModel(useCase)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(fakeSections, state.sections)
        assertEquals(0, state.selectedTabIndex)
    }

    @Test
    @DisplayName("exposes errorMessage and clears isLoading on failure")
    fun `init exposes error on failure`() = runTest {
        every { useCase() } returns flowOf(Result.failure(RuntimeException("Network error")))

        viewModel = HomeViewModel(useCase)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertTrue(state.sections.isEmpty())
    }

    @Test
    @DisplayName("errorMessage contains the exception's localizedMessage")
    fun `error message matches exception message`() = runTest {
        every { useCase() } returns flowOf(Result.failure(RuntimeException("timeout")))

        viewModel = HomeViewModel(useCase)

        assertEquals("timeout", viewModel.uiState.value.errorMessage)
    }

    // ─── Retry / reload ────────────────────────────────────────────────────────

    @Test
    @DisplayName("loadHome clears the previous error and sets isLoading before re-fetching")
    fun `loadHome clears error and sets loading on retry`() {
        // First call fails, second succeeds — we only care about the state
        // between the two calls (after the failure is registered and before
        // the success arrives).
        every { useCase() } returns flowOf(Result.failure(RuntimeException("err")))
        viewModel = HomeViewModel(useCase)
        assertNotNull(viewModel.uiState.value.errorMessage)

        // Now configure success for the retry and use a never-emitting flow
        // so we can observe the intermediate isLoading=true state.
        every { useCase() } returns flow { /* never emits */ }
        viewModel.loadHome()

        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    @DisplayName("loadHome delivers new sections after a previous failure")
    fun `loadHome retries successfully after error`() = runTest {
        every { useCase() } returns flowOf(Result.failure(RuntimeException("err")))
        viewModel = HomeViewModel(useCase)

        every { useCase() } returns flowOf(Result.success(fakeSections))
        viewModel.loadHome()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(fakeSections, state.sections)
    }

    @Test
    @DisplayName("loadHome resets selectedTabIndex to 0 on successful reload")
    fun `loadHome resets selected tab on success`() = runTest {
        every { useCase() } returns flowOf(Result.success(fakeSections))
        viewModel = HomeViewModel(useCase)
        viewModel.onTabSelected(2)

        viewModel.loadHome()

        assertEquals(0, viewModel.uiState.value.selectedTabIndex)
    }

    // ─── Tab selection ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("onTabSelected updates selectedTabIndex in uiState")
    fun `onTabSelected updates tab index`() = runTest {
        every { useCase() } returns flowOf(Result.success(emptyList()))
        viewModel = HomeViewModel(useCase)

        viewModel.onTabSelected(2)

        assertEquals(2, viewModel.uiState.value.selectedTabIndex)
    }

    @Test
    @DisplayName("onTabSelected emits a ScrollToSection event with the chosen index")
    fun `onTabSelected fires ScrollToSection event`() = runTest {
        every { useCase() } returns flowOf(Result.success(emptyList()))
        viewModel = HomeViewModel(useCase)

        // MutableSharedFlow(replay=1) stores the most recent emit, so first()
        // succeeds even if the collector subscribes after tryEmit.
        viewModel.onTabSelected(1)

        val event = viewModel.events.first()
        assertTrue(event is HomeEvent.ScrollToSection && event.listIndex == 1)
    }

    @Test
    @DisplayName("onTabSelected correctly stores different tab indices")
    fun `onTabSelected cycles through valid indices`() = runTest {
        every { useCase() } returns flowOf(Result.success(fakeSections))
        viewModel = HomeViewModel(useCase)

        viewModel.onTabSelected(0)
        assertEquals(0, viewModel.uiState.value.selectedTabIndex)

        viewModel.onTabSelected(2)
        assertEquals(2, viewModel.uiState.value.selectedTabIndex)

        viewModel.onTabSelected(1)
        assertEquals(1, viewModel.uiState.value.selectedTabIndex)
    }

    // ─── Scroll → tab sync ─────────────────────────────────────────────────────

    @Test
    @DisplayName("onScrolledToSection updates selectedTabIndex without emitting a scroll event")
    fun `onScrolledToSection updates tab silently`() = runTest {
        every { useCase() } returns flowOf(Result.success(fakeSections))
        viewModel = HomeViewModel(useCase)

        // Seed the replay cache with a known event via tab click.
        viewModel.onTabSelected(0)  // → ScrollToSection(0) in cache

        // Silently scroll to section 2 — must NOT overwrite the replay cache.
        viewModel.onScrolledToSection(2)

        // Tab index updated.
        assertEquals(2, viewModel.uiState.value.selectedTabIndex)
        // Replay cache still holds index 0 (last tab-click), not 2.
        val lastEvent = viewModel.events.first()
        assertFalse(
            lastEvent is HomeEvent.ScrollToSection && lastEvent.listIndex == 2,
            "onScrolledToSection must not emit a scroll event"
        )
    }

    @Test
    @DisplayName("onScrolledToSection does nothing when the index is already selected")
    fun `onScrolledToSection is idempotent for the same index`() = runTest {
        every { useCase() } returns flowOf(Result.success(fakeSections))
        viewModel = HomeViewModel(useCase)

        viewModel.onTabSelected(1)
        val stateBefore = viewModel.uiState.value

        viewModel.onScrolledToSection(1)   // same index → no-op
        val stateAfter = viewModel.uiState.value

        assertEquals(stateBefore.selectedTabIndex, stateAfter.selectedTabIndex)
    }

    @Test
    @DisplayName("onScrolledToSection updates tab when scrolling to a different section")
    fun `onScrolledToSection updates when index differs`() = runTest {
        every { useCase() } returns flowOf(Result.success(fakeSections))
        viewModel = HomeViewModel(useCase)

        viewModel.onScrolledToSection(2)

        assertEquals(2, viewModel.uiState.value.selectedTabIndex)
    }
}