package com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen

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
import com.nhbhuiyan.nestify.presentation.ui.screens.FileScreen.components.fileItem
import com.nhbhuiyan.nestify.presentation.viewModel.FileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileScreen(navController: NavController,modifier: Modifier = Modifier) {
    val viewModel : FileViewModel = hiltViewModel()
    val state = viewModel.uiState.collectAsState()

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Files")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.createFiles("uri","filename",".pdf", size = 1000, type = "pdf", mimeType = "application/pdf")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ){innerPadding->
        if(state.value.isLoading) LoadingShimmer()
        else{
            GenericList(
                items = state.value.files,
                    modifier = Modifier.padding(innerPadding)
            ) {file->
                fileItem(file, onClick = {navController.navigate(Route.FileDetail.createRoute(fileId = file.id))})
            }
        }
    }
}