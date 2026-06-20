package com.nhbhuiyan.nestify.data.local.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nhbhuiyan.nestify.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaEntity): Long

    @Update
    suspend fun updateMedia(media: MediaEntity)

    @Delete
    suspend fun deleteMedia(media: MediaEntity)

    @Query("SELECT * FROM media_table ORDER BY dateAdded DESC")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_table WHERE galleryCategory = :category ORDER BY dateAdded DESC")
    fun getMediaByCategory(category: com.nhbhuiyan.nestify.data.local.entity.GalleryCategory): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_table WHERE id = :id")
    suspend fun getMediaById(id: Long): MediaEntity?
}
