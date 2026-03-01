package com.podcast.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podcast.app.R
import com.podcast.app.domain.model.HomeSection
import com.podcast.app.domain.model.PodcastItem
import com.podcast.app.domain.model.SectionType
import com.podcast.app.ui.components.*
import com.podcast.app.ui.theme.PodcastTheme

// ─── Screen entry point ───────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.ScrollToSection ->
                    listState.animateScrollToItem(event.listIndex)
            }
        }
    }

    LaunchedEffect(listState, uiState.sections.size) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.isScrollInProgress }
            .collect { (firstVisible, isScrolling) ->
                if (isScrolling) viewModel.onScrolledToSection(firstVisible)
            }
    }

    HomeContent(
        uiState = uiState,
        listState = listState,
        onTabSelected = viewModel::onTabSelected,
        onRetry = viewModel::loadHome,
        onSettingsClick = onSettingsClick
    )
}

// ─── HomeContent ──────────────────────────────────────────────────────────────

@Composable
fun HomeContent(
    uiState: HomeUiState,
    listState: LazyListState = rememberLazyListState(),
    onTabSelected: (Int) -> Unit,
    onRetry: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HomeTopBar(onSettingsClick = onSettingsClick)

        val tabTitles = uiState.sections.map { it.title }
        if (tabTitles.isNotEmpty()) {
            HomeFilterTabs(
                tabs = tabTitles,
                selectedIndex = uiState.selectedTabIndex,
                onTabSelected = onTabSelected
            )
        }

        when {
            uiState.isLoading -> HomeLoadingState()
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize()
            )
            uiState.sections.isEmpty() -> HomeMockContent()
            else -> HomeSectionsList(
                sections = uiState.sections,
                listState = listState
            )
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(onSettingsClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = stringResource(R.string.greeting),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Icon(
            painter = painterResource(R.drawable.ic_settings),
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onSettingsClick)
        )
    }
}

// ─── Filter Tabs ──────────────────────────────────────────────────────────────

@Composable
private fun HomeFilterTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabsListState = rememberLazyListState()
    LaunchedEffect(selectedIndex) {
        tabsListState.animateScrollToItem(selectedIndex)
    }
    FilterTabRow(
        tabs = tabs,
        selectedIndex = selectedIndex,
        onTabSelected = onTabSelected,
        listState = tabsListState
    )
}

// ─── Sections List ────────────────────────────────────────────────────────────

@Composable
private fun HomeSectionsList(
    sections: List<HomeSection>,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
    ) {
        items(sections, key = { it.id }) { section ->
            HomeSectionItem(section = section)
        }
    }
}

@Composable
private fun HomeSectionItem(section: HomeSection) {
    when (section.type) {
        SectionType.QUEUE -> QueueSection(section)
        SectionType.LISTEN_BEFORE -> ListenBeforeSection(section)
        SectionType.NEW_EPISODES -> NewEpisodesSection(section)
        SectionType.TEAM_PICKS -> TeamPicksSection(section)
        SectionType.FROM_STUDIO -> FromStudioSection(section)
        SectionType.SPECIAL_EPISODES -> SpecialEpisodesSection(section)
    }
}

// ─── Queue Section ────────────────────────────────────────────────────────────

@Composable
private fun QueueSection(section: HomeSection) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.queue_meta, section.items.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        section.items.firstOrNull()?.let { item ->
            QueueEpisodeCard(
                item = item,
                onClick = {},
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// ─── Listen Before Section ────────────────────────────────────────────────────

@Composable
private fun ListenBeforeSection(section: HomeSection) {
    Column {
        SectionHeader(title = section.title, isHighlighted = true, onSeeAllClick = {})
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(section.items, key = { it.id }) { item ->
                SquarePodcastCard(item = item, onClick = {})
            }
        }
    }
}

// ─── New Episodes Section ─────────────────────────────────────────────────────

@Composable
private fun NewEpisodesSection(section: HomeSection) {
    Column {
        SectionHeader(title = section.title, onSeeAllClick = {})
        section.items.take(3).forEach { item ->
            EpisodeListItem(item = item, onClick = {})
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        }
    }
}

// ─── Team Picks Section ───────────────────────────────────────────────────────

@Composable
private fun TeamPicksSection(section: HomeSection) {
    Column {
        SectionHeader(title = section.title, onSeeAllClick = {})
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(section.items, key = { it.id }) { item ->
                TeamPickCard(item = item, onClick = {})
            }
        }
    }
}

