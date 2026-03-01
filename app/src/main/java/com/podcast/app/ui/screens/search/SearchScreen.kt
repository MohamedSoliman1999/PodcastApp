package com.podcast.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podcast.app.R
import com.podcast.app.domain.model.SearchResult
import com.podcast.app.domain.model.SearchResultType
import com.podcast.app.ui.components.ErrorState
import com.podcast.app.ui.components.PodcastThumbnail
import com.podcast.app.ui.components.ShimmerBox
import com.podcast.app.ui.theme.PodcastTheme

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchContent(
        uiState       = uiState,
        onQueryChange = viewModel::onQueryChange,
        onClear       = viewModel::clearSearch
    )
}

@Composable
fun SearchContent(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SearchBar(query = uiState.query, onQueryChange = onQueryChange, onClear = onClear)

        when {
            uiState.isIdle               -> SearchIdleState()
            uiState.isSearching          -> SearchLoadingState()
            uiState.errorMessage != null -> ErrorState(
                message  = uiState.errorMessage,
                onRetry  = { onQueryChange(uiState.query) },
                modifier = Modifier.fillMaxSize()
            )
            uiState.results.isEmpty()    -> SearchEmptyState(query = uiState.query)
            else                         -> SearchResultsList(results = uiState.results)
        }
    }
}

