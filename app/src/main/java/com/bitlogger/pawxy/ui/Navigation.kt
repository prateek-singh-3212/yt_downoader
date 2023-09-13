package com.bitlogger.pawxy.ui

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitlogger.pawxy.network.YoutubeAPI
import com.bitlogger.pawxy.ui.screen.YTDownloadScreen
import com.bitlogger.pawxy.ui.screen.YTLinkScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    navController: NavHostController,
    youtubeAPI: YoutubeAPI,
    activityResultLauncher: ActivityResultLauncher<Intent>,
    sharedViewModel: MainViewModel
) {

    AnimatedNavHost(
        navController = navController,
        startDestination = Screens.YTLinkScreen.route
    ) {
        composable(
            route = Screens.YTLinkScreen.route,
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -(1 * it / 2) })
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it })
            },

            ) {
            YTLinkScreen(navController, youtubeAPI, sharedViewModel, activityResultLauncher)
        }
        composable(
            route = Screens.YTDownloadScreen.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { (it) })
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 3 * it / 4 })
            }
        ) {
            YTDownloadScreen(navController, sharedViewModel)
        }
    }
}