// ─── From Studio Section ──────────────────────────────────────────────────────

@Composable
private fun FromStudioSection(section: HomeSection) {
    Column {
        SectionHeader(title = section.title, onSeeAllClick = {})
        section.items.firstOrNull()?.let { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    PodcastThumbnail(
                        imageUrl = item.thumbnailUrl,
                        contentDescription = item.title,
                        modifier = Modifier.size(56.dp),
                        cornerRadius = 8.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        item.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        item.publishedAgo?.let { ago ->
                            Text(
                                text = ago,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Special Episodes Section ─────────────────────────────────────────────────

@Composable
private fun SpecialEpisodesSection(section: HomeSection) {
    Column {
        SectionHeader(title = section.title, isHighlighted = true, onSeeAllClick = {})
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(section.items, key = { it.id }) { item ->
                SquarePodcastCard(item = item, onClick = {}, imageSize = 140.dp)
            }
        }
    }
}

// ─── Loading / Empty states ───────────────────────────────────────────────────

@Composable
private fun HomeLoadingState() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ShimmerBox(modifier = Modifier.fillMaxWidth().height(120.dp)) }
        items(4) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerBox(modifier = Modifier.width(120.dp).height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(4) { ShimmerBox(modifier = Modifier.size(120.dp)) }
                }
            }
        }
    }
}

@Composable
private fun HomeMockContent() = HomeLoadingState()

// ═════════════════════════════════════════════════════════════════════════════
// Preview data
// ═════════════════════════════════════════════════════════════════════════════

private val prevItemEn = PodcastItem(
    id = "1", title = "State of the World from NPR",
    thumbnailUrl = null, duration = "5m", publishedAgo = "2d ago",
    episodesCount = 805, isNew = true
)
private val prevItemAr = PodcastItem(
    id = "2", title = "السفر الصامت وضوضاء الإعلانات",
    thumbnailUrl = null, duration = "٣٠ د", publishedAgo = "أمس",
    episodesCount = 120, isNew = false,
    description = "وصف قصير للحلقة يظهر تحت العنوان في بطاقة المحتوى"
)
private val prevItems = listOf(prevItemEn, prevItemEn.copy(id = "3"), prevItemEn.copy(id = "4"))
private val prevItemsAr = listOf(prevItemAr, prevItemAr.copy(id = "5"), prevItemAr.copy(id = "6"))

private val queueSectionEn = HomeSection("q1", "Queue", SectionType.QUEUE, prevItems)
private val listenSectionEn = HomeSection("l1", "Listen Before", SectionType.LISTEN_BEFORE, prevItems)
private val newEpSectionEn = HomeSection("n1", "New Episodes", SectionType.NEW_EPISODES, prevItems)
private val teamSectionEn = HomeSection("t1", "Team Picks", SectionType.TEAM_PICKS, prevItems)
private val studioSectionEn = HomeSection("s1", "From the Studio", SectionType.FROM_STUDIO, prevItems)
private val specialSectionEn = HomeSection("sp1", "Special Episodes", SectionType.SPECIAL_EPISODES, prevItems)

private val queueSectionAr = HomeSection("q2", "الطابور", SectionType.QUEUE, prevItemsAr)
private val listenSectionAr = HomeSection("l2", "اسمع قبل الناس", SectionType.LISTEN_BEFORE, prevItemsAr)
private val newEpSectionAr = HomeSection("n2", "حلقات جديدة", SectionType.NEW_EPISODES, prevItemsAr)
private val teamSectionAr = HomeSection("t2", "اختيارات الفريق", SectionType.TEAM_PICKS, prevItemsAr)
private val studioSectionAr = HomeSection("s2", "من الاستوديو", SectionType.FROM_STUDIO, prevItemsAr)
private val specialSectionAr = HomeSection("sp2", "حلقات خاصة", SectionType.SPECIAL_EPISODES, prevItemsAr)

private val allSectionsEn = listOf(
    queueSectionEn, listenSectionEn, newEpSectionEn,
    teamSectionEn, studioSectionEn, specialSectionEn
)
private val allSectionsAr = listOf(
    queueSectionAr, listenSectionAr, newEpSectionAr,
    teamSectionAr, studioSectionAr, specialSectionAr
)

