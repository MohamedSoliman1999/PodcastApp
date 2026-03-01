package com.podcast.app.ui.viewmodel

import com.podcast.app.domain.model.SearchResult
import com.podcast.app.domain.model.SearchResultType
import com.podcast.app.domain.usecase.SearchUseCase
import com.podcast.app.ui.screens.search.SearchViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("SearchViewModel")
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: SearchUseCase
    private lateinit var viewModel: SearchViewModel

    private val sampleResults = listOf(
        SearchResult("1", "جادي", null, null, SearchResultType.PODCAST, 150)
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk()

        // Global default: any query returns empty success.
        // Individual tests that need different behaviour can override with their own every{}.
        every { useCase(any()) } returns flowOf(Result.success(emptyList()))

        viewModel = SearchViewModel(useCase)
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    // ─── Initial state ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("initial state is idle with a blank query and no results")
    fun `initial state is idle`() {
        val state = viewModel.uiState.value
        assertTrue(state.isIdle)
        assertFalse(state.isSearching)
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertNull(state.errorMessage)
    }

    // ─── Query updates ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("onQueryChange reflects the new query in uiState immediately")
    fun `onQueryChange updates query immediately`() = runTest {
        // The query state is set synchronously; we check BEFORE the debounce fires.
        viewModel.onQueryChange("جادي")
        // Only advance by 1 ms — well within the 200 ms debounce window —
        // so the use-case coroutine doesn't start.
        advanceTimeBy(1L)
        testDispatcher.scheduler.runCurrent()
        assertEquals("جادي", viewModel.uiState.value.query)
    }

    @Test
    @DisplayName("onQueryChange with a blank string sets isIdle = true")
    fun `blank query restores idle state`() = runTest {
        viewModel.onQueryChange("جادي")
        viewModel.onQueryChange("")
        advanceTimeBy(1L)
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isIdle)
    }

    @Test
    @DisplayName("onQueryChange with non-blank query sets isIdle = false")
    fun `non-blank query clears idle flag`() = runTest {
        // Only check the flag, don't advance past the debounce.
        viewModel.onQueryChange("جادي")
        advanceTimeBy(1L)
        testDispatcher.scheduler.runCurrent()
        assertFalse(viewModel.uiState.value.isIdle)
    }

    // ─── clearSearch ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("clearSearch resets all state to its initial defaults")
    fun `clearSearch resets to initial state`() = runTest {
        every { useCase(any()) } returns flowOf(Result.success(sampleResults))

        viewModel.onQueryChange("جادي")
        advanceTimeBy(201L)
        testDispatcher.scheduler.runCurrent()

        viewModel.clearSearch()

        val state = viewModel.uiState.value
        assertTrue(state.isIdle)
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertNull(state.errorMessage)
        assertFalse(state.isSearching)
    }

    // ─── 200 ms debounce ───────────────────────────────────────────────────────

    @Test
    @DisplayName("use-case is NOT invoked before the 200 ms debounce window has elapsed")
    fun `search not triggered before debounce window`() = runTest {
        viewModel.onQueryChange("جادي")
        advanceTimeBy(199L)             // one ms short of the threshold
        testDispatcher.scheduler.runCurrent()

        verify(exactly = 0) { useCase(any()) }
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    @DisplayName("use-case IS invoked after 200 ms have elapsed since the last keystroke")
    fun `search triggered after 200ms debounce`() = runTest {
        every { useCase("جادي") } returns flowOf(Result.success(sampleResults))

        viewModel.onQueryChange("جادي")
        advanceTimeBy(201L)
        testDispatcher.scheduler.runCurrent()

        verify(exactly = 1) { useCase("جادي") }
    }

    @Test
    @DisplayName("rapid typing within the window fires only one search for the final query")
    fun `rapid typing fires one search for final query`() = runTest {
        every { useCase("جادي") } returns flowOf(Result.success(sampleResults))

        viewModel.onQueryChange("ج")
        advanceTimeBy(50L)
        viewModel.onQueryChange("جا")
        advanceTimeBy(50L)
        viewModel.onQueryChange("جاد")
        advanceTimeBy(50L)
        viewModel.onQueryChange("جادي")
        advanceTimeBy(201L)
        testDispatcher.scheduler.runCurrent()

        verify(exactly = 0) { useCase("ج") }
        verify(exactly = 0) { useCase("جا") }
        verify(exactly = 0) { useCase("جاد") }
        verify(exactly = 1) { useCase("جادي") }
    }

    // ─── Request cancellation (flatMapLatest) ──────────────────────────────────

    @Test
    @DisplayName("a new query cancels the previous in-flight request before it completes")
    fun `new query cancels previous in-flight request`() = runTest {
        var firstQueryCompleted = false
        every { useCase("جادي") } returns flow {
            kotlinx.coroutines.delay(500L)
            firstQueryCompleted = true
            emit(Result.success(sampleResults))
        }
        val newResults = listOf(SearchResult("2", "NPR", null, null, SearchResultType.PODCAST))
        every { useCase("NPR") } returns flowOf(Result.success(newResults))

        viewModel.onQueryChange("جادي")
        advanceTimeBy(201L)
        testDispatcher.scheduler.runCurrent()

        viewModel.onQueryChange("NPR")
        advanceTimeBy(201L)
        advanceTimeBy(600L)
        testDispatcher.scheduler.runCurrent()

        assertFalse(firstQueryCompleted, "First request should have been cancelled by flatMapLatest")
        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals(newResults, state.results)
    }

    // ─── Success / failure ─────────────────────────────────────────────────────

    @Test
    @DisplayName("successful search populates results and clears isSearching")
    fun `successful search populates results`() = runTest {
        every { useCase("جادي") } returns flowOf(Result.success(sampleResults))

        viewModel.onQueryChange("جادي")
        advanceTimeBy(201L)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertFalse(state.isIdle)
        assertEquals(sampleResults, state.results)
        assertNull(state.errorMessage)
    }

    @Test
    @DisplayName("failed search sets errorMessage and clears isSearching")
    fun `failed search sets error message`() = runTest {
        every { useCase("جادي") } returns flowOf(Result.failure(RuntimeException("Network error")))

        viewModel.onQueryChange("جادي")
        advanceTimeBy(201L)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertNotNull(state.errorMessage)
        assertTrue(state.results.isEmpty())
    }

    @Test
    @DisplayName("a query shorter than 2 characters is never forwarded to the use-case")
    fun `single char query is ignored`() = runTest {
        viewModel.onQueryChange("ج")
        advanceTimeBy(300L)
        testDispatcher.scheduler.runCurrent()

        verify(exactly = 0) { useCase(any()) }
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    @DisplayName("isSearching transitions to true while the network request is in flight")
    fun `isSearching is true while request is in flight`() = runTest {
        every { useCase("جادي") } returns flow {
            kotlinx.coroutines.delay(300L)
            emit(Result.success(sampleResults))
        }

        viewModel.onQueryChange("جادي")
        advanceTimeBy(201L)
        testDispatcher.scheduler.runCurrent()

        assertTrue(viewModel.uiState.value.isSearching)

        advanceTimeBy(300L)
        testDispatcher.scheduler.runCurrent()

        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    @DisplayName("empty results from the use-case are reflected in the results list")
    fun `empty search results are reflected in state`() = runTest {
        every { useCase("xyz") } returns flowOf(Result.success(emptyList()))

        viewModel.onQueryChange("xyz")
        advanceTimeBy(201L)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertFalse(state.isIdle)
        assertTrue(state.results.isEmpty())
        assertNull(state.errorMessage)
    }
}