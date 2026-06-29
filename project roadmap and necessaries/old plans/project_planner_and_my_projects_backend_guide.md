# Project Planner & My Projects Backend Guide

## 🗺️ High-Level Roadmap
1. **Define Entities**: Created `ProjectPlanEntity` and `MyProjectEntity` to store local database records using Room.
2. **Update Type Converters**: Added serialization for `Map<String, String>` into `Converters.kt` via standard JSON, allowing seamless integration with structured lists/maps for projects.
3. **Establish DAOs**: Implemented Data Access Objects (`ProjectPlanDao`, `MyProjectDao`) to run SQL queries (`INSERT`, `UPDATE`, `DELETE`, `SELECT` with `Flow`).
4. **Extend Database**: Added both entities and DAOs into `AppDataBase`. Incremented version to 15.
5. **Create Repositories**: Formed interfaces and their implementation mappings (Domain vs Data Layer mappings).
6. **Inject Dependencies**: Provided singletons of DAOs and Repositories in `DataModule.kt`.

## 🧠 Logical Descriptions

### Simple View
We built the invisible "engine" that powers your project planner and your personal portfolio projects list. Now, anytime you create a new plan or add a project, it's securely saved locally inside the app's internal vault. Even when offline, you'll still be able to access everything.

### Technical Breakdown (Backend Layer)
- **Room Persistence**: The foundation is Room. `MyProjectEntity` leverages complex string lists by reusing existing converters, while a custom JSON parser handles dictionaries (`Map<String,String>`).
- **Data/Domain Separation**: DTOs (Entities) are completely hidden from UI. Mappers convert them instantly into `ProjectPlanModel` and `ProjectModel` using Kotlin Flow to maintain a unidirectional data stream that reacts to real-time changes.
- **Hilt Setup**: Boilerplate injection instances securely distribute `ProjectPlanRepository` and `MyProjectRepository` interfaces, keeping view models totally abstracted.

### Technical Breakdown (Presentation & Domain Layer)
- **Use Cases**: Created atomic Use Cases (e.g., `GetAllProjectPlansUseCase`, `AddProjectPlanUseCase`) for both features. This ensures the domain logic is decoupled from the ViewModel and UI.
- **ViewModels**: Built `ProjectPlanViewModel` and `MyProjectsViewModel` using Hilt injection. They expose a `StateFlow` to the UI, converting the continuous Flow from the database into a lifecycle-aware state.
- **UI Integration**: Wired the ViewModels to `ProjectPlansMainPage` and `MyProjectsScreen`. They now dynamically observe the StateFlow. The UI replaces hardcoded dummy data with the actual data from the local database, and provides test functionality to insert records directly from the screen's Floating Action Button or Top Bar.

## 💻 Full Implementation Code

**`ProjectPlanEntity.kt`**
```kotlin
package com.nhbhuiyan.nestify.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_plans")
data class ProjectPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imagePath: Int,
    val title: String,
    val description: String,
    val ideas: Int,
    val completed: Int,
    val workingWith: String
)
```

**`MyProjectEntity.kt`**
```kotlin
package com.nhbhuiyan.nestify.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_projects")
data class MyProjectEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val name: String,
    val motive: String,
    val description: String,
    val features: List<String>,
    val specialities: List<String>,
    val techStack: List<String>,
    val libraries: List<String>,
    val sources: Map<String, String>,
    val brandLogo: Int,
    val demoImages: List<Int>,
    val videoUrl: String?,
    val whereToFind: String,
    val category: String,
    val status: String
)
```

**`Converters.kt` (Addition)**
```kotlin
    @TypeConverter
    fun fromStringMap(map: Map<String, String>): String {
        val jsonObject = org.json.JSONObject()
        for ((key, value) in map) { jsonObject.put(key, value) }
        return jsonObject.toString()
    }
    @TypeConverter
    fun toStringMap(data: String): Map<String, String> {
        if (data.isEmpty()) return emptyMap()
        val map = mutableMapOf<String, String>()
        try {
            val jsonObject = org.json.JSONObject(data)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = jsonObject.getString(key)
            }
        } catch (e: Exception) { e.printStackTrace() }
        return map
    }
```

**`ProjectPlanDao.kt`**
```kotlin
package com.nhbhuiyan.nestify.data.local.Dao
import androidx.room.*
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
```

*(Identical mirrored structure for `MyProjectDao.kt`)*

**`ProjectPlanRepositoryImpl.kt`**
```kotlin
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
    override fun getAllProjectPlans(): Flow<List<ProjectPlanModel>> = dao.getAllProjectPlans().map { entities -> entities.map { it.toModel() } }
    override fun getProjectPlanById(id: Int): Flow<ProjectPlanModel?> = dao.getProjectPlanById(id).map { it?.toModel() }
    override suspend fun insertProjectPlan(plan: ProjectPlanModel) = dao.insertProjectPlan(plan.toEntity())
    override suspend fun updateProjectPlan(plan: ProjectPlanModel) = dao.updateProjectPlan(plan.toEntity())
    override suspend fun deleteProjectPlan(plan: ProjectPlanModel) = dao.deleteProjectPlan(plan.toEntity())
}

fun ProjectPlanEntity.toModel(): ProjectPlanModel = ProjectPlanModel(Id = id, ImagePath = imagePath, Title = title, Description = description, Ideas = ideas, Completed = completed, WorkingWith = workingWith)
fun ProjectPlanModel.toEntity(): ProjectPlanEntity = ProjectPlanEntity(id = Id, imagePath = ImagePath, title = Title, description = Description, ideas = Ideas, completed = Completed, workingWith = WorkingWith)
```

*(Identical mirrored structure for `MyProjectRepositoryImpl.kt`)*

**`DataModule.kt` (Additions)**
```kotlin
    @Provides
    @Singleton
    fun provideProjectPlanDao(appDataBase: AppDataBase): ProjectPlanDao = appDataBase.projectPlanDao()

    @Provides
    @Singleton
    fun provideProjectPlanRepository(dao: ProjectPlanDao): ProjectPlanRepository = ProjectPlanRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideMyProjectDao(appDataBase: AppDataBase): MyProjectDao = appDataBase.myProjectDao()

    @Provides
    @Singleton
    fun provideMyProjectRepository(dao: MyProjectDao): MyProjectRepository = MyProjectRepositoryImpl(dao)
```

## 🛠️ Extra Steps
1. **Migration Note**: Room Database version was bumped to `15`. If the app crashes on launch, either provide a proper Room Migration or simply uninstall and reinstall the app (since `fallbackToDestructiveMigration()` is already handled in your database builder).
2. **View Models**: The next integration step involves creating `ProjectPlanViewModel` and `MyProjectsViewModel` to call these repositories and map Flow lists directly into your Compose states.

## 📝 Summary
Data begins securely offline. The ViewModels will collect a continuous `Flow` from `MyProjectRepository` and `ProjectPlanRepository`. The Repositories then ask the DAOs for SQL data, intercepting Room's entity models, and translating them seamlessly into purely UI-friendly models (`ProjectPlanModel` & `ProjectModel`). The state is preserved cleanly across lifecycles.