@Composable
internal fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value         = query,
            onValueChange = onQueryChange,
            modifier      = Modifier.weight(1f),
            placeholder   = {
                Text(
                    text  = stringResource(R.string.search_hint),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    painter            = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            painter            = painterResource(R.drawable.ic_close),
                            contentDescription = "Clear",
                            tint               = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine  = true,
            shape       = RoundedCornerShape(24.dp),
            colors      = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
internal fun SearchResultsList(results: List<SearchResult>) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(results, key = { it.id }) { result ->
            SearchResultItem(result = result, onClick = {})
            HorizontalDivider(
                modifier = Modifier.padding(start = 80.dp, end = 16.dp),
                color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
internal fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        PodcastThumbnail(
            imageUrl           = result.thumbnailUrl,
            contentDescription = result.title,
            modifier           = Modifier.size(56.dp),
            cornerRadius       = if (result.type == SearchResultType.CREATOR) 28.dp else 8.dp
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = result.title,
                style    = MaterialTheme.typography.titleMedium,
                color    = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            result.description?.let {
                Text(
                    text     = it,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            result.episodesCount?.let {
                Text(
                    text  = "$it حلقة",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            painter            = painterResource(R.drawable.ic_chevron_start),
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(16.dp)
        )
    }
}

@Composable
internal fun SearchIdleState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter            = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(64.dp)
            )
            Text(
                text  = stringResource(R.string.search_idle_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun SearchLoadingState() {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(6) {
            Row(
                modifier              = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerBox(modifier = Modifier.size(56.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShimmerBox(modifier = Modifier
                        .width(180.dp)
                        .height(14.dp))
                    ShimmerBox(modifier = Modifier
                        .width(120.dp)
                        .height(11.dp))
                }
            }
        }
    }
}

@Composable
internal fun SearchEmptyState(query: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text  = stringResource(R.string.search_no_results, query),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val sampleResultsEn = listOf(
    SearchResult("1", "State of the World",     "NPR News",       null, SearchResultType.PODCAST,  312),
    SearchResult("2", "The Daily",              "New York Times", null, SearchResultType.EPISODE,  null),
    SearchResult("3", "Ira Glass",              "This American Life", null, SearchResultType.CREATOR, null),
    SearchResult("4", "Hidden Brain",           "NPR",            null, SearchResultType.PODCAST,  200)
)

private val sampleResultsAr = listOf(
    SearchResult("1", "جادي",                  "بودكاست شهير",   null, SearchResultType.PODCAST,  150),
    SearchResult("2", "السفر الصامت",          "حلقة جديدة",     null, SearchResultType.EPISODE,  null),
    SearchResult("3", "محمد العوضي",           null,             null, SearchResultType.CREATOR,  null),
    SearchResult("4", "ضوضاء الإعلانات",       "بودكاست أسبوعي", null, SearchResultType.PODCAST,   80)
)

private val idleStateEn    = SearchUiState(isIdle = true,  query = "")
private val idleStateAr    = SearchUiState(isIdle = true,  query = "")
private val loadingStateEn = SearchUiState(isIdle = false, isSearching = true, query = "NPR")
private val loadingStateAr = SearchUiState(isIdle = false, isSearching = true, query = "جادي")
private val resultsStateEn = SearchUiState(isIdle = false, query = "NPR",   results = sampleResultsEn)
private val resultsStateAr = SearchUiState(isIdle = false, query = "جادي", results = sampleResultsAr)
private val emptyStateEn   = SearchUiState(isIdle = false, query = "xyz",   results = emptyList())
private val emptyStateAr   = SearchUiState(isIdle = false, query = "ءءء",   results = emptyList())
private val errorStateEn   = SearchUiState(isIdle = false, query = "NPR",   errorMessage = "No internet connection")
private val errorStateAr   = SearchUiState(isIdle = false, query = "جادي",  errorMessage = "لا يوجد اتصال بالإنترنت")

@Preview(name = "SearchContent – EN Light | Idle", locale = "en", showBackground = true)
@Composable private fun SearchContentEnLightIdlePreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = idleStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – EN Dark | Idle", locale = "en", showBackground = true)
@Composable private fun SearchContentEnDarkIdlePreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = idleStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Light | Idle", locale = "ar", showBackground = true)
@Composable private fun SearchContentArLightIdlePreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = idleStateAr, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Dark | Idle", locale = "ar", showBackground = true)
@Composable private fun SearchContentArDarkIdlePreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = idleStateAr, onQueryChange = {}, onClear = {}) }
}

@Preview(name = "SearchContent – EN Light | Loading", locale = "en", showBackground = true)
@Composable private fun SearchContentEnLightLoadingPreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = loadingStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – EN Dark | Loading", locale = "en", showBackground = true)
@Composable private fun SearchContentEnDarkLoadingPreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = loadingStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Light | Loading", locale = "ar", showBackground = true)
@Composable private fun SearchContentArLightLoadingPreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = loadingStateAr, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Dark | Loading", locale = "ar", showBackground = true)
@Composable private fun SearchContentArDarkLoadingPreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = loadingStateAr, onQueryChange = {}, onClear = {}) }
}

@Preview(name = "SearchContent – EN Light | Results", locale = "en", showBackground = true)
@Composable private fun SearchContentEnLightResultsPreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = resultsStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – EN Dark | Results", locale = "en", showBackground = true)
@Composable private fun SearchContentEnDarkResultsPreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = resultsStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Light | Results", locale = "ar", showBackground = true)
@Composable private fun SearchContentArLightResultsPreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = resultsStateAr, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Dark | Results", locale = "ar", showBackground = true)
@Composable private fun SearchContentArDarkResultsPreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = resultsStateAr, onQueryChange = {}, onClear = {}) }
}

@Preview(name = "SearchContent – EN Light | Empty", locale = "en", showBackground = true)
@Composable private fun SearchContentEnLightEmptyPreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = emptyStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – EN Dark | Empty", locale = "en", showBackground = true)
@Composable private fun SearchContentEnDarkEmptyPreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = emptyStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Light | Empty", locale = "ar", showBackground = true)
@Composable private fun SearchContentArLightEmptyPreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = emptyStateAr, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Dark | Empty", locale = "ar", showBackground = true)
@Composable private fun SearchContentArDarkEmptyPreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = emptyStateAr, onQueryChange = {}, onClear = {}) }
}