private val loadingState = HomeUiState(isLoading = true)
private val errorState = HomeUiState(errorMessage = "Could not load. Please retry.")
private val errorStateAr = HomeUiState(errorMessage = "تعذّر التحميل. يرجى المحاولة مجدداً.")
private val loadedStateEn = HomeUiState(sections = allSectionsEn, selectedTabIndex = 0)
private val loadedStateAr = HomeUiState(sections = allSectionsAr, selectedTabIndex = 0)

// ═════════════════════════════════════════════════════════════════════════════
// Previews: HomeContent  (full-screen, all states)
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "HomeContent – EN Light | Loaded", locale = "en", showBackground = true)
@Composable
private fun HomeContentEnLightPreview() {
    PodcastTheme(darkTheme = false) { HomeContent(uiState = loadedStateEn, onTabSelected = {}, onRetry = {}) }
}

@Preview(name = "HomeContent – EN Dark | Loaded", locale = "en", showBackground = true)
@Composable
private fun HomeContentEnDarkPreview() {
    PodcastTheme(darkTheme = true) { HomeContent(uiState = loadedStateEn, onTabSelected = {}, onRetry = {}) }
}

@Preview(name = "HomeContent – AR Light | Loaded", locale = "ar", showBackground = true)
@Composable
private fun HomeContentArLightPreview() {
    PodcastTheme(darkTheme = false) { HomeContent(uiState = loadedStateAr, onTabSelected = {}, onRetry = {}) }
}

@Preview(name = "HomeContent – AR Dark | Loaded", locale = "ar", showBackground = true)
@Composable
private fun HomeContentArDarkPreview() {
    PodcastTheme(darkTheme = true) { HomeContent(uiState = loadedStateAr, onTabSelected = {}, onRetry = {}) }
}

// ─── HomeContent: Loading state ───────────────────────────────────────────────

@Preview(name = "HomeContent – EN Light | Loading", locale = "en", showBackground = true)
@Composable
private fun HomeContentEnLightLoadingPreview() {
    PodcastTheme(darkTheme = false) { HomeContent(uiState = loadingState, onTabSelected = {}, onRetry = {}) }
}

@Preview(name = "HomeContent – EN Dark | Loading", locale = "en", showBackground = true)
@Composable
private fun HomeContentEnDarkLoadingPreview() {
    PodcastTheme(darkTheme = true) { HomeContent(uiState = loadingState, onTabSelected = {}, onRetry = {}) }
}

@Preview(name = "HomeContent – AR Light | Loading", locale = "ar", showBackground = true)
@Composable
private fun HomeContentArLightLoadingPreview() {
    PodcastTheme(darkTheme = false) { HomeContent(uiState = loadingState, onTabSelected = {}, onRetry = {}) }
}

@Preview(name = "HomeContent – AR Dark | Loading", locale = "ar", showBackground = true)
@Composable
private fun HomeContentArDarkLoadingPreview() {
    PodcastTheme(darkTheme = true) { HomeContent(uiState = loadingState, onTabSelected = {}, onRetry = {}) }
}

// ─── HomeContent: Error state ─────────────────────────────────────────────────

@Preview(name = "HomeContent – EN Light | Error", locale = "en", showBackground = true)
@Composable
private fun HomeContentEnLightErrorPreview() {
    PodcastTheme(darkTheme = false) { HomeContent(uiState = errorState, onTabSelected = {}, onRetry = {}) }
}

@Preview(name = "HomeContent – EN Dark | Error", locale = "en", showBackground = true)
@Composable
private fun HomeContentEnDarkErrorPreview() {
    PodcastTheme(darkTheme = true) { HomeContent(uiState = errorState, onTabSelected = {}, onRetry = {}) }
}

@Preview(name = "HomeContent – AR Light | Error", locale = "ar", showBackground = true)
@Composable
private fun HomeContentArLightErrorPreview() {
    PodcastTheme(darkTheme = false) { HomeContent(uiState = errorStateAr, onTabSelected = {}, onRetry = {}) }
}

@Preview(name = "HomeContent – AR Dark | Error", locale = "ar", showBackground = true)
@Composable
private fun HomeContentArDarkErrorPreview() {
    PodcastTheme(darkTheme = true) { HomeContent(uiState = errorStateAr, onTabSelected = {}, onRetry = {}) }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: HomeTopBar
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "HomeTopBar – EN Light", locale = "en", showBackground = true)
@Composable
private fun HomeTopBarEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { HomeTopBar() } }
}

