package com.example.aurasugi.data.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UsdaSearchState(
    val results: List<UsdaFoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val query: String = ""
)

class UsdaSearchResultsHolder {
    private val _state = MutableStateFlow(UsdaSearchState())
    val state: StateFlow<UsdaSearchState> = _state.asStateFlow()

    fun setLoading(query: String) {
        _state.value = UsdaSearchState(
            query = query,
            isLoading = true,
            results = _state.value.results,
            errorMessage = null
        )
    }

    fun setResults(query: String, results: List<UsdaFoodItem>) {
        _state.value = UsdaSearchState(
            query = query,
            isLoading = false,
            results = results,
            errorMessage = null
        )
    }

    fun setError(query: String, message: String) {
        _state.value = UsdaSearchState(
            query = query,
            isLoading = false,
            results = emptyList(),
            errorMessage = message
        )
    }

    fun clear() {
        _state.value = UsdaSearchState()
    }
}
