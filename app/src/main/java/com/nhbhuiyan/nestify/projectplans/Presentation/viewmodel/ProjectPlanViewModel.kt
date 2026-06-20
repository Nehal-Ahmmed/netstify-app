package com.nhbhuiyan.nestify.projectplans.Presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.projectplans.domain.model.ProjectPlanModel
import com.nhbhuiyan.nestify.projectplans.domain.usecase.AddProjectPlanUseCase
import com.nhbhuiyan.nestify.projectplans.domain.usecase.DeleteProjectPlanUseCase
import com.nhbhuiyan.nestify.projectplans.domain.usecase.GetAllProjectPlansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectPlanViewModel @Inject constructor(
    private val getAllProjectPlansUseCase: GetAllProjectPlansUseCase,
    private val addProjectPlanUseCase: AddProjectPlanUseCase,
    private val deleteProjectPlanUseCase: DeleteProjectPlanUseCase
) : ViewModel() {

    private val _plans = MutableStateFlow<List<ProjectPlanModel>>(emptyList())
    val plans: StateFlow<List<ProjectPlanModel>> = _plans.asStateFlow()

    init {
        loadPlans()
    }

    private fun loadPlans() {
        viewModelScope.launch {
            getAllProjectPlansUseCase().collect { planList ->
                _plans.value = planList
            }
        }
    }

    fun addPlan(plan: ProjectPlanModel) {
        viewModelScope.launch {
            addProjectPlanUseCase(plan)
        }
    }

    fun deletePlan(plan: ProjectPlanModel) {
        viewModelScope.launch {
            deleteProjectPlanUseCase(plan)
        }
    }
}
