package com.example.aurasugi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackSurface
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.SoftText
import com.example.aurasugi.calculateCalories
import com.example.aurasugi.data.model.FoodEntry
import kotlin.math.roundToInt

@Composable
fun EditFoodEntryDialog(
    entry: FoodEntry,
    onDismiss: () -> Unit,
    onSave: (FoodEntry) -> Unit
) {
    var name by remember { mutableStateOf(entry.name) }
    var protein by remember { mutableStateOf(entry.protein.toString()) }
    var carbs by remember { mutableStateOf(entry.carbs.toString()) }
    var fat by remember { mutableStateOf(entry.fat.toString()) }
    var fiber by remember { mutableStateOf(entry.fiber.toString()) }
    var portion by remember { mutableStateOf(entry.portion.toString()) }

    val originalPortion = if (entry.portion > 0) entry.portion else 1
    val baseProtein = entry.protein
    val baseCarbs = entry.carbs
    val baseFat = entry.fat
    val baseFiber = entry.fiber

    val p = protein.toIntOrNull() ?: 0
    val c = carbs.toIntOrNull() ?: 0
    val f = fat.toIntOrNull() ?: 0
    val computedCalories = calculateCalories(p, c, f)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GreyishBlackSurface,
        title = { Text("Edit Logged Entry", color = NeoGreen) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(
                    value = computedCalories.toString(),
                    onValueChange = {},
                    label = { Text("Calories (kcal) - Auto-calculated") },
                    enabled = false,
                    singleLine = true
                )
                OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein (g)") }, singleLine = true)
                OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs (g)") }, singleLine = true)
                OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("Fat (g)") }, singleLine = true)
                OutlinedTextField(value = fiber, onValueChange = { fiber = it }, label = { Text("Fiber (g)") }, singleLine = true)
                OutlinedTextField(
                    value = portion,
                    onValueChange = { newPortionStr ->
                        portion = newPortionStr
                        val newP = newPortionStr.toIntOrNull()
                        if (newP != null && entry.portion > 0) {
                            protein = ((baseProtein.toFloat() / originalPortion) * newP).roundToInt().coerceAtLeast(0).toString()
                            carbs = ((baseCarbs.toFloat() / originalPortion) * newP).roundToInt().coerceAtLeast(0).toString()
                            fat = ((baseFat.toFloat() / originalPortion) * newP).roundToInt().coerceAtLeast(0).toString()
                            fiber = ((baseFiber.toFloat() / originalPortion) * newP).roundToInt().coerceAtLeast(0).toString()
                        }
                    },
                    label = { Text("Portion (g)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    entry.copy(
                        name = name.trim(),
                        calories = computedCalories,
                        protein = p,
                        carbs = c,
                        fat = f,
                        fiber = fiber.toIntOrNull() ?: entry.fiber,
                        portion = portion.toIntOrNull() ?: entry.portion
                    )
                )
            }) {
                Text("Save", color = NeoGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SoftText)
            }
        }
    )
}
