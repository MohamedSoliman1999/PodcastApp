package com.podcast.app.data.repository

import android.util.Log
import com.podcast.app.data.remote.api.HomeApi
import com.podcast.app.data.remote.api.SearchApi
import com.podcast.app.data.remote.dto.toSearchResult
import com.podcast.app.data.remote.dto.toDomain
import com.podcast.app.domain.model.HomeSection
import com.podcast.app.domain.model.SearchResult
import com.podcast.app.domain.repository.HomeRepository
import com.podcast.app.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
) : HomeRepository {

    override fun getHomeSections(): Flow<Result<List<HomeSection>>> = flow {
        try {
            val response = api.getHomeSections()
            val sections = response.sections
                .orEmpty()
                .sortedBy { it.order ?: Int.MAX_VALUE }
                .filter { !it.content.isNullOrEmpty() }
                .map { it.toDomain() }
            emit(Result.success(sections))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val api: SearchApi
) : SearchRepository {

    override fun search(query: String): Flow<Result<List<SearchResult>>> = flow {
        try {
            val response = api.search(query)
            val results = when {
                !response.sections.isNullOrEmpty() -> {
                    response.sections.flatMapIndexed { sectionIndex, section ->
                        section.content.orEmpty().mapIndexed { itemIndex, item ->
                            item.toSearchResult(
                                sectionKey = "${section.name}_$sectionIndex",
                                index = itemIndex
                            )
                        }
                    }
                }
                !response.results.isNullOrEmpty() -> {
                    response.results.mapIndexed { i, item ->
                        item.toSearchResult(sectionKey = "results", index = i)
                    }
                }
                !response.data.isNullOrEmpty() -> {
                    response.data.mapIndexed { i, item ->
                        item.toSearchResult(sectionKey = "data", index = i)
                    }
                }
                else -> emptyList()
            }
            emit(Result.success(results))
        } catch (e: Exception) {
            Log.e("SearchRepositoryImpl","Error: ${e.message}")
            emit(Result.failure(e))
        }
    }
}