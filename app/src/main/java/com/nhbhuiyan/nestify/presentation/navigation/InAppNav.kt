package com.nhbhuiyan.nestify.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.nhbhuiyan.nestify.domain.model.Note
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.navigation.Components.OxyChemTopAppBar
import com.nhbhuiyan.nestify.presentation.navigation.Components.OxyChemBottomNavBar
import com.nhbhuiyan.nestify.presentation.navigation.Components.OxyChemDrawerContent
import com.nhbhuiyan.nestify.presentation.ui.screens.AcademicFeed.AcademicFeedScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.ArchiveScreen.ArchiveScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.FacouritesScreen.FacouritesScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FileDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FileScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.FolderScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.HomeScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.LinkDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.MySpace.MySpaceScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.NoteDetailDestination
import com.nhbhuiyan.nestify.presentation.ui.screens.NoteScreen.NotesListScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.ServiceScreen.ServiceScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.createnote.CreateNoteScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.schedule.ScheduleScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.settings.SettingsScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.Management.ManagementHubScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.Management.MergeRequestsScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.Management.RoleManagementScreen
import com.nhbhuiyan.nestify.presentation.ui.screens.Management.AnnouncementsScreen

@Composable
fun InAppNav(
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navViewModel: NavigationViewModel = hiltViewModel()
    val session by navViewModel.session.collectAsState(initial = null)
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackState = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackState?.destination?.route

    val mainRoutes = listOf(Route.Home.route, Route.ExamPlanner.route, Route.Network.route, Route.MySpace.route)
    val isMainPage = currentRoute in mainRoutes

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isMainPage,
        drawerContent = {
            OxyChemDrawerContent(
                session = session,
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onNavigateToRoute = { route ->
                    if (route in mainRoutes) {
                        navigateToTab(navController, route)
                    } else {
                        navController.navigate(route)
                    }
                },
                onSignOut = onSignOut
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (isMainPage) {
                    OxyChemTopAppBar(
                        session = session,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSearchClick = { navController.navigate(Route.Search.route) },
                        onNotificationsClick = { navController.navigate(Route.Network.route) },
                        onProfileClick = { navController.navigate(Route.Profile.route) }
                    )
                }
            },
            bottomBar = {
                if (isMainPage) {
                    OxyChemBottomNavBar(
                        selectedRoute = currentRoute ?: Route.Home.route,
                        onItemClick = { route ->
                            navigateToTab(navController, route)
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Route.BottomNavBarNav.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // ── Bottom-nav tab graph ──────────────────────────────────────────
                    navigation(
                        route = Route.BottomNavBarNav.route,
                        startDestination = Route.Home.route
                    ) {
                        composable(
                            route = Route.Home.route,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            HomeScreen(navController)
                        }
                        composable(
                            route = Route.ExamPlanner.route,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner.ExamPlannerScreen(navController)
                        }
                        composable(
                            route = Route.Network.route,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            AcademicFeedScreen(navController)
                        }
                        composable(
                            route = Route.MySpace.route,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None }
                        ) {
                            MySpaceScreen(navController)
                        }
                    }

            // ── Secondary destinations reachable from the tabs ────────────────
            composable(route = Route.Library.route) {
                com.nhbhuiyan.nestify.presentation.ui.screens.LibraryScreen.LibraryScreen(navController)
            }
            composable(route = Route.Services.route) {
                ServiceScreen(navController)
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
            composable(route = Route.ExamDetail.route) { backStackEntry ->
                val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
                com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner.ExamDetailScreen(navController, subjectName)
            }
            composable(route = Route.ReadingRoom.route) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId")?.toLongOrNull() ?: 0L
                val viewModel: com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner.ExamPlannerViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner.ReadingRoomScreen(
                    topicId = topicId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            //my projects
            composable(route = Route.MyProjects.route) {
                com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.MyProjectsScreen(navController)
            }
            composable(route = Route.ProjectDetail.route) {
                com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.ProjectDetailScreen(navController)
            }

            //files and folders
            composable(route = Route.FolderScreen.route) {
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

            //search
            composable(route = Route.Search.route) {
                com.nhbhuiyan.nestify.presentation.ui.screens.SearchScreen.SearchScreen(navController)
            }

            //settings
            composable(route = Route.Settings.route) {
                SettingsScreen(
                    navController = navController,
                    onSignOut = onSignOut
                )
            }

            // Management Hub
            composable(route = Route.Management.route) {
                ManagementHubScreen(navController = navController)
            }
            composable(route = Route.MergeRequests.route) {
                MergeRequestsScreen(navController = navController)
            }
            composable(route = Route.RoleManagement.route) {
                RoleManagementScreen(navController = navController)
            }
            composable(route = Route.Announcements.route) {
                AnnouncementsScreen(navController = navController)
            }
        }
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
