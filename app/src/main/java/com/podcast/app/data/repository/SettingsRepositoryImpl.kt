package com.podcast.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.podcast.app.domain.model.AppLanguage
import com.podcast.app.domain.model.AppSettings
import com.podcast.app.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "podcast_settings"
)

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object Keys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val LANGUAGE     = stringPreferencesKey("language")
    }

    override fun getSettings(): Flow<AppSettings> =
        context.settingsDataStore.data.map { prefs ->
            AppSettings(
                isDarkMode = prefs[Keys.IS_DARK_MODE] ?: false,
                language   = prefs[Keys.LANGUAGE]
                    ?.let { tag -> AppLanguage.entries.firstOrNull { it.tag == tag } }
                    ?: AppLanguage.ARABIC
            )
        }

    override suspend fun saveSettings(settings: AppSettings) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.IS_DARK_MODE] = settings.isDarkMode
            prefs[Keys.LANGUAGE]     = settings.language.tag
        }
    }
}