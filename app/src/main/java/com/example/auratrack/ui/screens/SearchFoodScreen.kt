package com.example.aurasugi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackCard
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.SoftText
import com.example.aurasugi.data.model.SavedItem
import com.example.aurasugi.ui.MainViewModel
import com.example.aurasugi.ui.components.EditSavedItemDialog
import com.example.aurasugi.ui.components.SavedFoodItemCard
import com.example.aurasugi.ui.components.UsdaFoodItemCard
import com.example.aurasugi.ui.components.UsdaPurple

@Composable
fun SearchFoodScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val savedItems by viewModel.savedItems.collectAsState()
    val isUsdaApiEnabled by viewModel.isUsdaApiEnabled.collectAsState()
    val usdaSearchState by viewModel.usdaSearchState.collectAsState()

    var editingSavedItem by remember { mutableStateOf<SavedItem?>(null) }

    // Clear USDA API search results when exiting search screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearUsdaSearchResults()
        }
    }

    editingSavedItem?.let { item ->
        EditSavedItemDialog(
            item = item,
            onDismiss = { editingSavedItem = null },
            onSave = { updated ->
                viewModel.insertSavedItem(updated)
                editingSavedItem = null
            }
        )
    }

    // Filter local DB items on text change
    val filteredSavedItems = savedItems.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Foods") },
                placeholder = { Text(if (isUsdaApiEnabled) "Search saved foods or tap Search USDA..." else "Search saved foods...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = NeoGreen)
                },
                trailingIcon = {
                    if (usdaSearchState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = UsdaPurple
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (isUsdaApiEnabled && searchQuery.isNotBlank()) {
                            viewModel.searchUsdaFoods(searchQuery)
                        }
                    }
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            if (isUsdaApiEnabled) {
                Button(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.searchUsdaFoods(searchQuery)
                        }
                    },
                    enabled = searchQuery.isNotBlank() && !usdaSearchState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UsdaPurple,
                        contentColor = Color.Black,
                        disabledContainerColor = GreyishBlackCard,
                        disabledContentColor = SoftText.copy(alpha = 0.4f)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp)
                ) {
                    Icon(Icons.Default.Api, contentDescription = "Search USDA", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("USDA", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // API Status Info Banner
        if (isUsdaApiEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "USDA Online Search Ready",
                    style = MaterialTheme.typography.labelMedium,
                    color = UsdaPurple
                )
                Text(
                    text = if (usdaSearchState.results.isNotEmpty()) "${usdaSearchState.results.size} USDA results" else "Tap 'USDA' to query API",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftText.copy(alpha = 0.6f)
                )
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = GreyishBlackCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "USDA API is currently disabled.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftText.copy(alpha = 0.7f)
                    )
                    TextButton(
                        onClick = { viewModel.setUsdaApiEnabled(true) },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Api, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Enable API", color = UsdaPurple, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Search Results List
        if (filteredSavedItems.isEmpty() && usdaSearchState.results.isEmpty() && !usdaSearchState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when {
                            usdaSearchState.errorMessage != null -> usdaSearchState.errorMessage ?: "Search error"
                            searchQuery.isBlank() -> "Type a food name above to search."
                            isUsdaApiEnabled -> "No local saved items found. Tap 'USDA' button to query online database."
                            else -> "No saved items found."
                        },
                        color = if (usdaSearchState.errorMessage != null) MaterialTheme.colorScheme.error else SoftText.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Local Saved Items Section First
                if (filteredSavedItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "Saved Items",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeoGreen,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }

                    items(filteredSavedItems, key = { "saved_${it.id}" }) { item ->
                        SavedFoodItemCard(
                            item = item,
                            onLogClick = {
                                viewModel.logSavedItemToToday(item)
                                Toast.makeText(context, "Logged ${item.name} to Today", Toast.LENGTH_SHORT).show()
                            },
                            onEditClick = { editingSavedItem = item },
                            onDeleteClick = {
                                viewModel.deleteSavedItem(item)
                            }
                        )
                    }
                }

                // USDA API Results Section Second
                if (isUsdaApiEnabled && usdaSearchState.results.isNotEmpty()) {
                    item {
                        Text(
                            text = "USDA Database Results",
                            style = MaterialTheme.typography.titleSmall,
                            color = UsdaPurple,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(usdaSearchState.results, key = { "usda_${it.fdcId}" }) { usdaItem ->
                        UsdaFoodItemCard(
                            item = usdaItem,
                            onLogClick = {
                                viewModel.logUsdaItemToToday(usdaItem)
                                Toast.makeText(context, "Logged ${usdaItem.name} to Today", Toast.LENGTH_SHORT).show()
                            },
                            onSaveClick = {
                                viewModel.saveUsdaItemToSavedItems(usdaItem)
                                Toast.makeText(context, "Saved ${usdaItem.name} to Saved Items", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}
