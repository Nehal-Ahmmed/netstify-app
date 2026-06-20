package com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.data.HomeViewModel
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.components_new.Banner
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.components_new.CategorySection
import com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.components_new.topBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeState by viewModel.homeState.collectAsState()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Nestify Settings",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { navController.navigate(Route.Settings.route) }
                )
                NavigationDrawerItem(
                    label = { Text("Bookmarks") },
                    selected = false,
                    onClick = { navController.navigate(Route.Bookmarks.route) }
                )
            }
        }
    ) {
        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("Nestify", fontWeight = FontWeight.Bold) },
//                    navigationIcon = {
//                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
//                            Icon(Icons.Default.Menu, contentDescription = "Menu")
//                        }
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
//                )
//            },

        ) { paddingValues ->

            LazyColumn(
                modifier= Modifier
                    .fillMaxSize()
                    .background(color = colorResource(R.color.lightBlue))
                    .padding(paddingValues = paddingValues)
            ) {
                item {topBar()}
                item {CategorySection(navController)}
                item {Banner()}
            }

//            ConstraintLayout(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(backgroundBrush)
//                    .padding(paddingValues)
//                    .padding(horizontal = 16.dp)
//            ) {
//                val (greetingText, statsCard, gridMenu) = createRefs()
//
//                Text(
//                    text = "Welcome back,\n${homeState.userName}",
//                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
//                    modifier = Modifier.constrainAs(greetingText) {
//                        top.linkTo(parent.top, margin = 24.dp)
//                        start.linkTo(parent.start)
//                    }
//                )
//
//                // Stunning stat card
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .constrainAs(statsCard) {
//                            top.linkTo(greetingText.bottom, margin = 32.dp)
//                            start.linkTo(parent.start)
//                            end.linkTo(parent.end)
//                        },
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
//                    shape = RoundedCornerShape(24.dp),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(24.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Column {
//                            Text("Today's Notes", style = MaterialTheme.typography.labelLarge)
//                            Text("${homeState.todaysNotesCount}", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold))
//                        }
//                        Column {
//                            Text("Recent Activities", style = MaterialTheme.typography.labelLarge)
//                            Text("${homeState.recentActivityCount}", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold))
//                        }
//                    }
//                }
//
//                // Simplified actions
//                Column(
//                    modifier = Modifier.constrainAs(gridMenu) {
//                        top.linkTo(statsCard.bottom, margin = 32.dp)
//                        start.linkTo(parent.start)
//                        end.linkTo(parent.end)
//                        bottom.linkTo(parent.bottom)
//                        height = Dimension.fillToConstraints
//                    }
//                ) {
//                    Text("Quick Access", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
//                    Spacer(Modifier.height(16.dp))
//
//                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                        QuickAccessButton("Notes", Modifier.weight(1f)) { navController.navigate(Route.Notes.route) }
//                        QuickAccessButton("Links", Modifier.weight(1f)) { navController.navigate(Route.Links.route) }
//                    }
//                    Spacer(Modifier.height(16.dp))
//                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                        QuickAccessButton("Files", Modifier.weight(1f)) { navController.navigate(Route.FolderScreen.route) }
//                        QuickAccessButton("Routines", Modifier.weight(1f)) { navController.navigate(Route.Routines.route) }
//                    }
//                }
//            }
        }
    }
}

@Composable
fun QuickAccessButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.8f
            )
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}