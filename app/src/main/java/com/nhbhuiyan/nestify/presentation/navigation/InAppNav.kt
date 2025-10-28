package com.nhbhuiyan.nestify.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.presentation.navigation.Components.BottomNavItem
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.navigation.Components.bottomNavigation
import com.nhbhuiyan.nestify.presentation.ui.screens.ArchiveScreen.ArchiveScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.FacouritesScreen.FacouritesScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FileDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FileScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.HomeScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.LinkDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.LinksListScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.NoteDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.NotesListScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.RoutineScreen.RoutineDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.RoutineScreen.RoutineListScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.SearchScreen.SearchScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.bookmarks.BookmarksScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.createnote.CreateNoteScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.settings.SettingsScreen

@Composable
fun InAppNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem(icon = R.drawable.ic_home, text = "Home"),
        BottomNavItem(icon = R.drawable.ic_search, text = "Search"),
        BottomNavItem(icon = R.drawable.ic_bookmark, text = "BookMark"),
        BottomNavItem(icon = R.drawable.baseline_settings_24, text = "Settings")
    )
    var backStackState = navController.currentBackStackEntryAsState().value
    var selectedItem by rememberSaveable {
        mutableStateOf(0)
    }

    selectedItem = when (backStackState?.destination?.route) {
        Route.Home.route -> 0
        Route.Search.route -> 1
        Route.Bookmarks.route -> 2
        Route.Settings.route -> 3

        else -> 0
    }



    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            bottomNavigation(
                items = items,
                selectedItem = selectedItem,
                onItemClick = { index ->
                    when (index) {
                        0 -> navigateToTab(navController = navController, route = Route.Home.route)
                        1 -> navigateToTab(
                            navController = navController,
                            route = Route.Search.route
                        )

                        2 -> navigateToTab(
                            navController = navController,
                            route = Route.Bookmarks.route
                        )

                        3 -> navigateToTab(
                            navController = navController,
                            route = Route.Settings.route
                        )
                    }
                }
            )
        }
    ) {
        val bottomPadding = it.calculateBottomPadding()

        NavHost(
            navController = navController,
            startDestination = Route.BottomNavBarNav.route,
            modifier = modifier.padding(bottom = bottomPadding)
        ) {

            navigation(
                route = Route.BottomNavBarNav.route,
                startDestination = Route.Home.route
            ) {
                //homeScreen
                composable(route = Route.Home.route) {
                    HomeScreen(navController)
                }

                //search
                composable(route = Route.Search.route) {
                    SearchScreen(navController)
                }

                //Bookmarks
                composable(route = Route.Bookmarks.route) {
                    BookmarksScreen(navController)
                }

                //settings
                composable(route = Route.Settings.route) {
                    SettingsScreen(navController)
                }
            }


            //notes
            composable(route = Route.Notes.route) {
                NotesListScreen(navController)
            }
            composable(route = Route.NoteDetail.route) {
                NoteDetailDestination(navController = navController)
            }
            composable(route = Route.createNote.route) {
                CreateNoteScreen(navController = navController)
            }

            //links
            composable(route = Route.Links.route) {
                LinksListScreen(navController)
            }
            composable(route = Route.LinkDetail.route) {
                LinkDetailDestination(navController)
            }

            //files
            composable(route = Route.Files.route) {
                FileScreen(navController)
            }
            composable(route = Route.FileDetail.route) {
                FileDetailDestination(navController)
            }

            //routines
            composable(route = Route.Routines.route) {
                RoutineListScreen(navController = navController)
            }
            composable(route = Route.RoutineDetail.route) {
                RoutineDetailDestination(navController = navController)
            }

            //archive
            composable(route = Route.Archive.route) {
                ArchiveScreen(navController = navController)
            }

            //favorites
            composable(route = Route.Favorites.route) {
                FacouritesScreen(navController = navController)
            }

        }
    }

}

fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route = route) {
        navController.graph.startDestinationId.let { homeRoute ->
            popUpTo(homeRoute) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
}

fun navigateToDetails(navController: NavController, note: Note) {
    navController.currentBackStackEntry?.savedStateHandle?.set(
        key = "",
        value = note
    )
    navController.navigate(
        route = Route.Notes.route
    )
}