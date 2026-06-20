package com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.usecases.MyProjectsUseCases.AddMyProjectUseCase
import com.nhbhuiyan.nestify.domain.usecases.MyProjectsUseCases.DeleteMyProjectUseCase
import com.nhbhuiyan.nestify.domain.usecases.MyProjectsUseCases.GetAllMyProjectsUseCase
import com.nhbhuiyan.nestify.presentation.ui.screens.MyProjects.ProjectModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProjectsViewModel @Inject constructor(
    private val getAllMyProjectsUseCase: GetAllMyProjectsUseCase,
    private val addMyProjectUseCase: AddMyProjectUseCase,
    private val deleteMyProjectUseCase: DeleteMyProjectUseCase
) : ViewModel() {

    private val _projects = MutableStateFlow<List<ProjectModel>>(emptyList())
    val projects: StateFlow<List<ProjectModel>> = _projects.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            getAllMyProjectsUseCase().collect { projectList ->
                _projects.value = projectList
            }
        }
    }

    fun addProject(project: ProjectModel) {
        viewModelScope.launch {
            addMyProjectUseCase(project)
        }
    }

    fun deleteProject(project: ProjectModel) {
        viewModelScope.launch {
            deleteMyProjectUseCase(project)
        }
    }
}
