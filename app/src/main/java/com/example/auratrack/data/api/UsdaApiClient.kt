package com.example.aurasugi.data.api

import com.example.aurasugi.data.model.UsdaFoodItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class UsdaApiClient {
    private val nonAlphaNumRegex = Regex("[^a-z0-9]")

    suspend fun searchFoods(
        query: String,
        apiKey: String,
        pageSize: Int = 200,
        pageNumber: Int = 1
    ): Result<List<UsdaFoodItem>> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanQuery = query.trim()
            val cleanKey = apiKey.trim()

            if (cleanQuery.isBlank()) return@runCatching emptyList()
            if (cleanKey.isBlank()) throw IllegalArgumentException("API key is required.")

            val encodedQuery = URLEncoder.encode(cleanQuery, "UTF-8")
            val encodedKey = URLEncoder.encode(cleanKey, "UTF-8")
            val urlString = "https://api.nal.usda.gov/fdc/v1/foods/search?" +
                    "query=$encodedQuery&" +
                    "dataType=Foundation,SR%20Legacy,Branded&" +
                    "pageSize=$pageSize&" +
                    "pageNumber=$pageNumber&" +
                    "api_key=$encodedKey"

            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("accept", "*/*")
                connectTimeout = 10000
                readTimeout = 10000
            }

            val responseCode = connection.responseCode
            val responseStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                val errText = connection.errorStream?.bufferedReader()?.use { it.readText() }
                if (responseCode == 403 || responseCode == 401) {
                    throw IllegalStateException("Invalid USDA API Key. Please verify your API key in USDA settings.")
                } else {
                    throw IllegalStateException("USDA API Error ($responseCode): ${errText ?: "Unknown error"}")
                }
            }

            val jsonText = responseStream.bufferedReader().use { it.readText() }
            parseSearchResponse(jsonText, cleanQuery)
        }
    }

    private fun parseSearchResponse(jsonString: String, rawQuery: String): List<UsdaFoodItem> {
        val root = JSONObject(jsonString)
        val foodsArray = root.optJSONArray("foods") ?: return emptyList()
        val items = mutableListOf<UsdaFoodItem>()

        for (i in 0 until foodsArray.length()) {
            val foodObj = foodsArray.getJSONObject(i)
            val fdcId = foodObj.optLong("fdcId", 0L)
            var description = foodObj.optString("description", "Unknown Food")
            val brandName = foodObj.optString("brandName", "")
            val brandOwner = foodObj.optString("brandOwner", "")
            val dataType = foodObj.optString("dataType", "")

            if (brandName.isNotBlank() && !description.startsWith(brandName, ignoreCase = true)) {
                description = "$brandName - $description"
            } else if (brandOwner.isNotBlank() && !description.startsWith(brandOwner, ignoreCase = true)) {
                description = "$brandOwner - $description"
            }

            var calories = 0
            var protein = 0
            var carbs = 0
            var fat = 0
            var fiber = 0
            var servingSize = 100

            if (foodObj.has("servingSize")) {
                val size = foodObj.optDouble("servingSize", 100.0)
                if (size > 0) servingSize = size.toInt()
            }

            val nutrientsArray = foodObj.optJSONArray("foodNutrients")
            if (nutrientsArray != null) {
                for (j in 0 until nutrientsArray.length()) {
                    val nutrientObj = nutrientsArray.getJSONObject(j)
                    val nutrientId = nutrientObj.optInt("nutrientId", 0)
                    val nutrientName = nutrientObj.optString("nutrientName", "").lowercase()
                    val unitName = nutrientObj.optString("unitName", "").uppercase()
                    val value = nutrientObj.optDouble("value", 0.0)

                    // Energy / Calories: ID 1008 or 2047 or name contains energy
                    if (nutrientId == 1008 || nutrientId == 2047 || (nutrientName.contains("energy") && unitName == "KCAL")) {
                        calories = value.toInt()
                    }
                    // Protein: ID 1003 or name == protein
                    else if (nutrientId == 1003 || nutrientName == "protein") {
                        protein = value.toInt()
                    }
                    // Carbohydrate: ID 1005 or contains carbohydrate
                    else if (nutrientId == 1005 || nutrientName.contains("carbohydrate")) {
                        carbs = value.toInt()
                    }
                    // Total lipid (fat): ID 1004 or contains lipid / fat
                    else if (nutrientId == 1004 || nutrientName.contains("lipid") || nutrientName.contains("fat")) {
                        fat = value.toInt()
                    }
                    // Fiber: ID 1079 or 291 or contains fiber
                    else if (nutrientId == 1079 || nutrientId == 291 || nutrientName.contains("fiber")) {
                        fiber = value.toInt()
                    }
                }
            }

            // Fallback for calories if 0 and macros exist
            if (calories == 0 && (protein > 0 || carbs > 0 || fat > 0)) {
                calories = (protein * 4) + (carbs * 4) + (fat * 9)
            }

            items.add(
                UsdaFoodItem(
                    fdcId = fdcId,
                    name = description,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    fiber = fiber,
                    portion = servingSize,
                    dataType = dataType
                )
            )
        }

        return sanitizeAndRankResults(items, rawQuery)
    }


    private fun sanitizeAndRankResults(rawItems: List<UsdaFoodItem>, rawQuery: String): List<UsdaFoodItem> {
        if (rawItems.isEmpty()) return emptyList()

        // 1. Deduplicate identical food items using the pre-compiled regex
        val deduplicated = rawItems.distinctBy { item ->
            val normName = item.name.lowercase().replace(nonAlphaNumRegex, "")
            "${normName}_${item.calories}_${item.portion}"
        }

        val queryTrimmed = rawQuery.trim().lowercase()
        val queryTokens = queryTrimmed.split(Regex("\\s+")).filter { it.isNotBlank() }

        if (queryTokens.isEmpty()) return deduplicated

        // 2. Perform secondary query re-ranking
        val scoredItems = deduplicated.map { item ->
            val itemNameLower = item.name.lowercase()
            var score = 0.0

            val matchedTokenCount = queryTokens.count { token -> itemNameLower.contains(token) }
            val matchRatio = matchedTokenCount.toDouble() / queryTokens.size

            if (matchedTokenCount == queryTokens.size) {
                score += 1000.0
            } else {
                score += matchRatio * 400.0
            }

            if (itemNameLower == queryTrimmed) {
                score += 1500.0
            } else if (itemNameLower.startsWith(queryTrimmed)) {
                score += 800.0
            } else if (itemNameLower.contains(queryTrimmed)) {
                score += 600.0
            }

            var lastIdx = -1
            var inOrder = true
            for (token in queryTokens) {
                val idx = itemNameLower.indexOf(token)
                if (idx == -1 || idx < lastIdx) {
                    inOrder = false
                    break
                }
                lastIdx = idx
            }
            if (inOrder && queryTokens.size > 1) {
                score += 250.0
            }

            // Fixed the missing minus sign here:
            val lengthDiff = (itemNameLower.length - queryTrimmed.length).coerceAtLeast(0)
            score -= (lengthDiff * 0.3)

            if (item.calories > 0 || item.protein > 0 || item.carbs > 0 || item.fat > 0) {
                score += 20.0
            }

            item to score
        }

        return scoredItems.sortedByDescending { it.second }.map { it.first }
    }
}
