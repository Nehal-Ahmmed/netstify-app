package com.nhbhuiyan.nestify.presentation.ui.screens.SearchScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SearchScreen(navController: NavController) {
    Text(
        text = "Search Screen",
        style = MaterialTheme.typography.displayLarge
    )
}