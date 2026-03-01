package com.podcast.app.data.remote.api

import com.podcast.app.data.remote.dto.HomeSectionsResponse
import com.podcast.app.data.remote.dto.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("home_sections")
    suspend fun getHomeSections(): HomeSectionsResponse
}

interface SearchApi {
    @GET("search")
    suspend fun search(@Query("q") query: String): SearchResponse
}
