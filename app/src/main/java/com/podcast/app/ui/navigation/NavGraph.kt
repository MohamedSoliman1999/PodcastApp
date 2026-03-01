package com.podcast.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.podcast.app.ui.components.PodcastBottomBar
import com.podcast.app.ui.player.MiniPlayerBar
import com.podcast.app.ui.player.PlayerViewModel
import com.podcast.app.ui.screens.home.HomeScreen
import com.podcast.app.ui.screens.search.SearchScreen
import com.podcast.app.ui.screens.settings.SettingsScreen
import com.podcast.app.ui.screens.settings.SettingsViewModel


sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object Search   : Screen("search")
    object Library  : Screen("library")
    object Settings : Screen("settings")
}
private fun NavOptionsBuilder.topLevelNavOptions() {
    popUpTo(Screen.Home.route) {
        saveState = true
        inclusive = false
    }
    launchSingleTop = true
    restoreState    = true
}

private fun NavController.navigateTopLevel(route: String) {
    navigate(route) { topLevelNavOptions() }
}


@Composable
fun PodcastNavGraph(settingsViewModel: SettingsViewModel) {
    val navController     = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    val playerViewModel: PlayerViewModel = hiltViewModel()
    val playerState by playerViewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        bottomBar = {
            Column {
                if (playerState.isVisible) {
                    MiniPlayerBar(
                        state         = playerState,
                        onPlayPause   = playerViewModel::playPause,
                        onSkipForward = playerViewModel::skipForward,
                        onSeek        = playerViewModel::seekTo,
                        onExpand      = { /* TODO: full player */ }
                    )
                }
                PodcastBottomBar(
                    currentRoute = currentRoute,
                    onNavigate   = { screen ->
                        navController.navigateTopLevel(screen.route)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSettingsClick = {
                        navController.navigateTopLevel(Screen.Settings.route)
                    }
                )
            }
            composable(Screen.Search.route)  { SearchScreen() }
            composable(Screen.Library.route) { Surface(Modifier.fillMaxSize()) {} }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}