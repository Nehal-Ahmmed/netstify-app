package com.nhbhuiyan.nestify.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route

import com.nhbhuiyan.nestify.presentation.ui.screens.auth.AuthScreen

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val startDest = if (currentUser != null) Route.InAppNav1.route else Route.Auth.route
    
    NavHost(navController=navController,startDestination= startDest){
        // Auth screen
        composable(route = Route.Auth.route) {
            AuthScreen(navController = navController)
        }

        //group 1
        navigation(route= Route.AppStartNav.route, startDestination = Route.SplashScreen.route){
            composable(route= Route.SplashScreen.route){}
        }
        
        //group 2
        navigation(route= Route.InAppNav1.route, startDestination = Route.mediaNav.route){
            composable(route= Route.mediaNav.route){
                InAppNav(
                    onSignOut = {
                        navController.navigate(Route.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}