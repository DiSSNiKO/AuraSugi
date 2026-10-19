package com.example.aurasugi.data.repository

import com.example.aurasugi.data.db.ApiKeyDao
import com.example.aurasugi.data.db.EntryDao
import com.example.aurasugi.data.db.SavedItemsDao
import com.example.aurasugi.data.model.ApiKey
import com.example.aurasugi.data.model.DailySummary
import com.example.aurasugi.data.model.FoodEntry
import com.example.aurasugi.data.model.SavedItem
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val entryDao: EntryDao,
    private val savedItemsDao: SavedItemsDao,
    private val apiKeyDao: ApiKeyDao? = null
) {
    fun getEntriesForDate(date: String): Flow<List<FoodEntry>> =
        entryDao.getEntriesForDate(date)

    fun getAllEntries(): Flow<List<FoodEntry>> =
        entryDao.getAllEntries()

    fun getDailySummaryForDate(date: String): Flow<DailySummary> =
        entryDao.getDailySummaryForDate(date)

    fun getAllSavedItems(): Flow<List<SavedItem>> =
        savedItemsDao.getAllSavedItems()

    fun getApiKey(): Flow<String?>? =
        apiKeyDao?.getApiKey()

    suspend fun insertEntry(entry: FoodEntry) {
        entryDao.insertEntry(entry)
    }

    suspend fun deleteEntry(entry: FoodEntry) {
        entryDao.deleteEntry(entry)
    }

    suspend fun insertSavedItem(item: SavedItem) {
        savedItemsDao.insertSavedItem(item)
    }

    suspend fun deleteSavedItem(item: SavedItem) {
        savedItemsDao.deleteSavedItem(item)
    }

    suspend fun setApiKey(key: String) {
        apiKeyDao?.insertOrUpdateApiKey(ApiKey(key = key))
    }
}
