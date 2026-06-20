package com.nhbhuiyan.nestify.domain.usecases.MyProjectsUseCases

import com.nhbhuiyan.nestify.domain.repository.MyProjectRepository
import com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.ProjectModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMyProjectsUseCase @Inject constructor(
    private val repository: MyProjectRepository
) {
    operator fun invoke(): Flow<List<ProjectModel>> {
        return repository.getAllMyProjects()
    }
}

class AddMyProjectUseCase @Inject constructor(
    private val repository: MyProjectRepository
) {
    suspend operator fun invoke(project: ProjectModel) {
        repository.insertMyProject(project)
    }
}

class DeleteMyProjectUseCase @Inject constructor(
    private val repository: MyProjectRepository
) {
    suspend operator fun invoke(project: ProjectModel) {
        repository.deleteMyProject(project)
    }
}
