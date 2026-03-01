package com.podcast.app.domain.usecase

import com.podcast.app.domain.model.SearchResult
import com.podcast.app.domain.model.SearchResultType
import com.podcast.app.domain.repository.SearchRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("SearchUseCase")
class SearchUseCaseTest {

    private lateinit var repository: SearchRepository
    private lateinit var useCase: SearchUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = SearchUseCase(repository)
    }

    @Test
    @DisplayName("returns results for a valid non-blank query")
    fun `invoke returns results for valid query`() = runTest {
        val results = listOf(
            SearchResult("1", "جادي", null, null, SearchResultType.PODCAST)
        )
        every { repository.search("جادي") } returns flowOf(Result.success(results))

        val output = useCase("جادي").toList()

        assertTrue(output.first().isSuccess)
        assertEquals(results, output.first().getOrNull())
    }

    @Test
    @DisplayName("trims leading and trailing whitespace before passing to repository")
    fun `invoke trims query before passing to repository`() = runTest {
        every { repository.search("جادي") } returns flowOf(Result.success(emptyList()))

        useCase("  جادي  ").toList()

        verify { repository.search("جادي") }
    }

    @ParameterizedTest(name = "throws IllegalArgumentException for blank query: [{0}]")
    @ValueSource(strings = ["", "   "])
    fun `invoke throws for blank query`(query: String) {
        assertThrows(IllegalArgumentException::class.java) {
            useCase(query)
        }
    }

    @Test
    @DisplayName("propagates repository failure as Result.failure")
    fun `invoke propagates failure`() = runTest {
        val error = RuntimeException("Search failed")
        every { repository.search(any()) } returns flowOf(Result.failure(error))

        val output = useCase("test").toList()

        assertTrue(output.first().isFailure)
        assertEquals(error, output.first().exceptionOrNull())
    }

    @Test
    @DisplayName("returns empty list wrapped in Result.success when repository has no results")
    fun `invoke returns empty list when no results found`() = runTest {
        every { repository.search("unknown") } returns flowOf(Result.success(emptyList()))

        val output = useCase("unknown").toList()

        assertTrue(output.first().isSuccess)
        assertTrue(output.first().getOrNull()!!.isEmpty())
    }

    @Test
    @DisplayName("delegates to repository exactly once per invocation")
    fun `invoke calls repository once`() = runTest {
        every { repository.search(any()) } returns flowOf(Result.success(emptyList()))

        useCase("query").toList()

        verify(exactly = 1) { repository.search("query") }
    }
}