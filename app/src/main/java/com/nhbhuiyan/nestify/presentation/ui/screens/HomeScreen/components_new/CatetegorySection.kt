package com.nhbhuiyan.nestify.presentation.ui.screens.HomeScreen.components_new

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route

data class CategoryModel(
    var Id: Int = 0,
    var ImagePath: Int,
    var Name: String = ""
)

private val categoryList = listOf<CategoryModel>(
    CategoryModel(Id = 1, ImagePath = R.drawable.noteimage, Name = "Daily Notes"),
    CategoryModel(Id = 2, ImagePath = R.drawable.link, Name = "Links"),
    CategoryModel(Id = 4, ImagePath = R.drawable.exammarks, Name = "Exam Planner"),
    CategoryModel(Id = 5, ImagePath = R.drawable.pplanner, Name = "Project Planner"),
    CategoryModel(Id = 6, ImagePath = R.drawable.projects, Name = "My Projects"),
    CategoryModel(Id = 7, ImagePath = R.drawable.routine, Name = "Schedules"),
    CategoryModel(Id = 8, ImagePath = R.drawable.temporary, Name = "Temporary Shared Items"),
)


@Composable
fun CategorySection(navController: androidx.navigation.NavController) {
    Text(
        text = "Workspace",
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    )
    val rows = categoryList.chunked(3)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEachIndexed { index, categoryModel ->
                    CategoryItem(
                        category = categoryModel,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        onItemClick = {
                            when (categoryModel.Name) {
                                "Daily Notes" -> navController.navigate(Route.Notes.route)
                                "Links" -> navController.navigate(Route.LinkCategories.route)
                                "Exam Planner" -> navController.navigate(Route.ExamPlanner.route)
                                "My Projects" -> navController.navigate(Route.MyProjects.route)
                                "Project Planner" -> navController.navigate(Route.ProjectPlans.route)
                                "Schedules" -> navController.navigate(Route.Schedule.route)
                                //"My Projects" -> navController.navigate(Route.MyProjects.route)
                                else -> { /* Handle others */
                                }
                            }
                        }
                    )
                }
            }
            if (row.size < 3) {
                repeat(3 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }

}


@Composable
fun CategoryItem(
    category: CategoryModel,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(R.color.white),
                shape = RoundedCornerShape(13.dp)
            )
            .clickable(onClick = onItemClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(category.ImagePath),
            contentDescription = null,
            modifier = Modifier.size(45.dp)
        )
        Text(
            text = category.Name,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}