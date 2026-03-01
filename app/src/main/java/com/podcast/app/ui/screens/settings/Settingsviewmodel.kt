package com.podcast.app.ui.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.podcast.app.domain.model.AppLanguage
import com.podcast.app.domain.model.AppSettings
import com.podcast.app.domain.usecase.GetSettingsUseCase
import com.podcast.app.domain.usecase.SaveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {

    // Single DataStore subscription shared by the whole app.
    val settings: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    // Becomes true once the first real DataStore value has been read.
    // MainActivity holds the splash open until this flips so PodcastTheme
    // never draws with the wrong initialValue default (isDarkMode = false).
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    init {
        viewModelScope.launch {
            // .first() on the raw use-case flow (not the stateIn) suspends
            // until DataStore emits its first real persisted value, then
            // immediately cancels — one read, no ongoing subscription.
            // We do NOT subscribe a second time to settings (stateIn) to avoid
            // creating a duplicate DataStore collector.
            getSettingsUseCase().first()
            _isReady.value = true
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            saveSettingsUseCase(settings.value.copy(isDarkMode = enabled))
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            saveSettingsUseCase(settings.value.copy(language = language))
        }
        // Apply locale HERE — only when the user explicitly picks a language.
        // NOT in a LaunchedEffect on every cold start, which caused Activity
        // recreation on every launch (the double-restart bug).
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language.tag)
        )
    }
}