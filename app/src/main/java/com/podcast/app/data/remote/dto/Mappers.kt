package com.podcast.app.data.remote.dto

import com.podcast.app.domain.model.HomeSection
import com.podcast.app.domain.model.PodcastItem
import com.podcast.app.domain.model.SearchResult
import com.podcast.app.domain.model.SearchResultType
import com.podcast.app.domain.model.SectionType
import java.time.Instant
import java.util.UUID


fun SectionDto.toDomain(): HomeSection {
    val sectionKey = "${name}_${order}"
    val items = content
        ?.mapIndexed { index, item -> item.toDomain(contentType, sectionKey, index) }
        ?: emptyList()
    return HomeSection(
        id = sectionKey,
        title = name ?: "",
        type = type.toSectionType(contentType),
        items = items
    )
}

fun String?.toSectionType(contentType: String?): SectionType = when {
    this == null -> SectionType.NEW_EPISODES
    equals("queue", ignoreCase = true) -> SectionType.QUEUE
    equals("big_square", ignoreCase = true) || equals("big square", ignoreCase = true) ->
        if (contentType == "episode") SectionType.SPECIAL_EPISODES else SectionType.TEAM_PICKS
    equals("2_lines_grid", ignoreCase = true) -> SectionType.NEW_EPISODES
    equals("square", ignoreCase = true) ->
        if (contentType == "audio_article") SectionType.FROM_STUDIO else SectionType.LISTEN_BEFORE
    else -> SectionType.NEW_EPISODES
}


fun ContentItemDto.toDomain(
    contentType: String?,
    sectionKey: String,
    index: Int
): PodcastItem {
    val rawId = podcastId ?: episodeId ?: audiobookId ?: articleId ?: UUID.randomUUID().toString()
    val uniqueId = "${sectionKey}_${rawId}_$index"

    return PodcastItem(
        id = uniqueId,
        title = name ?: "",
        subtitle = when (contentType) {
            "episode", "audio_book", "audio_article" -> authorName ?: podcastName
            else -> null
        },
        description = description?.stripHtml(),
        thumbnailUrl = avatarUrl,
        coverUrl = null,
        duration = durationSeconds?.formatDuration(),
        publishedAgo = releaseDate?.toRelativeDate(),
        podcastName = podcastName,
        episodesCount = episodeCountInt,
        isNew = false,
        progress = 0f
    )
}


fun ContentItemDto.toSearchResult(sectionKey: String, index: Int): SearchResult {
    val rawId = podcastId ?: episodeId ?: audiobookId ?: articleId ?: UUID.randomUUID().toString()
    return SearchResult(
        id = "${sectionKey}_${rawId}_$index",
        title = name ?: "",
        description = description?.stripHtml(),
        thumbnailUrl = avatarUrl,
        type = if (episodeId != null) SearchResultType.EPISODE else SearchResultType.PODCAST,
        episodesCount = episodeCountInt
    )
}


fun Int.formatDuration(): String {
    val totalMinutes = this / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${totalMinutes}m"
    }
}

fun String.toRelativeDate(): String = try {
    val instant = Instant.parse(this)
    val diffSeconds = Instant.now().epochSecond - instant.epochSecond
    val diffDays = diffSeconds / 86400
    when {
        diffDays > 365 -> "${diffDays / 365}y ago"
        diffDays > 30  -> "${diffDays / 30}mo ago"
        diffDays > 1   -> "${diffDays}d ago"
        diffDays == 1L -> "Yesterday"
        diffSeconds / 3600 > 1 -> "${diffSeconds / 3600}h ago"
        diffSeconds / 60 > 1   -> "${diffSeconds / 60}m ago"
        else -> "Just now"
    }
} catch (e: Exception) {
    this.take(10)
}

fun String.stripHtml(): String =
    replace(Regex("<[^>]+>"), "").replace("&amp;", "&").trim()