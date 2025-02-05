/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.jetstream.presentation.screens.favourites

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import com.google.jetstream.presentation.LocalMovieRepository
import com.google.jetstream.presentation.screens.dashboard.rememberChildPadding

@Composable
fun FavouritesScreen(
    onMovieClick: (movieId: String) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean
) {
    val childPadding = rememberChildPadding()
    val filteredMoviesGridState = rememberTvLazyGridState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            filteredMoviesGridState.firstVisibleItemIndex == 0 &&
                    filteredMoviesGridState.firstVisibleItemScrollOffset < 100
        }
    }

    val movieRepository = LocalMovieRepository.current!!
    val favouriteMovies = remember { movieRepository.getFavouriteMovies() }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }
    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) filteredMoviesGridState.animateScrollToItem(0)
    }

    val chipRowTopPadding by animateDpAsState(
        targetValue = if (shouldShowTopBar) 0.dp else childPadding.top, label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = childPadding.start)
    ) {
        Column {
            val movieFilterRange = remember { (0..9).toList() }
            val tvShowsFilterRange = remember { (10..17).toList() }
            val addedLastWeekFilterRange = remember { (18..23).toList() }
            val availableIn4KFilterRange = remember { (24..28).toList() }
            val movieListRange = remember { mutableStateListOf<Int>() }

            MovieFilterChipRow(
                modifier = Modifier.padding(top = chipRowTopPadding),
                movieListRange = movieListRange,
                movieFilterRange = movieFilterRange,
                tvShowsFilterRange = tvShowsFilterRange,
                addedLastWeekFilterRange = addedLastWeekFilterRange,
                availableIn4KFilterRange = availableIn4KFilterRange
            )
            FilteredMoviesGrid(
                state = filteredMoviesGridState,
                movies = favouriteMovies,
                movieListRange = movieListRange,
                onMovieClick = onMovieClick
            )
        }
    }
}
