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

package com.google.jetstream.presentation.screens.movies

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import com.google.jetstream.data.entities.Movie
import com.google.jetstream.data.util.StringConstants
import com.google.jetstream.presentation.LocalMovieRepository
import com.google.jetstream.presentation.common.MoviesRow
import com.google.jetstream.presentation.screens.dashboard.rememberChildPadding

@Composable
fun MoviesScreen(
    onMovieClick: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean
) {
    val childPadding = rememberChildPadding()
    val tvLazyListState = rememberTvLazyListState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            tvLazyListState.firstVisibleItemIndex == 0 &&
                    tvLazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    val movieRepository = LocalMovieRepository.current!!

    val movies_16_9 = remember { movieRepository.getMovies_16_9() }
    val popularFilmsThisWeek = remember { movieRepository.getPopularFilmsThisWeek() }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }
    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) tvLazyListState.animateScrollToItem(0)
    }

    TvLazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = tvLazyListState
    ) {
        item {
            Spacer(modifier = Modifier.padding(top = childPadding.top))
        }
        item {
            MoviesScreenMovieList(
                movieList = movies_16_9,
                onMovieClick = onMovieClick
            )
        }
        item {
            MoviesRow(
                modifier = Modifier.padding(top = childPadding.top),
                title = StringConstants.Composable.PopularFilmsThisWeekTitle,
                movies = popularFilmsThisWeek,
                onMovieClick = onMovieClick
            )
        }
        item {
            Spacer(
                modifier = Modifier.padding(
                    bottom = LocalConfiguration.current.screenHeightDp.dp.times(0.19f)
                )
            )
        }
    }
}
