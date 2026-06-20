package com.nhbhuiyan.nestify.projectplans.Presentation.screens

import androidx.compose.foundation.background
import com.nhbhuiyan.nestify.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhbhuiyan.nestify.projectplans.Presentation.viewmodel.ProjectPlanViewModel
import com.nhbhuiyan.nestify.projectplans.domain.model.ProjectPlanModel

//@Preview
@Composable
fun ProjectPlansPage(viewModel: ProjectPlanViewModel = hiltViewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.drawWithContent {
                    drawContent()
                }
            ) {
                Spacer(Modifier.height(24.dp))

                Text(
                    "Demo",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Content Demo") },
                    selected = false,
                    onClick = {}
                )

                NavigationDrawerItem(
                    label = { Text("Content demo") },
                    selected = false,
                    onClick = {}
                )
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.addPlan(
                            ProjectPlanModel(
                                ImagePath = R.drawable.pplanner,
                                Title = "New Master Plan",
                                Description = "Automatically generated professional showcase plan",
                                Completed = 0,
                                Ideas = 5,
                                WorkingWith = "Team Alpha"
                            )
                        )
                    },
                    containerColor = colorResource(R.color.primary),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, "Create New")
                }
            }
        ) { innerpadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(R.color.lightBlue))
                    .padding(paddingValues = innerpadding)
            ) {
                HeroSectionProjectPlan()
                PlansList(viewModel = viewModel)
            }
        }

    }
}



