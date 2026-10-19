package com.example.aurasugi.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aurasugi.data.model.ApiKey
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiKeyDao {
    @Query("SELECT `key` FROM apiKey WHERE id = 1")
    fun getApiKey(): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateApiKey(apiKey: ApiKey)
}
