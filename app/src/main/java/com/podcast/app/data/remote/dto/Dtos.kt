package com.podcast.app.data.remote.dto

import com.google.gson.annotations.SerializedName


data class HomeSectionsResponse(
    @SerializedName("sections") val sections: List<SectionDto>? = null
)

data class SectionDto(
    @SerializedName("name")         val name: String? = null,
    @SerializedName("type")         val type: String? = null,
    @SerializedName("content_type") val contentType: String? = null,
    @SerializedName("order")        val orderRaw: Any? = null,
    @SerializedName("content")      val content: List<ContentItemDto>? = null
) {
    val order: Int? get() = when (orderRaw) {
        is Double -> orderRaw.toInt()
        is Int    -> orderRaw
        is String -> orderRaw.toIntOrNull()
        else      -> null
    }
}

data class ContentItemDto(
    @SerializedName("podcast_id")    val podcastId: String? = null,
    @SerializedName("episode_id")    val episodeId: String? = null,
    @SerializedName("audiobook_id")  val audiobookId: String? = null,
    @SerializedName("article_id")    val articleId: String? = null,

    @SerializedName("name")          val name: String? = null,
    @SerializedName("description")   val description: String? = null,
    @SerializedName("author_name")   val authorName: String? = null,
    @SerializedName("avatar_url")    val avatarUrl: String? = null,
    @SerializedName("duration")        val durationRaw: String? = null,
    @SerializedName("episode_count")   val episodeCountRaw: String? = null,
    @SerializedName("priority")        val priorityRaw: String? = null,
    @SerializedName("popularityScore") val popularityScoreRaw: String? = null,
    @SerializedName("score")           val scoreRaw: String? = null,

    @SerializedName("language")      val language: String? = null,
    @SerializedName("podcast_name")  val podcastName: String? = null,
    @SerializedName("release_date")  val releaseDate: String? = null,
    @SerializedName("episode_type")  val episodeType: String? = null
) {
    val durationSeconds: Int? get() = durationRaw?.toDoubleOrNull()?.toInt()
    val episodeCountInt: Int? get() = episodeCountRaw?.toDoubleOrNull()?.toInt()
}


data class SearchResponse(
    @SerializedName("sections") val sections: List<SectionDto>? = null,
    @SerializedName("results")  val results: List<ContentItemDto>? = null,
    @SerializedName("data")     val data: List<ContentItemDto>? = null
)