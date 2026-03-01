package com.podcast.app.domain.usecase

import com.podcast.app.domain.model.HomeSection
import com.podcast.app.domain.model.SearchResult
import com.podcast.app.domain.repository.HomeRepository
import com.podcast.app.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHomeSectionsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<Result<List<HomeSection>>> =
        repository.getHomeSections()
}

class SearchUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    operator fun invoke(query: String): Flow<Result<List<SearchResult>>> {
        require(query.isNotBlank()) { "Query cannot be blank" }
        return repository.search(query.trim())
    }
}
