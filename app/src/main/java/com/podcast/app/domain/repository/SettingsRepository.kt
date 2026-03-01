package com.podcast.app.domain.repository

import com.podcast.app.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>

    suspend fun saveSettings(settings: AppSettings)
}