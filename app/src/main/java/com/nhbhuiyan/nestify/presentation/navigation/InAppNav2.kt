package com.nhbhuiyan.nestify.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route

@Composable
fun InAppNav2() {

    val navController = rememberNavController()

    Scaffold (
        modifier = Modifier.fillMaxSize()
    ){
        val bottompadding = it.calculateBottomPadding()

        NavHost(
            navController=navController,
            modifier = Modifier.padding(bottom = bottompadding),
            startDestination = Route.Notes.route
        ) { }
    }
    
}