package com.nhbhuiyan.nestify.data.repository

import com.nhbhuiyan.nestify.data.local.Dao.MyProjectDao
import com.nhbhuiyan.nestify.data.local.entity.MyProjectEntity
import com.nhbhuiyan.nestify.domain.repository.MyProjectRepository
import com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.ProjectModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyProjectRepositoryImpl @Inject constructor(
    private val dao: MyProjectDao
) : MyProjectRepository {

    override fun getAllMyProjects(): Flow<List<ProjectModel>> {
        return dao.getAllMyProjects().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getMyProjectById(id: String): Flow<ProjectModel?> {
        return dao.getMyProjectById(id).map { it?.toModel() }
    }

    override suspend fun insertMyProject(project: ProjectModel) {
        dao.insertMyProject(project.toEntity())
    }

    override suspend fun updateMyProject(project: ProjectModel) {
        dao.updateMyProject(project.toEntity())
    }

    override suspend fun deleteMyProject(project: ProjectModel) {
        dao.deleteMyProject(project.toEntity())
    }
}

// Extension functions for mapping
fun MyProjectEntity.toModel(): ProjectModel {
    return ProjectModel(
        id = id,
        name = name,
        motive = motive,
        description = description,
        features = features,
        specialities = specialities,
        techStack = techStack,
        libraries = libraries,
        sources = sources,
        brandLogo = brandLogo,
        demoImages = demoImages,
        videoUrl = videoUrl,
        whereToFind = whereToFind,
        category = category,
        status = status
    )
}

fun ProjectModel.toEntity(): MyProjectEntity {
    return MyProjectEntity(
        id = id,
        name = name,
        motive = motive,
        description = description,
        features = features,
        specialities = specialities,
        techStack = techStack,
        libraries = libraries,
        sources = sources,
        brandLogo = brandLogo,
        demoImages = demoImages,
        videoUrl = videoUrl,
        whereToFind = whereToFind,
        category = category,
        status = status
    )
}
