package com.podcast.app.domain.model

data class AppSettings(
    val isDarkMode: Boolean = false,
    val language: AppLanguage = AppLanguage.ARABIC
)