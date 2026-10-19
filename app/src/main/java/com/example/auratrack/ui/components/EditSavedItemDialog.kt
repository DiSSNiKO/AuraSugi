package com.example.aurasugi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackSurface
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.SoftText
import com.example.aurasugi.calculateCalories
import com.example.aurasugi.data.model.SavedItem

@Composable
fun EditSavedItemDialog(
    item: SavedItem,
    onDismiss: () -> Unit,
    onSave: (SavedItem) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var protein by remember { mutableStateOf(item.protein.toString()) }
    var carbs by remember { mutableStateOf(item.carbs.toString()) }
    var fat by remember { mutableStateOf(item.fat.toString()) }
    var fiber by remember { mutableStateOf(item.fiber.toString()) }
    var portion by remember { mutableStateOf(item.portion.toString()) }

    val p = protein.toIntOrNull() ?: 0
    val c = carbs.toIntOrNull() ?: 0
    val f = fat.toIntOrNull() ?: 0
    val computedCalories = calculateCalories(p, c, f)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GreyishBlackSurface,
        title = { Text("Edit Saved Item", color = NeoGreen) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                OutlinedTextField(value = portion, onValueChange = { portion = it }, label = { Text("Portion (g)") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    item.copy(
                        name = name.trim(),
                        calories = computedCalories,
                        protein = p,
                        carbs = c,
                        fat = f,
                        fiber = fiber.toIntOrNull() ?: item.fiber,
                        portion = portion.toIntOrNull() ?: item.portion
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
