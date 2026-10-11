package com.example.aurasugi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aurasugi.data.api.UsdaApiClient
import com.example.aurasugi.data.model.DailySummary
import com.example.aurasugi.data.model.FoodEntry
import com.example.aurasugi.data.model.SavedItem
import com.example.aurasugi.data.model.UsdaFoodItem
import com.example.aurasugi.data.model.UsdaSearchResultsHolder
import com.example.aurasugi.data.model.UsdaSearchState
import com.example.aurasugi.data.preferences.UsdaPreferences
import com.example.aurasugi.data.repository.AppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: AppRepository,
    private val usdaPreferences: UsdaPreferences? = null,
    private val usdaApiClient: UsdaApiClient = UsdaApiClient()
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // USDA API Settings state
    private val _isUsdaApiEnabled = MutableStateFlow(usdaPreferences?.isUsdaApiEnabled ?: false)
    val isUsdaApiEnabled: StateFlow<Boolean> = _isUsdaApiEnabled.asStateFlow()

    private val _usdaApiKey = MutableStateFlow(usdaPreferences?.usdaApiKey ?: "")
    val usdaApiKey: StateFlow<String> = _usdaApiKey.asStateFlow()

    // USDA Search Results Holder object
    private val usdaSearchResultsHolder = UsdaSearchResultsHolder()
    val usdaSearchState: StateFlow<UsdaSearchState> = usdaSearchResultsHolder.state

    private var searchJob: Job? = null

    val dailySummary: StateFlow<DailySummary> = _selectedDate
        .flatMapLatest { date ->
            repository.getDailySummaryForDate(date.toString())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailySummary()
        )

    val entriesForSelectedDate: StateFlow<List<FoodEntry>> = _selectedDate
        .flatMapLatest { date ->
            repository.getEntriesForDate(date.toString())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allEntries: StateFlow<List<FoodEntry>> = repository.getAllEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedItems: StateFlow<List<SavedItem>> = repository.getAllSavedItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun previousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun nextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    fun setUsdaApiEnabled(enabled: Boolean) {
        _isUsdaApiEnabled.value = enabled
        usdaPreferences?.isUsdaApiEnabled = enabled
        if (!enabled) {
            usdaSearchResultsHolder.clear()
        }
    }

    fun setUsdaApiKey(key: String) {
        _usdaApiKey.value = key
        usdaPreferences?.usdaApiKey = key
    }

    fun searchUsdaFoods(query: String) {
        searchJob?.cancel()
        val trimmed = query.trim()

        if (!_isUsdaApiEnabled.value) {
            usdaSearchResultsHolder.clear()
            return
        }

        if (trimmed.length < 2) {
            usdaSearchResultsHolder.clear()
            return
        }

        searchJob = viewModelScope.launch {
            val apiKey = _usdaApiKey.value.ifBlank { "DEMO_KEY" }
            usdaSearchResultsHolder.setLoading(trimmed)

            val result = usdaApiClient.searchFoods(trimmed, apiKey)
            result.fold(
                onSuccess = { items ->
                    usdaSearchResultsHolder.setResults(trimmed, items)
                },
                onFailure = { error ->
                    usdaSearchResultsHolder.setError(trimmed, error.localizedMessage ?: "Failed to fetch USDA foods")
                }
            )
        }
    }

    fun clearUsdaSearchResults() {
        searchJob?.cancel()
        usdaSearchResultsHolder.clear()
    }

    fun logUsdaItemToToday(item: UsdaFoodItem) {
        viewModelScope.launch {
            val entry = FoodEntry(
                name = item.name,
                calories = item.calories,
                protein = item.protein,
                carbs = item.carbs,
                fat = item.fat,
                fiber = item.fiber,
                portion = item.portion,
                date = LocalDate.now().toString()
            )
            repository.insertEntry(entry)
        }
    }

    fun saveUsdaItemToSavedItems(item: UsdaFoodItem) {
        viewModelScope.launch {
            val savedItem = SavedItem(
                name = item.name,
                calories = item.calories,
                protein = item.protein,
                carbs = item.carbs,
                fat = item.fat,
                fiber = item.fiber,
                portion = item.portion
            )
            repository.insertSavedItem(savedItem)
        }
    }

    fun testUsdaApiConnection(apiKey: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val keyToTest = apiKey.ifBlank { "DEMO_KEY" }
            val result = usdaApiClient.searchFoods("apple", keyToTest, pageSize = 1)
            result.fold(
                onSuccess = {
                    onResult(true, "Successfully connected to USDA API!")
                },
                onFailure = { error ->
                    onResult(false, error.localizedMessage ?: "Connection failed.")
                }
            )
        }
    }

    fun insertEntry(entry: FoodEntry) {
        viewModelScope.launch {
            repository.insertEntry(entry)
        }
    }

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun logEntryToToday(entry: FoodEntry) {
        viewModelScope.launch {
            val newEntry = entry.copy(
                id = java.util.UUID.randomUUID().toString(),
                date = LocalDate.now().toString()
            )
            repository.insertEntry(newEntry)
        }
    }

    fun insertSavedItem(item: SavedItem) {
        viewModelScope.launch {
            repository.insertSavedItem(item)
        }
    }

    fun deleteSavedItem(item: SavedItem) {
        viewModelScope.launch {
            repository.deleteSavedItem(item)
        }
    }

    fun logSavedItemToToday(item: SavedItem) {
        viewModelScope.launch {
            val entry = FoodEntry(
                name = item.name,
                calories = item.calories,
                protein = item.protein,
                carbs = item.carbs,
                fat = item.fat,
                fiber = item.fiber,
                portion = item.portion,
                date = LocalDate.now().toString()
            )
            repository.insertEntry(entry)
        }
    }
}

class MainViewModelFactory(
    private val repository: AppRepository,
    private val usdaPreferences: UsdaPreferences? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, usdaPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
