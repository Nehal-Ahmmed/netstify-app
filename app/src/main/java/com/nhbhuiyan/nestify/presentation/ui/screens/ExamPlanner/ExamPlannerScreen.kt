package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhbhuiyan.nestify.ui.theme.*
import kotlinx.coroutines.launch

enum class ExamPlannerTab(val title: String, val icon: ImageVector) {
    CGPA_DASHBOARD("CGPA Dashboard", Icons.Default.Assessment),
    SUBJECTS_DETAILS("Subjects Details", Icons.Default.MenuBook),
    CLASS_TEST_MARKS("Class Test Marks", Icons.Default.List),
    EXAM_PLAN("Exam Plan", Icons.Default.PlaylistAddCheck),
    EXAM_RESULTS("Exam Results", Icons.Default.Star),
    PACKAGING_SYNC("Packaging & Sync", Icons.Default.Sync)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPlannerScreen(
    navController: NavController,
    viewModel: ExamPlannerViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf(ExamPlannerTab.CGPA_DASHBOARD) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = NestifySurface,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                modifier = Modifier.width(300.dp)
            ) {
                // Sidebar Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NestifySlate, Color(0xFF1E272C))
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Surface(
                            color = NestifyPeach.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "ACADEMIC PORTAL",
                                color = NestifyPeach,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nestify Exam Planner",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Level 2 Term 2",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Menu items
                ExamPlannerTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationDrawerItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title, tint = if (isSelected) Color.White else NestifySlate) },
                        label = { Text(tab.title, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                        selected = isSelected,
                        onClick = {
                            currentTab = tab
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = NestifySlate,
                            selectedTextColor = Color.White,
                            unselectedTextColor = NestifySlate
                        ),
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .height(50.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                currentTab.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = NestifySlate
                            )
                            Text(
                                "Academic Workspace",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu", tint = NestifySlate)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to Workspaces", tint = NestifySlate)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NestifySurface,
                        scrolledContainerColor = NestifySurface
                    )
                )
            },
            containerColor = NestifySurface
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    ExamPlannerTab.CGPA_DASHBOARD -> CGPADashboardTab(viewModel)
                    ExamPlannerTab.SUBJECTS_DETAILS -> SubjectsDetailsTab(viewModel)
                    ExamPlannerTab.CLASS_TEST_MARKS -> ClassTestMarksTab(viewModel)
                    ExamPlannerTab.EXAM_PLAN -> ExamPlanTab(navController, viewModel)
                    ExamPlannerTab.EXAM_RESULTS -> ExamResultsTab(viewModel)
                    ExamPlannerTab.PACKAGING_SYNC -> PackagingSyncTab(viewModel)
                }
            }
        }
    }
}