@Preview(name = "HomeTopBar – EN Dark", locale = "en", showBackground = true)
@Composable
private fun HomeTopBarEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { HomeTopBar() } }
}

@Preview(name = "HomeTopBar – AR Light", locale = "ar", showBackground = true)
@Composable
private fun HomeTopBarArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { HomeTopBar() } }
}

@Preview(name = "HomeTopBar – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun HomeTopBarArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { HomeTopBar() } }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: HomeLoadingState
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "HomeLoadingState – EN Light", locale = "en", showBackground = true)
@Composable
private fun HomeLoadingEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { HomeLoadingState() } }
}

@Preview(name = "HomeLoadingState – EN Dark", locale = "en", showBackground = true)
@Composable
private fun HomeLoadingEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { HomeLoadingState() } }
}

@Preview(name = "HomeLoadingState – AR Light", locale = "ar", showBackground = true)
@Composable
private fun HomeLoadingArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { HomeLoadingState() } }
}

@Preview(name = "HomeLoadingState – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun HomeLoadingArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { HomeLoadingState() } }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: QueueSection
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "QueueSection – EN Light", locale = "en", showBackground = true)
@Composable
private fun QueueSectionEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { QueueSection(section = queueSectionEn) } }
}

@Preview(name = "QueueSection – EN Dark", locale = "en", showBackground = true)
@Composable
private fun QueueSectionEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { QueueSection(section = queueSectionEn) } }
}

@Preview(name = "QueueSection – AR Light", locale = "ar", showBackground = true)
@Composable
private fun QueueSectionArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { QueueSection(section = queueSectionAr) } }
}

@Preview(name = "QueueSection – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun QueueSectionArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { QueueSection(section = queueSectionAr) } }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: ListenBeforeSection
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "ListenBeforeSection – EN Light", locale = "en", showBackground = true)
@Composable
private fun ListenBeforeEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { ListenBeforeSection(section = listenSectionEn) } }
}

@Preview(name = "ListenBeforeSection – EN Dark", locale = "en", showBackground = true)
@Composable
private fun ListenBeforeEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { ListenBeforeSection(section = listenSectionEn) } }
}

@Preview(name = "ListenBeforeSection – AR Light", locale = "ar", showBackground = true)
@Composable
private fun ListenBeforeArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { ListenBeforeSection(section = listenSectionAr) } }
}

@Preview(name = "ListenBeforeSection – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun ListenBeforeArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { ListenBeforeSection(section = listenSectionAr) } }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: NewEpisodesSection
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "NewEpisodesSection – EN Light", locale = "en", showBackground = true)
@Composable
private fun NewEpisodesEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { NewEpisodesSection(section = newEpSectionEn) } }
}

@Preview(name = "NewEpisodesSection – EN Dark", locale = "en", showBackground = true)
@Composable
private fun NewEpisodesEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { NewEpisodesSection(section = newEpSectionEn) } }
}

@Preview(name = "NewEpisodesSection – AR Light", locale = "ar", showBackground = true)
@Composable
private fun NewEpisodesArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { NewEpisodesSection(section = newEpSectionAr) } }
}

@Preview(name = "NewEpisodesSection – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun NewEpisodesArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { NewEpisodesSection(section = newEpSectionAr) } }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: TeamPicksSection
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "TeamPicksSection – EN Light", locale = "en", showBackground = true)
@Composable
private fun TeamPicksEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { TeamPicksSection(section = teamSectionEn) } }
}

@Preview(name = "TeamPicksSection – EN Dark", locale = "en", showBackground = true)
@Composable
private fun TeamPicksEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { TeamPicksSection(section = teamSectionEn) } }
}

@Preview(name = "TeamPicksSection – AR Light", locale = "ar", showBackground = true)
@Composable
private fun TeamPicksArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { TeamPicksSection(section = teamSectionAr) } }
}

@Preview(name = "TeamPicksSection – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun TeamPicksArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { TeamPicksSection(section = teamSectionAr) } }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: FromStudioSection
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "FromStudioSection – EN Light", locale = "en", showBackground = true)
@Composable
private fun FromStudioEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { FromStudioSection(section = studioSectionEn) } }
}

@Preview(name = "FromStudioSection – EN Dark", locale = "en", showBackground = true)
@Composable
private fun FromStudioEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { FromStudioSection(section = studioSectionEn) } }
}

