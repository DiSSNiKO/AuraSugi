package com.example.aurasugi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.SoftText
import com.example.aurasugi.data.model.FoodEntry
import com.example.aurasugi.ui.MainViewModel
import com.example.aurasugi.ui.components.EditFoodEntryDialog
import com.example.aurasugi.ui.components.LoggedFoodEntryCard

@Composable
fun HistoryScreen(viewModel: MainViewModel) {
    val entries by viewModel.allEntries.collectAsState()

    var editingEntry by remember { mutableStateOf<FoodEntry?>(null) }

    editingEntry?.let { entry ->
        EditFoodEntryDialog(
            entry = entry,
            onDismiss = { editingEntry = null },
            onSave = { updated ->
                viewModel.insertEntry(updated)
                editingEntry = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Food Log History",
            style = MaterialTheme.typography.titleLarge,
            color = NeoGreen
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No log history found.", color = SoftText.copy(alpha = 0.6f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { item ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = item.date,
                            style = MaterialTheme.typography.labelMedium,
                            color = NeoGreen,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        LoggedFoodEntryCard(
                            entry = item,
                            onLogToTodayClick = {
                                viewModel.logEntryToToday(item)
                            },
                            onEditClick = { editingEntry = item },
                            onDeleteClick = {
                                viewModel.deleteEntry(item)
                            }
                        )
                    }
                }
            }
        }
    }
}
