package com.example.aurasugi.data.model

data class UsdaFoodItem(
    val fdcId: Long,
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int,
    val portion: Int = 100,
    val dataType: String = ""
)
