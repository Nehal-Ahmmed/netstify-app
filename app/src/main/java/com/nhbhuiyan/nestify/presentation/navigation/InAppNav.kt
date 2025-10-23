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
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.presentation.navigation.Components.BottomNavItem
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.navigation.Components.bottomNavigation
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FileDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FileScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.HomeScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.LinkDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.LinksListScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.NoteDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.NotesListScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.RoutineScreen.RoutineDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.RoutineScreen.RoutineListScreen

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
        Route.Notes.route -> 1
        Route.Links.route -> 2
        Route.Files.route -> 3

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
                        1 -> navigateToTab(navController = navController, route = Route.Notes.route)
                        2 -> navigateToTab(navController = navController, route = Route.Links.route)
                        3 -> navigateToTab(navController = navController, route = Route.Files.route)
                    }
                }
            )
        }
    ) {
        val bottomPadding = it.calculateBottomPadding()

        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = modifier.padding(bottom = bottomPadding)
        ) {

            composable(route = Route.Home.route) {
                HomeScreen(navController)
            }

            //notes
            composable(route = Route.Notes.route) {
                NotesListScreen(navController)
            }
            composable(route = Route.NoteDetail.route){
                NoteDetailDestination(navController = navController)
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
            composable(route= Route.Routines.route){
                RoutineListScreen(navController=navController)
            }
            composable(route= Route.RoutineDetail.route){
                RoutineDetailDestination(navController=navController)
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