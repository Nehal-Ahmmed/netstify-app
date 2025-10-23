package com.nhbhuiyan.nestify.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    
    NavHost(navController=navController,startDestination= Route.InAppNav.route){

        //group 1
        navigation(route= Route.AppStartNav.route, startDestination = Route.SplashScreen.route){
            composable(route= Route.SplashScreen.route){}
        }
        
        //group 2
        navigation(route= Route.InAppNav.route, startDestination = Route.mediaNav.route){
            composable(route= Route.mediaNav.route){
                InAppNav()
            }
        }
    }
}