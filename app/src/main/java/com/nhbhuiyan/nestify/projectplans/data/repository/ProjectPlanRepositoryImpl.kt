package com.nhbhuiyan.nestify.projectplans.data.repository

import com.nhbhuiyan.nestify.data.local.Dao.ProjectPlanDao
import com.nhbhuiyan.nestify.data.local.entity.ProjectPlanEntity
import com.nhbhuiyan.nestify.projectplans.domain.model.ProjectPlanModel
import com.nhbhuiyan.nestify.projectplans.domain.repo.ProjectPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProjectPlanRepositoryImpl @Inject constructor(
    private val dao: ProjectPlanDao
) : ProjectPlanRepository {

    override fun getAllProjectPlans(): Flow<List<ProjectPlanModel>> {
        return dao.getAllProjectPlans().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getProjectPlanById(id: Int): Flow<ProjectPlanModel?> {
        return dao.getProjectPlanById(id).map { it?.toModel() }
    }

    override suspend fun insertProjectPlan(plan: ProjectPlanModel) {
        dao.insertProjectPlan(plan.toEntity())
    }

    override suspend fun updateProjectPlan(plan: ProjectPlanModel) {
        dao.updateProjectPlan(plan.toEntity())
    }

    override suspend fun deleteProjectPlan(plan: ProjectPlanModel) {
        dao.deleteProjectPlan(plan.toEntity())
    }
}

// Extension functions for mapping
fun ProjectPlanEntity.toModel(): ProjectPlanModel {
    return ProjectPlanModel(
        Id = id,
        ImagePath = imagePath,
        Title = title,
        Description = description,
        Ideas = ideas,
        Completed = completed,
        WorkingWith = workingWith
    )
}

fun ProjectPlanModel.toEntity(): ProjectPlanEntity {
    return ProjectPlanEntity(
        id = Id,
        imagePath = ImagePath,
        title = Title,
        description = Description,
        ideas = Ideas,
        completed = Completed,
        workingWith = WorkingWith
    )
}
