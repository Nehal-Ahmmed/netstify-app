package com.nhbhuiyan.nestify.projectplans.domain.usecase

import com.nhbhuiyan.nestify.projectplans.domain.model.ProjectPlanModel
import com.nhbhuiyan.nestify.projectplans.domain.repo.ProjectPlanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProjectPlansUseCase @Inject constructor(
    private val repository: ProjectPlanRepository
) {
    operator fun invoke(): Flow<List<ProjectPlanModel>> {
        return repository.getAllProjectPlans()
    }
}

class AddProjectPlanUseCase @Inject constructor(
    private val repository: ProjectPlanRepository
) {
    suspend operator fun invoke(plan: ProjectPlanModel) {
        repository.insertProjectPlan(plan)
    }
}

class DeleteProjectPlanUseCase @Inject constructor(
    private val repository: ProjectPlanRepository
) {
    suspend operator fun invoke(plan: ProjectPlanModel) {
        repository.deleteProjectPlan(plan)
    }
}
