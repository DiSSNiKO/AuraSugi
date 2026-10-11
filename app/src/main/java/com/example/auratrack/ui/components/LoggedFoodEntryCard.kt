package com.example.aurasugi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackCard
import com.example.aurasugi.GreyishBlackSurface
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.SoftText
import com.example.aurasugi.data.model.FoodEntry

@Composable
fun LoggedFoodEntryCard(
    entry: FoodEntry,
    onLogToTodayClick: (() -> Unit)? = null,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = "Food Icon",
                        tint = NeoGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = entry.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftText
                        )
                        Text(
                            text = "${entry.calories} kcal${if (entry.portion > 0) " • ${entry.portion}g" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeoGreen
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onLogToTodayClick != null) {
                        Button(
                            onClick = onLogToTodayClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeoGreen,
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Log to Today",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "LOG", style = MaterialTheme.typography.labelMedium)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Entry", tint = SoftText)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Entry", tint = Color(0xFFCF6679))
                    }
                }
            }

            HorizontalDivider(color = GreyishBlackSurface)
            val macros = listOf(
                "${entry.protein}g Protein",
                "${entry.carbs}g Carbs",
                "${entry.fat}g Fat",
                "${entry.fiber}g Fiber"
            )
            val chunkedMacros = macros.chunked(2)
            Column(modifier = Modifier.fillMaxWidth()) {
                chunkedMacros.forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowItems.forEach { macro ->
                            Text(
                                text = macro,
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftText.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
