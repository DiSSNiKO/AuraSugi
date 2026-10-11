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
import com.example.aurasugi.data.model.SavedItem

@Composable
fun SavedFoodItemCard(
    item: SavedItem,
    onLogClick: () -> Unit,
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
                        contentDescription = "Saved Food Icon",
                        tint = NeoGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftText
                        )
                        Text(
                            text = "${item.calories} kcal${if (item.portion > 0) " • ${item.portion}g" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeoGreen
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onLogClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeoGreen,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "LOG", style = MaterialTheme.typography.labelMedium)
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Saved Item", tint = SoftText)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Saved Item", tint = Color(0xFFCF6679))
                    }
                }
            }

            HorizontalDivider(color = GreyishBlackSurface)
            val macros = listOf(
                "${item.protein}g Protein",
                "${item.carbs}g Carbs",
                "${item.fat}g Fat",
                "${item.fiber}g Fiber"
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
