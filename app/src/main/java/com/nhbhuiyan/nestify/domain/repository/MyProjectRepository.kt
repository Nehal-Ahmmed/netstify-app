package com.nhbhuiyan.nestify.domain.repository

import com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.ProjectModel
import kotlinx.coroutines.flow.Flow

interface MyProjectRepository {
    fun getAllMyProjects(): Flow<List<ProjectModel>>
    fun getMyProjectById(id: String): Flow<ProjectModel?>
    suspend fun insertMyProject(project: ProjectModel)
    suspend fun updateMyProject(project: ProjectModel)
    suspend fun deleteMyProject(project: ProjectModel)
}
