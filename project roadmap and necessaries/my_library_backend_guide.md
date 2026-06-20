# My Library Section Backend Guide

## 🗺️ High-Level Roadmap
1. **Leverage Existing Data Layer**: Reused the pre-existing `LibraryItemEntity`, `LibraryItemDao`, and `LibraryRepositoryImpl` connected to Room DB.
2. **Domain Use Cases**: Created atomic Use Cases (`GetAllLibraryItemsUseCase`, `GetLibraryItemsByStatusUseCase`, `AddLibraryItemUseCase`, `DeleteLibraryItemUseCase`) to encapsulate business operations.
3. **Presentation ViewModel**: Built `LibraryViewModel` annotated with `@HiltViewModel` to consume the Use Cases and expose state via `StateFlow`.
4. **UI Integration**: Injected `LibraryViewModel` into `LibraryScreen.kt`. Upgraded the screen to dynamically react to local database changes instead of showing static placeholder cards.
5. **Interactive Testing**: Wired the screen's Floating Action Button (FAB) to insert dummy books directly into the local database, allowing instant visual feedback via the reactive streams.

## 🧠 Logical Descriptions

### Simple View
We built the complete "brain" behind the My Library screen. When you open the screen, it instantly reads your saved books and articles from the phone's internal storage and sorts them. When you hit the "+" button, it automatically adds a new book to your collection and the screen updates in real-time, just like a professional app.

### Technical Breakdown (Backend & Domain Layers)
- **Data Layer (Existing)**: The foundation is Room. `LibraryItemEntity` handles database schemas while `LibraryItemDao` runs SQL queries asynchronously.
- **Domain Layer**: Clean Architecture Use Cases decouple the UI from the database logic. Each Use Case has a single responsibility (e.g., fetching all items or adding one).
- **Presentation Layer**: The `LibraryViewModel` collects data from the Use Cases and manages a unidirectional `StateFlow`. It acts as the ultimate truth for the `LibraryScreen`.
- **Reactive UI**: The Compose screen observes `viewModel.libraryItems.collectAsState()`. It filters the `READING` items for the featured section and maps the rest to the catalog grid.

## 💻 Full Implementation Code

**`LibraryUseCases.kt`**
```kotlin
package com.nhbhuiyan.nestify.domain.usecases.LibraryUseCases
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemStatus
import com.nhbhuiyan.nestify.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLibraryItemsUseCase @Inject constructor(private val repository: LibraryRepository) {
    operator fun invoke(): Flow<List<LibraryItemEntity>> = repository.getAllLibraryItems()
}
class GetLibraryItemsByStatusUseCase @Inject constructor(private val repository: LibraryRepository) {
    operator fun invoke(status: LibraryItemStatus): Flow<List<LibraryItemEntity>> = repository.getLibraryItemsByStatus(status)
}
class AddLibraryItemUseCase @Inject constructor(private val repository: LibraryRepository) {
    suspend operator fun invoke(item: LibraryItemEntity) = repository.insertLibraryItem(item)
}
class DeleteLibraryItemUseCase @Inject constructor(private val repository: LibraryRepository) {
    suspend operator fun invoke(item: LibraryItemEntity) = repository.deleteLibraryItem(item)
}
```

**`LibraryViewModel.kt`**
```kotlin
package com.nhbhuiyan.nestify.presentation.ui.screens.LibraryScreen.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import com.nhbhuiyan.nestify.domain.usecases.LibraryUseCases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAllLibraryItemsUseCase: GetAllLibraryItemsUseCase,
    private val addLibraryItemUseCase: AddLibraryItemUseCase,
    private val deleteLibraryItemUseCase: DeleteLibraryItemUseCase
) : ViewModel() {
    private val _libraryItems = MutableStateFlow<List<LibraryItemEntity>>(emptyList())
    val libraryItems: StateFlow<List<LibraryItemEntity>> = _libraryItems.asStateFlow()

    init { loadItems() }

    private fun loadItems() {
        viewModelScope.launch {
            getAllLibraryItemsUseCase().collect { items -> _libraryItems.value = items }
        }
    }
    fun addItem(item: LibraryItemEntity) { viewModelScope.launch { addLibraryItemUseCase(item) } }
    fun deleteItem(item: LibraryItemEntity) { viewModelScope.launch { deleteLibraryItemUseCase(item) } }
}
```

## 🛠️ Extra Steps
1. **Data Source Integration**: The local database implementation for `LibraryRepositoryImpl` and `LibraryItemDao` was already pre-configured in `AppDataBase` and `DataModule`. We simply capitalized on it by building the upper layers.
2. **UI Scaling**: Future iterations can include pagination or more dynamic filtering options directly triggered through the ViewModel using the `GetLibraryItemsByStatusUseCase`.

## 📝 Summary
Data flows strictly from the UI event (like clicking the "Add" FAB) -> ViewModel -> Use Case -> Repository -> DAO -> Room Database. Once the database updates, the `Flow` instantly emits the new list up the same chain, automatically recomposing the Compose UI without any manual UI refreshes.
