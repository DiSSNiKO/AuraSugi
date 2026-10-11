package com.example.aurasugi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackCard
import com.example.aurasugi.GreyishBlackSurface
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.NeonPink
import com.example.aurasugi.SoftText
import com.example.aurasugi.data.model.FoodEntry
import com.example.aurasugi.ui.MainViewModel
import com.example.aurasugi.ui.components.EditFoodEntryDialog
import com.example.aurasugi.ui.components.LoggedFoodEntryCard
import com.example.aurasugi.ui.components.neonGlow

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val dailySummary by viewModel.dailySummary.collectAsState()
    val entries by viewModel.entriesForSelectedDate.collectAsState()

    val dateString = selectedDate.toString()
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousDay() }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = NeoGreen)
            }
            Text(text = dateString, style = MaterialTheme.typography.titleLarge, color = SoftText)
            IconButton(onClick = { viewModel.nextDay() }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = NeoGreen)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .neonGlow(
                    color = NeoGreen,
                    alpha = 0.2f
                ),
            colors = CardDefaults.cardColors(containerColor = GreyishBlackCard)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Daily Summary", style = MaterialTheme.typography.titleMedium, color = NeoGreen)
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(110.dp),
                        color = NeonPink,
                        trackColor = GreyishBlackSurface,
                        strokeWidth = 6.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${dailySummary.totalCalories}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NeonPink
                        )
                        Text(
                            text = "kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = GreyishBlackSurface)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Protein", style = MaterialTheme.typography.bodySmall, color = SoftText.copy(alpha = 0.7f))
                        Text(text = "${dailySummary.totalProtein}g", style = MaterialTheme.typography.titleMedium, color = NeoGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Carbs", style = MaterialTheme.typography.bodySmall, color = SoftText.copy(alpha = 0.7f))
                        Text(text = "${dailySummary.totalCarbs}g", style = MaterialTheme.typography.titleMedium, color = SoftText)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Fat", style = MaterialTheme.typography.bodySmall, color = SoftText.copy(alpha = 0.7f))
                        Text(text = "${dailySummary.totalFat}g", style = MaterialTheme.typography.titleMedium, color = SoftText)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Fiber", style = MaterialTheme.typography.bodySmall, color = SoftText.copy(alpha = 0.7f))
                        Text(text = "${dailySummary.totalFiber}g", style = MaterialTheme.typography.titleMedium, color = SoftText)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Entries for $dateString",
            style = MaterialTheme.typography.titleSmall,
            color = NeoGreen,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No food entries for this date.", color = SoftText.copy(alpha = 0.6f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { item ->
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
