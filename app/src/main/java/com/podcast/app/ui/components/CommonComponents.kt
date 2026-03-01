package com.podcast.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.podcast.app.R
import com.podcast.app.domain.model.PodcastItem
import com.podcast.app.ui.theme.PodcastTheme

@Composable
fun PodcastThumbnail(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    isNew: Boolean = false
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius)),
            placeholder = painterResource(R.drawable.ic_podcast_placeholder),
            error = painterResource(R.drawable.ic_podcast_placeholder)
        )
        if (isNew) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isHighlighted) {
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        if (onSeeAllClick != null) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_start),
                contentDescription = "See all",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onSeeAllClick)
            )
        }
    }
}

@Composable
fun PlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_play),
            contentDescription = "Play",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(size * 0.45f)
        )
    }
}

@Composable
fun DurationBadge(
    duration: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_play_small),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = duration,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SquarePodcastCard(
    item: PodcastItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = 130.dp
) {
    Column(
        modifier = modifier
            .width(imageSize)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PodcastThumbnail(
            imageUrl = item.thumbnailUrl,
            contentDescription = item.title,
            isNew = item.isNew,
            modifier = Modifier.size(imageSize)
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        item.publishedAgo?.let {
            DurationBadge(duration = it)
        }
    }
}

@Composable
fun EpisodeListItem(
    item: PodcastItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        PodcastThumbnail(
            imageUrl = item.thumbnailUrl,
            contentDescription = item.title,
            modifier = Modifier.size(72.dp),
            cornerRadius = 8.dp
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item.publishedAgo?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            item.duration?.let { dur ->
                DurationBadge(duration = dur)
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_more_vert),
            contentDescription = "More options",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun QueueEpisodeCard(
    item: PodcastItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PodcastThumbnail(
            imageUrl = item.thumbnailUrl,
            contentDescription = item.title,
            modifier = Modifier.size(80.dp),
            cornerRadius = 8.dp
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            item.publishedAgo?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            PlayButton(onClick = onClick, size = 32.dp)
        }
        AsyncImage(
            model = item.coverUrl ?: item.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(100.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun TeamPickCard(
    item: PodcastItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 160.dp
) {
    Box(
        modifier = modifier
            .width(cardWidth)
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = painterResource(R.drawable.ic_podcast_placeholder),
            error = painterResource(R.drawable.ic_podcast_placeholder)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xCC000000))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            item.episodesCount?.let {
                Text(
                    text = "$it حلقة",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun FilterTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    listState: androidx.compose.foundation.lazy.LazyListState = rememberLazyListState()
) {
    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(tabs) { index, tab ->
            val isSelected = index == selectedIndex
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .testTag("filter_tab_$index")
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = onRetry) {
            Text(stringResource(R.string.try_again))
        }
    }
}

private val sampleItem = PodcastItem(
    id = "1",
    title = "السفر الصامت وضوضاء الإعلانات",
    subtitle = "بودكاست",
    thumbnailUrl = null,
    duration = "٣٠ د",
    publishedAgo = "أمس",
    isNew = true
)

@Preview(name = "Section Header - EN Light", locale = "en")
@Composable
private fun SectionHeaderEnLightPreview() {
    PodcastTheme(darkTheme = false,{
        Surface {
            SectionHeader(title = "Listen Before Everyone", isHighlighted = true)
        }
    })
}

@Preview(name = "Section Header - EN Dark", locale = "en")
@Composable
private fun SectionHeaderEnDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface {
            SectionHeader(title = "Listen Before Everyone", isHighlighted = true)
        }
    })
}

@Preview(name = "Section Header - AR Light", locale = "ar")
@Composable
private fun SectionHeaderArLightPreview() {
    PodcastTheme(darkTheme = false,{
        Surface {
            SectionHeader(title = "اسمع قبل الناس", isHighlighted = true)
        }
    })
}

@Preview(name = "Section Header - AR Dark", locale = "ar")
@Composable
private fun SectionHeaderArDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface {
            SectionHeader(title = "اسمع قبل الناس", isHighlighted = true)
        }
    })
}

@Preview(name = "Thumbnail - EN Light", locale = "en", showBackground = true)
@Composable
private fun ThumbnailEnLightPreview() {
    PodcastTheme(darkTheme = false,{
        Surface { PodcastThumbnail(imageUrl = null, contentDescription = "Podcast", modifier = Modifier.size(80.dp), isNew = true) }
    })
}
@Preview(name = "Thumbnail - EN Dark", locale = "en", showBackground = true)
@Composable
private fun ThumbnailEnDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface { PodcastThumbnail(imageUrl = null, contentDescription = "Podcast", modifier = Modifier.size(80.dp), isNew = true) }
    })
}
@Preview(name = "Thumbnail - AR Light", locale = "ar", showBackground = true)
@Composable
private fun ThumbnailArLightPreview() {
    PodcastTheme(darkTheme = false,{
        Surface { PodcastThumbnail(imageUrl = null, contentDescription = "بودكاست", modifier = Modifier.size(80.dp), isNew = false) }
    })
}
@Preview(name = "Thumbnail - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun ThumbnailArDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface { PodcastThumbnail(imageUrl = null, contentDescription = "بودكاست", modifier = Modifier.size(80.dp), isNew = false) }
    })
}