@Preview(name = "SearchContent – EN Light | Error", locale = "en", showBackground = true)
@Composable private fun SearchContentEnLightErrorPreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = errorStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – EN Dark | Error", locale = "en", showBackground = true)
@Composable private fun SearchContentEnDarkErrorPreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = errorStateEn, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Light | Error", locale = "ar", showBackground = true)
@Composable private fun SearchContentArLightErrorPreview() {
    PodcastTheme(darkTheme = false) { SearchContent(uiState = errorStateAr, onQueryChange = {}, onClear = {}) }
}
@Preview(name = "SearchContent – AR Dark | Error", locale = "ar", showBackground = true)
@Composable private fun SearchContentArDarkErrorPreview() {
    PodcastTheme(darkTheme = true) { SearchContent(uiState = errorStateAr, onQueryChange = {}, onClear = {}) }
}

@Preview(name = "SearchBar – EN Light | Empty", locale = "en", showBackground = true)
@Composable private fun SearchBarEnLightEmptyPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchBar(query = "", onQueryChange = {}, onClear = {}) } }
}
@Preview(name = "SearchBar – EN Dark | Empty", locale = "en", showBackground = true)
@Composable private fun SearchBarEnDarkEmptyPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchBar(query = "", onQueryChange = {}, onClear = {}) } }
}
@Preview(name = "SearchBar – AR Light | Empty", locale = "ar", showBackground = true)
@Composable private fun SearchBarArLightEmptyPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchBar(query = "", onQueryChange = {}, onClear = {}) } }
}
@Preview(name = "SearchBar – AR Dark | Empty", locale = "ar", showBackground = true)
@Composable private fun SearchBarArDarkEmptyPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchBar(query = "", onQueryChange = {}, onClear = {}) } }
}

@Preview(name = "SearchBar – EN Light | With text", locale = "en", showBackground = true)
@Composable private fun SearchBarEnLightFilledPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchBar(query = "NPR News", onQueryChange = {}, onClear = {}) } }
}
@Preview(name = "SearchBar – EN Dark | With text", locale = "en", showBackground = true)
@Composable private fun SearchBarEnDarkFilledPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchBar(query = "NPR News", onQueryChange = {}, onClear = {}) } }
}
@Preview(name = "SearchBar – AR Light | With text", locale = "ar", showBackground = true)
@Composable private fun SearchBarArLightFilledPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchBar(query = "جادي", onQueryChange = {}, onClear = {}) } }
}
@Preview(name = "SearchBar – AR Dark | With text", locale = "ar", showBackground = true)
@Composable private fun SearchBarArDarkFilledPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchBar(query = "جادي", onQueryChange = {}, onClear = {}) } }
}

@Preview(name = "SearchResultItem – EN Light | Podcast", locale = "en", showBackground = true)
@Composable private fun SearchResultItemEnLightPodcastPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchResultItem(result = sampleResultsEn[0], onClick = {}) } }
}
@Preview(name = "SearchResultItem – EN Dark | Podcast", locale = "en", showBackground = true)
@Composable private fun SearchResultItemEnDarkPodcastPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchResultItem(result = sampleResultsEn[0], onClick = {}) } }
}
@Preview(name = "SearchResultItem – AR Light | Podcast", locale = "ar", showBackground = true)
@Composable private fun SearchResultItemArLightPodcastPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchResultItem(result = sampleResultsAr[0], onClick = {}) } }
}
@Preview(name = "SearchResultItem – AR Dark | Podcast", locale = "ar", showBackground = true)
@Composable private fun SearchResultItemArDarkPodcastPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchResultItem(result = sampleResultsAr[0], onClick = {}) } }
}

