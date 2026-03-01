package com.podcast.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcast.app.domain.model.HomeSection
import com.podcast.app.domain.usecase.GetHomeSectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val sections: List<HomeSection> = emptyList(),
    val errorMessage: String? = null,
    val selectedTabIndex: Int = 0
)

sealed class HomeEvent {
    data class ScrollToSection(val listIndex: Int) : HomeEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeSectionsUseCase: GetHomeSectionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>(replay = 1)
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        getHomeSectionsUseCase()
            .onEach { result ->
                result.fold(
                    onSuccess = { sections ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            sections = sections,
                            selectedTabIndex = 0
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Unknown error"
                        )
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    fun onTabSelected(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = index)
        _events.tryEmit(HomeEvent.ScrollToSection(index))
    }

    fun onScrolledToSection(sectionIndex: Int) {
        if (sectionIndex != _uiState.value.selectedTabIndex) {
            _uiState.value = _uiState.value.copy(selectedTabIndex = sectionIndex)
        }
    }
}