@Preview(name = "FromStudioSection – AR Light", locale = "ar", showBackground = true)
@Composable
private fun FromStudioArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { FromStudioSection(section = studioSectionAr) } }
}

@Preview(name = "FromStudioSection – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun FromStudioArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { FromStudioSection(section = studioSectionAr) } }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: SpecialEpisodesSection
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "SpecialEpisodesSection – EN Light", locale = "en", showBackground = true)
@Composable
private fun SpecialEpisodesEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { SpecialEpisodesSection(section = specialSectionEn) } }
}

@Preview(name = "SpecialEpisodesSection – EN Dark", locale = "en", showBackground = true)
@Composable
private fun SpecialEpisodesEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { SpecialEpisodesSection(section = specialSectionEn) } }
}

@Preview(name = "SpecialEpisodesSection – AR Light", locale = "ar", showBackground = true)
@Composable
private fun SpecialEpisodesArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { SpecialEpisodesSection(section = specialSectionAr) } }
}

@Preview(name = "SpecialEpisodesSection – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun SpecialEpisodesArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { SpecialEpisodesSection(section = specialSectionAr) } }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: HomeFilterTabs
// ═════════════════════════════════════════════════════════════════════════════

private val tabsEn = listOf("Queue", "Listen Before", "New Episodes", "Team Picks", "From Studio", "Special")
private val tabsAr = listOf("الطابور", "اسمع قبل الناس", "حلقات جديدة", "اختيارات الفريق", "من الاستوديو", "حلقات خاصة")

@Preview(name = "HomeFilterTabs – EN Light | First selected", locale = "en", showBackground = true)
@Composable
private fun HomeFilterTabsEnLightPreview() {
    PodcastTheme(darkTheme = false) {
        Surface { HomeFilterTabs(tabs = tabsEn, selectedIndex = 0, onTabSelected = {}) }
    }
}

@Preview(name = "HomeFilterTabs – EN Dark | Third selected", locale = "en", showBackground = true)
@Composable
private fun HomeFilterTabsEnDarkPreview() {
    PodcastTheme(darkTheme = true) {
        Surface { HomeFilterTabs(tabs = tabsEn, selectedIndex = 2, onTabSelected = {}) }
    }
}

@Preview(name = "HomeFilterTabs – AR Light | First selected", locale = "ar", showBackground = true)
@Composable
private fun HomeFilterTabsArLightPreview() {
    PodcastTheme(darkTheme = false) {
        Surface { HomeFilterTabs(tabs = tabsAr, selectedIndex = 0, onTabSelected = {}) }
    }
}

@Preview(name = "HomeFilterTabs – AR Dark | Third selected", locale = "ar", showBackground = true)
@Composable
private fun HomeFilterTabsArDarkPreview() {
    PodcastTheme(darkTheme = true) {
        Surface { HomeFilterTabs(tabs = tabsAr, selectedIndex = 2, onTabSelected = {}) }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Previews: HomeSectionsList
// ═════════════════════════════════════════════════════════════════════════════

@Preview(name = "HomeSectionsList – EN Light", locale = "en", showBackground = true)
@Composable
private fun HomeSectionsListEnLightPreview() {
    PodcastTheme(darkTheme = false) {
        Surface { HomeSectionsList(sections = allSectionsEn, listState = rememberLazyListState()) }
    }
}

@Preview(name = "HomeSectionsList – EN Dark", locale = "en", showBackground = true)
@Composable
private fun HomeSectionsListEnDarkPreview() {
    PodcastTheme(darkTheme = true) {
        Surface { HomeSectionsList(sections = allSectionsEn, listState = rememberLazyListState()) }
    }
}

@Preview(name = "HomeSectionsList – AR Light", locale = "ar", showBackground = true)
@Composable
private fun HomeSectionsListArLightPreview() {
    PodcastTheme(darkTheme = false) {
        Surface { HomeSectionsList(sections = allSectionsAr, listState = rememberLazyListState()) }
    }
}

@Preview(name = "HomeSectionsList – AR Dark", locale = "ar", showBackground = true)
@Composable
private fun HomeSectionsListArDarkPreview() {
    PodcastTheme(darkTheme = true) {
        Surface { HomeSectionsList(sections = allSectionsAr, listState = rememberLazyListState()) }
    }
}