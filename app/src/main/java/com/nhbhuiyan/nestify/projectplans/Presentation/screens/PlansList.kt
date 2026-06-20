package com.nhbhuiyan.nestify.projectplans.Presentation.screens

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.R
import com.nhbhuiyan.nestify.presentation.ui.screens.ServiceScreen.ServiceData
import com.nhbhuiyan.nestify.projectplans.domain.model.ProjectPlanModel
import com.nhbhuiyan.nestify.ui.theme.NestifySlate
import com.nhbhuiyan.nestify.projectplans.Presentation.viewmodel.ProjectPlanViewModel

@Composable
fun PlansList(viewModel: ProjectPlanViewModel) {
    val plansList by viewModel.plans.collectAsState()

    Scaffold(
        modifier = Modifier

    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(R.color.lightBlue))
                .padding(paddingValues = innerPadding)
        ) {
            items(plansList) { plan ->
                PlansCard(plan)
            }
        }
    }
}


@Composable
fun PlansCard(planmodel: ProjectPlanModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color = colorResource(R.color.white).copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource( planmodel.ImagePath),
                    contentDescription = null,
                    tint = Color.Blue.copy(alpha = 0.50f),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(20.dp))
            Column {
                Text(
                    text = planmodel.Title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NestifySlate
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = planmodel.Description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}