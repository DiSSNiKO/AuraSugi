package com.example.aurasugi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "foodEntry")
data class FoodEntry(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val protein: Int = 0,
    val calories: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val fiber: Int = 0,
    val portion: Int = 0,
    val date: String = LocalDate.now().toString()
)
