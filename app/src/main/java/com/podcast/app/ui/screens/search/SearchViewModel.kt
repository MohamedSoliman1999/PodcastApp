package com.podcast.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcast.app.domain.model.SearchResult
import com.podcast.app.domain.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<SearchResult> = emptyList(),
    val errorMessage: String? = null,
    val isIdle: Boolean = true
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Raw query emissions from the UI
    private val _query = MutableStateFlow("")

    init {
        _query
            .debounce(200L)
            .distinctUntilChanged()
            .filter { it.length >= 2 }
            .flatMapLatest { query ->
                flow {
                    // Mark loading immediately on the new query
                    _uiState.value = _uiState.value.copy(
                        isSearching  = true,
                        errorMessage = null,
                        results      = emptyList()
                    )
                    emitAll(searchUseCase(query))
                }
            }
            .onEach { result ->
                result.fold(
                    onSuccess = { results ->
                        _uiState.value = _uiState.value.copy(
                            isSearching = false,
                            results     = results,
                            isIdle      = false
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isSearching  = false,
                            errorMessage = error.localizedMessage ?: "Search failed"
                        )
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            query  = query,
            isIdle = query.isBlank()
        )
        _query.value = query
    }

    fun clearSearch() {
        _uiState.value = SearchUiState()
        _query.value   = ""
    }
}