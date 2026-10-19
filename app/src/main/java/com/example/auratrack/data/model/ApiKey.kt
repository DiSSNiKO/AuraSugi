package com.example.aurasugi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apiKey")
data class ApiKey(
    @PrimaryKey val id: Int = 1,
    val key: String
)
