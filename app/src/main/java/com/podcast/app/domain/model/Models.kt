package com.podcast.app.domain.model

data class HomeSection(
    val id: String,
    val title: String,
    val type: SectionType,
    val items: List<PodcastItem>
)

enum class SectionType {
    QUEUE,
    LISTEN_BEFORE,
    NEW_EPISODES,
    TEAM_PICKS,
    FROM_STUDIO,
    SPECIAL_EPISODES
}

data class PodcastItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val coverUrl: String? = null,
    val duration: String? = null,
    val publishedAgo: String? = null,
    val podcastName: String? = null,
    val episodesCount: Int? = null,
    val isNew: Boolean = false,
    val progress: Float = 0f
)

data class SearchResult(
    val id: String,
    val title: String,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val type: SearchResultType,
    val episodesCount: Int? = null
)

enum class SearchResultType {
    PODCAST, EPISODE, CREATOR
}