@Preview(name = "Play Button - EN Light", locale = "en", showBackground = true)
@Composable
private fun PlayButtonEnLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { Box(Modifier.padding(16.dp)) { PlayButton(onClick = {}) } } })
}
@Preview(name = "Play Button - EN Dark", locale = "en", showBackground = true)
@Composable
private fun PlayButtonEnDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { Box(Modifier.padding(16.dp)) { PlayButton(onClick = {}) } } })
}
@Preview(name = "Play Button - AR Light", locale = "ar", showBackground = true)
@Composable
private fun PlayButtonArLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { Box(Modifier.padding(16.dp)) { PlayButton(onClick = {}) } } })
}
@Preview(name = "Play Button - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun PlayButtonArDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { Box(Modifier.padding(16.dp)) { PlayButton(onClick = {}) } } })
}

@Preview(name = "Duration Badge - EN Light", locale = "en", showBackground = true)
@Composable
private fun DurationBadgeEnLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { Box(Modifier.padding(16.dp)) { DurationBadge(duration = "30 min") } } })
}
@Preview(name = "Duration Badge - EN Dark", locale = "en", showBackground = true)
@Composable
private fun DurationBadgeEnDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { Box(Modifier.padding(16.dp)) { DurationBadge(duration = "30 min") } } })
}
@Preview(name = "Duration Badge - AR Light", locale = "ar", showBackground = true)
@Composable
private fun DurationBadgeArLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { Box(Modifier.padding(16.dp)) { DurationBadge(duration = "٣٠ د") } } })
}
@Preview(name = "Duration Badge - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun DurationBadgeArDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { Box(Modifier.padding(16.dp)) { DurationBadge(duration = "٣٠ د") } } })
}

@Preview(name = "Square Card - EN Light", locale = "en", showBackground = true)
@Composable
private fun SquareCardEnLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { SquarePodcastCard(item = sampleItem, onClick = {}) } })
}
@Preview(name = "Square Card - EN Dark", locale = "en", showBackground = true)
@Composable
private fun SquareCardEnDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { SquarePodcastCard(item = sampleItem, onClick = {}) } })
}
@Preview(name = "Square Card - AR Light", locale = "ar", showBackground = true)
@Composable
private fun SquareCardArLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { SquarePodcastCard(item = sampleItem.copy(title = "السفر الصامت"), onClick = {}) } })
}
@Preview(name = "Square Card - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun SquareCardArDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { SquarePodcastCard(item = sampleItem.copy(title = "السفر الصامت"), onClick = {}) } })
}

@Preview(name = "Episode List Item - EN Light", locale = "en", showBackground = true)
@Composable
private fun EpisodeListItemEnLightPreview() {
    PodcastTheme(darkTheme = false,{
        Surface { EpisodeListItem(item = sampleItem.copy(title = "State of the World from NPR", publishedAgo = "2d ago", duration = "5m"), onClick = {}) }
    })
}
@Preview(name = "Episode List Item - EN Dark", locale = "en", showBackground = true)
@Composable
private fun EpisodeListItemEnDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface { EpisodeListItem(item = sampleItem.copy(title = "State of the World from NPR", publishedAgo = "2d ago", duration = "5m"), onClick = {}) }
    })
}
@Preview(name = "Episode List Item - AR Light", locale = "ar", showBackground = true)
@Composable
private fun EpisodeListItemArLightPreview() {
    PodcastTheme(darkTheme = false,{
        Surface { EpisodeListItem(item = sampleItem, onClick = {}) }
    })
}
@Preview(name = "Episode List Item - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun EpisodeListItemArDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface { EpisodeListItem(item = sampleItem, onClick = {}) }
    })
}

@Preview(name = "Queue Card - EN Light", locale = "en", showBackground = true)
@Composable
private fun QueueCardEnLightPreview() {
    PodcastTheme(darkTheme = false,{
        Surface { QueueEpisodeCard(item = sampleItem.copy(title = "NPR News Now", publishedAgo = "Today"), onClick = {}) }
    })
}
@Preview(name = "Queue Card - EN Dark", locale = "en", showBackground = true)
@Composable
private fun QueueCardEnDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface { QueueEpisodeCard(item = sampleItem.copy(title = "NPR News Now", publishedAgo = "Today"), onClick = {}) }
    })
}
@Preview(name = "Queue Card - AR Light", locale = "ar", showBackground = true)
@Composable
private fun QueueCardArLightPreview() {
    PodcastTheme(darkTheme = true,{
        Surface { QueueEpisodeCard(item = sampleItem, onClick = {}) }
    })
}
@Preview(name = "Queue Card - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun QueueCardArDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface { QueueEpisodeCard(item = sampleItem, onClick = {}) }
    })
}

