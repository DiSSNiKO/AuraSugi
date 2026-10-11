package com.example.aurasugi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savedItems")
data class SavedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val portion: Int = 0,
    val calories: Int = 0,
    val protein: Int = 0,
    val fiber: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0
)
