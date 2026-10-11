package com.example.aurasugi.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aurasugi.data.model.DailySummary
import com.example.aurasugi.data.model.FoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Query("SELECT * FROM foodEntry WHERE date = :date ORDER BY id DESC")
    fun getEntriesForDate(date: String): Flow<List<FoodEntry>>

    @Query("SELECT * FROM foodEntry ORDER BY date DESC, id DESC")
    fun getAllEntries(): Flow<List<FoodEntry>>

    @Query("""
        SELECT 
            COALESCE(SUM(calories), 0) AS totalCalories,
            COALESCE(SUM(protein), 0) AS totalProtein,
            COALESCE(SUM(carbs), 0) AS totalCarbs,
            COALESCE(SUM(fat), 0) AS totalFat,
            COALESCE(SUM(fiber), 0) AS totalFiber
        FROM foodEntry WHERE date = :date
    """)
    fun getDailySummaryForDate(date: String): Flow<DailySummary>

    @Query("SELECT SUM(calories) FROM foodEntry WHERE date = :date")
    fun getTotalCaloriesForDate(date: String): Flow<Int?>

    @Query("SELECT SUM(protein) FROM foodEntry WHERE date = :date")
    fun getTotalProteinForDate(date: String): Flow<Int?>

    @Query("SELECT SUM(carbs) FROM foodEntry WHERE date = :date")
    fun getTotalCarbsForDate(date: String): Flow<Int?>

    @Query("SELECT SUM(fat) FROM foodEntry WHERE date = :date")
    fun getTotalFatForDate(date: String): Flow<Int?>

    @Query("SELECT SUM(fiber) FROM foodEntry WHERE date = :date")
    fun getTotalFiberForDate(date: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntry)

    @Delete
    suspend fun deleteEntry(entry: FoodEntry)
}
