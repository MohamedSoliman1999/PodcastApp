package com.podcast.app.domain.usecase

import com.podcast.app.domain.model.HomeSection
import com.podcast.app.domain.model.SectionType
import com.podcast.app.domain.repository.HomeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("GetHomeSectionsUseCase")
class GetHomeSectionsUseCaseTest {

    private lateinit var repository: HomeRepository
    private lateinit var useCase: GetHomeSectionsUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = GetHomeSectionsUseCase(repository)
    }

    @Test
    @DisplayName("returns sections wrapped in Result.success from the repository")
    fun `invoke returns sections on success`() = runTest {
        val sections = listOf(
            HomeSection("1", "الطابور",       SectionType.QUEUE,         emptyList()),
            HomeSection("2", "اسمع قبل الناس", SectionType.LISTEN_BEFORE, emptyList())
        )
        every { repository.getHomeSections() } returns flowOf(Result.success(sections))

        val results = useCase().toList()

        assertEquals(1, results.size)
        assertTrue(results.first().isSuccess)
        assertEquals(sections, results.first().getOrNull())
    }

    @Test
    @DisplayName("propagates Result.failure from the repository unchanged")
    fun `invoke returns failure on repository error`() = runTest {
        val error = RuntimeException("Network error")
        every { repository.getHomeSections() } returns flowOf(Result.failure(error))

        val results = useCase().toList()

        assertEquals(1, results.size)
        assertTrue(results.first().isFailure)
        assertEquals(error, results.first().exceptionOrNull())
    }

    @Test
    @DisplayName("delegates to the repository exactly once per invocation")
    fun `invoke calls repository once`() = runTest {
        every { repository.getHomeSections() } returns flowOf(Result.success(emptyList()))

        useCase().toList()

        verify(exactly = 1) { repository.getHomeSections() }
    }

    @Test
    @DisplayName("returns empty list wrapped in Result.success when repository has no sections")
    fun `invoke returns empty list on empty repository response`() = runTest {
        every { repository.getHomeSections() } returns flowOf(Result.success(emptyList()))

        val results = useCase().toList()

        assertTrue(results.first().isSuccess)
        assertTrue(results.first().getOrNull()!!.isEmpty())
    }
}