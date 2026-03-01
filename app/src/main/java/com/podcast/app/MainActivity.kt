package com.podcast.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.podcast.app.ui.navigation.PodcastNavGraph
import com.podcast.app.ui.screens.settings.SettingsViewModel
import com.podcast.app.ui.theme.PodcastTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            !settingsViewModel.isReady.value
        }
        enableEdgeToEdge()

        setContent {
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            PodcastTheme(darkTheme = settings.isDarkMode) {
                PodcastNavGraph(settingsViewModel = settingsViewModel)
            }
        }
    }
}