@Preview(name = "SearchResultItem – EN Light | Creator", locale = "en", showBackground = true)
@Composable private fun SearchResultItemEnLightCreatorPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchResultItem(result = sampleResultsEn[0], onClick = {}) } }
}
@Preview(name = "SearchResultItem – EN Dark | Creator", locale = "en", showBackground = true)
@Composable private fun SearchResultItemEnDarkCreatorPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchResultItem(result = sampleResultsEn[0], onClick = {}) } }
}
@Preview(name = "SearchResultItem – AR Light | Creator", locale = "ar", showBackground = true)
@Composable private fun SearchResultItemArLightCreatorPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchResultItem(result = sampleResultsAr[0], onClick = {}) } }
}
@Preview(name = "SearchResultItem – AR Dark | Creator", locale = "ar", showBackground = true)
@Composable private fun SearchResultItemArDarkCreatorPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchResultItem(result = sampleResultsAr[0], onClick = {}) } }
}

@Preview(name = "SearchResultsList – EN Light", locale = "en", showBackground = true)
@Composable private fun SearchResultsListEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchResultsList(results = sampleResultsEn) } }
}
@Preview(name = "SearchResultsList – EN Dark", locale = "en", showBackground = true)
@Composable private fun SearchResultsListEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchResultsList(results = sampleResultsEn) } }
}
@Preview(name = "SearchResultsList – AR Light", locale = "ar", showBackground = true)
@Composable private fun SearchResultsListArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchResultsList(results = sampleResultsAr) } }
}
@Preview(name = "SearchResultsList – AR Dark", locale = "ar", showBackground = true)
@Composable private fun SearchResultsListArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchResultsList(results = sampleResultsAr) } }
}

@Preview(name = "SearchIdleState – EN Light", locale = "en", showBackground = true)
@Composable private fun SearchIdleEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface(Modifier.fillMaxSize()) { SearchIdleState() } }
}
@Preview(name = "SearchIdleState – EN Dark", locale = "en", showBackground = true)
@Composable private fun SearchIdleEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface(Modifier.fillMaxSize()) { SearchIdleState() } }
}
@Preview(name = "SearchIdleState – AR Light", locale = "ar", showBackground = true)
@Composable private fun SearchIdleArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface(Modifier.fillMaxSize()) { SearchIdleState() } }
}
@Preview(name = "SearchIdleState – AR Dark", locale = "ar", showBackground = true)
@Composable private fun SearchIdleArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface(Modifier.fillMaxSize()) { SearchIdleState() } }
}

@Preview(name = "SearchLoadingState – EN Light", locale = "en", showBackground = true)
@Composable private fun SearchLoadingEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchLoadingState() } }
}
@Preview(name = "SearchLoadingState – EN Dark", locale = "en", showBackground = true)
@Composable private fun SearchLoadingEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchLoadingState() } }
}
@Preview(name = "SearchLoadingState – AR Light", locale = "ar", showBackground = true)
@Composable private fun SearchLoadingArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface { SearchLoadingState() } }
}
@Preview(name = "SearchLoadingState – AR Dark", locale = "ar", showBackground = true)
@Composable private fun SearchLoadingArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface { SearchLoadingState() } }
}

@Preview(name = "SearchEmptyState – EN Light", locale = "en", showBackground = true)
@Composable private fun SearchEmptyEnLightPreview() {
    PodcastTheme(darkTheme = false) { Surface(Modifier.fillMaxSize()) { SearchEmptyState(query = "xyz") } }
}
@Preview(name = "SearchEmptyState – EN Dark", locale = "en", showBackground = true)
@Composable private fun SearchEmptyEnDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface(Modifier.fillMaxSize()) { SearchEmptyState(query = "xyz") } }
}
@Preview(name = "SearchEmptyState – AR Light", locale = "ar", showBackground = true)
@Composable private fun SearchEmptyArLightPreview() {
    PodcastTheme(darkTheme = false) { Surface(Modifier.fillMaxSize()) { SearchEmptyState(query = "ءءء") } }
}
@Preview(name = "SearchEmptyState – AR Dark", locale = "ar", showBackground = true)
@Composable private fun SearchEmptyArDarkPreview() {
    PodcastTheme(darkTheme = true) { Surface(Modifier.fillMaxSize()) { SearchEmptyState(query = "ءءء") } }
}