package com.podcast.app.domain.usecase

import com.podcast.app.domain.model.AppSettings
import com.podcast.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = repository.getSettings()
}

class SaveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(settings: AppSettings) = repository.saveSettings(settings)
}