package com.podcast.app.domain.repository

import com.podcast.app.domain.model.HomeSection
import com.podcast.app.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getHomeSections(): Flow<Result<List<HomeSection>>>
}

interface SearchRepository {
    fun search(query: String): Flow<Result<List<SearchResult>>>
}
