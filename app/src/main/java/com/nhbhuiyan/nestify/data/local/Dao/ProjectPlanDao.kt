package com.nhbhuiyan.nestify.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nhbhuiyan.nestify.data.local.entity.ProjectPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectPlanDao {
    @Query("SELECT * FROM project_plans")
    fun getAllProjectPlans(): Flow<List<ProjectPlanEntity>>

    @Query("SELECT * FROM project_plans WHERE id = :id")
    fun getProjectPlanById(id: Int): Flow<ProjectPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectPlan(plan: ProjectPlanEntity)

    @Update
    suspend fun updateProjectPlan(plan: ProjectPlanEntity)

    @Delete
    suspend fun deleteProjectPlan(plan: ProjectPlanEntity)
}
