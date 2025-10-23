package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.GenericList
import com.nhbhuiyan.nestify.presentation.ui.components.LoadingShimmer
import com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components.linkItem
import com.nhbhuiyan.nestify.presentation.viewModel.LinksViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinksListScreen(navController: NavController) {
    val viewModel: LinksViewmodel = hiltViewModel()
    val state = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Links") }
        )
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.createLink("test Link","this is a test link", "www.google.com")
                }
            ){
                Icon(Icons.Default.Add, contentDescription = "add link")
            }
        }
        ){padding->
        if (state.value.isLoading){
            LoadingShimmer()
        }else{
            GenericList(
                items = state.value.links,
                modifier = Modifier.padding(padding)
            ) { link -> linkItem(link, onClick = {navController.navigate(Route.LinkDetail.createRoute(link.id))}) }
        }
    }
}