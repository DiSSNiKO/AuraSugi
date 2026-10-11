package com.example.aurasugi.data.preferences

import android.content.Context
import android.content.SharedPreferences

class UsdaPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("usda_prefs", Context.MODE_PRIVATE)

    var isUsdaApiEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var usdaApiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value).apply()

    companion object {
        private const val KEY_ENABLED = "usda_api_enabled"
        private const val KEY_API_KEY = "usda_api_key"
    }
}