@Preview(name = "Team Pick Card - EN Light", locale = "en", showBackground = true)
@Composable
private fun TeamPickCardEnLightPreview() {
    PodcastTheme(darkTheme = false,{
        Surface { TeamPickCard(item = sampleItem.copy(title = "The NPR Politics Podcast", episodesCount = 150), onClick = {}) }
    })
}
@Preview(name = "Team Pick Card - EN Dark", locale = "en", showBackground = true)
@Composable
private fun TeamPickCardEnDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface { TeamPickCard(item = sampleItem.copy(title = "The NPR Politics Podcast", episodesCount = 150), onClick = {}) }
    })
}
@Preview(name = "Team Pick Card - AR Light", locale = "ar", showBackground = true)
@Composable
private fun TeamPickCardArLightPreview() {
    PodcastTheme(darkTheme = false,{
        Surface { TeamPickCard(item = sampleItem.copy(episodesCount = 150), onClick = {}) }
    })
}
@Preview(name = "Team Pick Card - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun TeamPickCardArDarkPreview() {
    PodcastTheme(darkTheme = true,{
        Surface {TeamPickCard(item = sampleItem.copy(episodesCount = 150), onClick = {}) }
    })
}

private val previewTabs = listOf("Top Podcasts", "Episodes", "Audiobooks", "Articles")
private val previewTabsAr = listOf("أفضل البودكاست", "الحلقات", "الكتب الصوتية", "المقالات")

@Preview(name = "Filter Tab Row - EN Light", locale = "en", showBackground = true)
@Composable
private fun FilterTabRowEnLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { FilterTabRow(tabs = previewTabs, selectedIndex = 0, onTabSelected = {}) } })
}
@Preview(name = "Filter Tab Row - EN Dark", locale = "en", showBackground = true)
@Composable
private fun FilterTabRowEnDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { FilterTabRow(tabs = previewTabs, selectedIndex = 1, onTabSelected = {}) } })
}
@Preview(name = "Filter Tab Row - AR Light", locale = "ar", showBackground = true)
@Composable
private fun FilterTabRowArLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { FilterTabRow(tabs = previewTabsAr, selectedIndex = 0, onTabSelected = {}) } })
}
@Preview(name = "Filter Tab Row - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun FilterTabRowArDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { FilterTabRow(tabs = previewTabsAr, selectedIndex = 2, onTabSelected = {}) } })
}

@Preview(name = "Shimmer Box - EN Light", locale = "en", showBackground = true)
@Composable
private fun ShimmerBoxEnLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { ShimmerBox(modifier = Modifier.size(200.dp, 80.dp)) } })
}
@Preview(name = "Shimmer Box - EN Dark", locale = "en", showBackground = true)
@Composable
private fun ShimmerBoxEnDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { ShimmerBox(modifier = Modifier.size(200.dp, 80.dp)) } })
}
@Preview(name = "Shimmer Box - AR Light", locale = "ar", showBackground = true)
@Composable
private fun ShimmerBoxArLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { ShimmerBox(modifier = Modifier.size(200.dp, 80.dp)) } })
}
@Preview(name = "Shimmer Box - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun ShimmerBoxArDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { ShimmerBox(modifier = Modifier.size(200.dp, 80.dp)) } })
}

@Preview(name = "Error State - EN Light", locale = "en", showBackground = true)
@Composable
private fun ErrorStateEnLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { ErrorState(message = "Could not load content. Please try again.", onRetry = {}) } })
}
@Preview(name = "Error State - EN Dark", locale = "en", showBackground = true)
@Composable
private fun ErrorStateEnDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { ErrorState(message = "Could not load content. Please try again.", onRetry = {}) } })
}
@Preview(name = "Error State - AR Light", locale = "ar", showBackground = true)
@Composable
private fun ErrorStateArLightPreview() {
    PodcastTheme(darkTheme = false,{ Surface { ErrorState(message = "تعذّر تحميل المحتوى. يرجى المحاولة مرة أخرى.", onRetry = {}) } })
}
@Preview(name = "Error State - AR Dark", locale = "ar", showBackground = true)
@Composable
private fun ErrorStateArDarkPreview() {
    PodcastTheme(darkTheme = true,{ Surface { ErrorState(message = "تعذّر تحميل المحتوى. يرجى المحاولة مرة أخرى.", onRetry = {}) } })
}