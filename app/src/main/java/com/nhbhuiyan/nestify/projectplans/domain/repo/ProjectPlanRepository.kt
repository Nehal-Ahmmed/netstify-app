package com.nhbhuiyan.nestify.projectplans.domain.repo

import com.nhbhuiyan.nestify.projectplans.domain.model.ProjectPlanModel
import kotlinx.coroutines.flow.Flow

interface ProjectPlanRepository {
    fun getAllProjectPlans(): Flow<List<ProjectPlanModel>>
    fun getProjectPlanById(id: Int): Flow<ProjectPlanModel?>
    suspend fun insertProjectPlan(plan: ProjectPlanModel)
    suspend fun updateProjectPlan(plan: ProjectPlanModel)
    suspend fun deleteProjectPlan(plan: ProjectPlanModel)
}
