package com.example.aurasugi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackCard
import com.example.aurasugi.GreyishBlackSurface
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.SoftText
import com.example.aurasugi.calculateCalories
import com.example.aurasugi.data.model.SavedItem
import com.example.aurasugi.ui.MainViewModel
import com.example.aurasugi.ui.components.EditSavedItemDialog
import com.example.aurasugi.ui.components.SavedFoodItemCard
import com.example.aurasugi.ui.components.neonGlow

@Composable
fun EntryRecordScreen(viewModel: MainViewModel) {
    var foodName by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var fiber by remember { mutableStateOf("") }
    var portionSize by remember { mutableStateOf("") }

    val p = protein.toIntOrNull() ?: 0
    val c = carbs.toIntOrNull() ?: 0
    val f = fat.toIntOrNull() ?: 0
    val computedCalories = calculateCalories(p, c, f)

    val savedItems by viewModel.savedItems.collectAsState()
    var editingSavedItem by remember { mutableStateOf<SavedItem?>(null) }

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

    val isFormValid = foodName.isNotBlank()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
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
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Create Custom Saved Item",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeoGreen
                    )
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = computedCalories.toString(),
                        onValueChange = {},
                        label = { Text("Calories (kcal) - Auto-calculated") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        singleLine = true
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = protein,
                            onValueChange = { protein = it },
                            label = { Text("Protein (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = carbs,
                            onValueChange = { carbs = it },
                            label = { Text("Carbs (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fat,
                            onValueChange = { fat = it },
                            label = { Text("Fat (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = fiber,
                            onValueChange = { fiber = it },
                            label = { Text("Fiber (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = portionSize,
                        onValueChange = { portionSize = it },
                        label = { Text("Portion (g)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (isFormValid) {
                                val item = SavedItem(
                                    name = foodName.trim(),
                                    calories = computedCalories,
                                    portion = portionSize.toIntOrNull() ?: 0,
                                    protein = p,
                                    carbs = c,
                                    fat = f,
                                    fiber = fiber.toIntOrNull() ?: 0
                                )

                                viewModel.insertSavedItem(item)

                                foodName = ""
                                portionSize = ""
                                protein = ""
                                carbs = ""
                                fat = ""
                                fiber = ""
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeoGreen,
                            contentColor = Color.Black,
                            disabledContainerColor = GreyishBlackSurface,
                            disabledContentColor = SoftText.copy(alpha = 0.4f)
                        )
                    ) {
                        Text("Save Item")
                    }
                }
            }
        }

        item {
            Text(
                text = "Your Saved Items",
                style = MaterialTheme.typography.titleMedium,
                color = NeoGreen
            )
        }

        if (savedItems.isEmpty()) {
            item {
                Text(
                    text = "No saved items yet. Create one above!",
                    color = SoftText.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(savedItems, key = { it.id }) { item ->
                SavedFoodItemCard(
                    item = item,
                    onLogClick = {
                        viewModel.logSavedItemToToday(item)
                    },
                    onEditClick = { editingSavedItem = item },
                    onDeleteClick = {
                        viewModel.deleteSavedItem(item)
                    }
                )
            }
        }
    }
}
