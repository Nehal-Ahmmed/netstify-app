package com.nhbhuiyan.nestify.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.PhotoLibrary

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
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.presentation.navigation.Components.BottomNavItem
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.navigation.Components.bottomNavigation
import com.nhbhuiyan.nestify.presentation.ui.screens.ArchiveScreen.ArchiveScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.FacouritesScreen.FacouritesScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FileDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FileScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FolderScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.HomeScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.LinkDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.LinksListScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.NoteDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.NotesListScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.ServiceScreen.ServiceScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.createnote.CreateNoteScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.schedule.ScheduleScreen
import com.nhbhuiyan.nestify.projectplans.Presentation.screens.ProjectPlansPage

@Composable
fun InAppNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem(icon = Icons.Default.Home, text = "Home"),
        BottomNavItem(icon = Icons.Default.PhotoLibrary, text = "Gallery"),
        BottomNavItem(icon = Icons.Default.LibraryBooks, text = "Library"),
        BottomNavItem(icon = Icons.Default.Build, text = "Services"),
        BottomNavItem(icon = Icons.Default.AccountCircle, text = "Profile")
    )
    var backStackState = navController.currentBackStackEntryAsState().value
    var selectedItem by rememberSaveable {
        mutableStateOf(0)
    }

    selectedItem = when (backStackState?.destination?.route) {
        Route.Home.route -> 0
        Route.Gallery.route -> 1
        Route.Library.route -> 2
        Route.Services.route -> 3
        Route.Profile.route -> 4

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
                        1 -> navigateToTab(navController = navController, route = Route.Gallery.route)
                        2 -> navigateToTab(navController = navController, route = Route.Library.route)
                        3 -> navigateToTab(navController = navController, route = Route.Services.route)
                        4 -> navigateToTab(navController = navController, route = Route.Profile.route)
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

                composable(route = Route.Gallery.route) {
                    com.nhbhuiyan.nestify.presentation.ui.screens.GalleryScreen.GalleryScreen(navController)
                }

                composable(route = Route.Library.route) {
                    com.nhbhuiyan.nestify.presentation.ui.screens.LibraryScreen.LibraryScreen(navController)
                }

                composable(route = Route.Services.route) {
                    // Placeholder for ServicesScreen
                    ServiceScreen(navController)
                }

                composable(route = Route.Profile.route) {
                    com.nhbhuiyan.nestify.presentation.ui.screens.ProfileScreen.ProfileScreen(navController)
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
            composable(route = Route.LinkCategories.route) {
                com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.LinkCategoriesScreen(navController)
            }
            composable(route = Route.CategorySpreadSheet.route) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.CategorySpreadSheetScreen(navController, categoryId)
            }
            composable(route = Route.LinkGroupDetail.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.LinkGroupDetailScreen(navController, groupId)
            }
            composable(route = Route.LinkDetail.route) {
                LinkDetailDestination(navController)
            }

            //project plans
            composable(route = Route.ProjectPlans.route) {
                com.nhbhuiyan.nestify.presentation.ui.screens.ProjectPlanner.ProjectPlannerScreen(navController)
            }
            composable(route = Route.ProjectPlanDetail.route) { backStackEntry ->
                val planId = backStackEntry.arguments?.getString("planId") ?: ""
                com.nhbhuiyan.nestify.presentation.ui.screens.ProjectPlanner.ProjectPlanDetailScreen(navController, planId)
            }

            //schedule
            composable(route = Route.Schedule.route) {
                ScheduleScreen()
            }

            //exam planner
            composable(route = Route.ExamPlanner.route) {
                com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner.ExamPlannerScreen(navController)
            }
            composable(route = Route.ExamDetail.route) { backStackEntry ->
                val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
                com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner.ExamDetailScreen(navController, subjectName)
            }

            //my projects
            composable(route = Route.MyProjects.route) {
                com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.MyProjectsScreen(navController)
            }
            composable(route = Route.ProjectDetail.route) {
                com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.ProjectDetailScreen(navController)
            }


            //files and folders
            composable(route = Route.FolderScreen.route){
                FolderScreen(navController = navController)
            }

            composable(route = Route.Files.route) {
                FileScreen(navController)
            }
            composable(route = Route.FileDetail.route) {
                FileDetailDestination(navController)
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