package com.nhbhuiyan.nestify.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nhbhuiyan.nestify.data.local.entity.LibraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibraryItem(item: LibraryItemEntity): Long

    @Update
    suspend fun updateLibraryItem(item: LibraryItemEntity)

    @Delete
    suspend fun deleteLibraryItem(item: LibraryItemEntity)

    @Query("SELECT * FROM library_item_table ORDER BY dateAdded DESC")
    fun getAllLibraryItems(): Flow<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_item_table WHERE status = :status ORDER BY dateAdded DESC")
    fun getLibraryItemsByStatus(status: com.nhbhuiyan.nestify.data.local.entity.LibraryItemStatus): Flow<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_item_table WHERE id = :id")
    suspend fun getLibraryItemById(id: Long): LibraryItemEntity?
}
