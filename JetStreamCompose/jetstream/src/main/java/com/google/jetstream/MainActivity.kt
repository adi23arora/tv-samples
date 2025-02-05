package com.google.jetstream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.google.gson.Gson
import com.google.jetstream.data.repositories.MovieRepositoryImpl
import com.google.jetstream.data.util.AssetsReader
import com.google.jetstream.data.util.StringConstants
import com.google.jetstream.presentation.LocalMovieRepository
import com.google.jetstream.presentation.screens.Screens
import com.google.jetstream.presentation.screens.categories.CategoryMovieListScreen
import com.google.jetstream.presentation.screens.dashboard.DashboardScreen
import com.google.jetstream.presentation.screens.movies.MovieDetailsScreen
import com.google.jetstream.presentation.screens.videoPlayer.VideoPlayerScreen
import com.google.jetstream.presentation.theme.JetStreamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val movieRepository = remember {
                MovieRepositoryImpl(
                    AssetsReader(context, Gson())
                )
            }
            CompositionLocalProvider(LocalMovieRepository provides movieRepository) {
                App()
            }
        }
    }
}

@Composable
private fun MainActivity.App() {
    JetStreamTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface
            ) {
                val navController = rememberNavController()
                var isComingBackFromDifferentScreen by remember { mutableStateOf(false) }

                NavHost(
                    navController = navController,
                    startDestination = Screens.Dashboard(),
                    builder = {
                        composable(
                            route = Screens.CategoryMovieList(),
                            arguments = listOf(
                                navArgument(CategoryMovieListScreen.CategoryIdBundleKey) {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            backStackEntry.arguments?.let { nnBundle ->
                                val categoryId =
                                    nnBundle.getString(CategoryMovieListScreen.CategoryIdBundleKey)
                                categoryId?.let { nnCategoryId ->
                                    CategoryMovieListScreen(
                                        categoryId = nnCategoryId,
                                        parentNavController = navController,
                                        onBackPressed = {
                                            if (navController.navigateUp()) {
                                                isComingBackFromDifferentScreen = true
                                            }
                                        }
                                    )
                                } ?: run {
                                    Text(
                                        text = StringConstants.Exceptions.InvalidCategoryId,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .wrapContentSize()
                                    )
                                }
                            }
                        }
                        composable(
                            route = Screens.MovieDetails(),
                            arguments = listOf(
                                navArgument(MovieDetailsScreen.MovieIdBundleKey) {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val movieId =
                                backStackEntry.arguments?.getString(MovieDetailsScreen.MovieIdBundleKey)
                            movieId?.let { nnMovieId ->
                                MovieDetailsScreen(
                                    movieId = nnMovieId,
                                    goToMoviePlayer = {
                                        navController.navigate(Screens.VideoPlayer())
                                    },
                                    refreshScreenWithNewMovie = { movie ->
                                        navController.navigate(
                                            Screens.MovieDetails.withArgs(movie.id)
                                        ) {
                                            popUpTo(Screens.MovieDetails()) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    onBackPressed = {
                                        if (navController.navigateUp()) {
                                            isComingBackFromDifferentScreen = true
                                        }
                                    }
                                )
                            }
                        }
                        composable(route = Screens.Dashboard()) {
                            DashboardScreen(
                                openCategoryMovieList = { categoryId ->
                                    navController.navigate(
                                        Screens.CategoryMovieList.withArgs(categoryId)
                                    )
                                },
                                openMovieDetailsScreen = { movieId ->
                                    navController.navigate(
                                        Screens.MovieDetails.withArgs(movieId)
                                    )
                                },
                                openVideoPlayer = {
                                    navController.navigate(Screens.VideoPlayer())
                                },
                                onBackPressed = onBackPressedDispatcher::onBackPressed,
                                isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
                                resetIsComingBackFromDifferentScreen = {
                                    isComingBackFromDifferentScreen = false
                                }
                            )
                        }
                        composable(route = Screens.VideoPlayer()) {
                            VideoPlayerScreen(
                                onBackPressed = {
                                    if (navController.navigateUp()) {
                                        isComingBackFromDifferentScreen = true
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}
