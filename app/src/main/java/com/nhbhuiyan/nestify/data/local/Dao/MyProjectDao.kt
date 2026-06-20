package com.nhbhuiyan.nestify.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nhbhuiyan.nestify.data.local.entity.MyProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyProjectDao {
    @Query("SELECT * FROM my_projects")
    fun getAllMyProjects(): Flow<List<MyProjectEntity>>

    @Query("SELECT * FROM my_projects WHERE id = :id")
    fun getMyProjectById(id: String): Flow<MyProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyProject(project: MyProjectEntity)

    @Update
    suspend fun updateMyProject(project: MyProjectEntity)

    @Delete
    suspend fun deleteMyProject(project: MyProjectEntity)
}
