package com.example.aurasugi.data.db

import androidx.room.*
import com.example.aurasugi.data.model.SavedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedItemsDao {
    @Query("SELECT * FROM savedItems ORDER BY name ASC")
    fun getAllSavedItems(): Flow<List<SavedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedItem(food: SavedItem)

    @Delete
    suspend fun deleteSavedItem(food: SavedItem)
}
