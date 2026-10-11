package com.example.aurasugi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackCard
import com.example.aurasugi.GreyishBlackSurface
import com.example.aurasugi.SoftText
import com.example.aurasugi.data.model.UsdaFoodItem

val UsdaPurple = Color(0xFFAD66A9)
val UsdaBlurColor = Color(0xFFD444CA)

@Composable
fun UsdaFoodItemCard(
    item: UsdaFoodItem,
    onLogClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .neonGlow(
                color = UsdaBlurColor,
                alpha = 0.25f
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
                        imageVector = Icons.Default.Public,
                        contentDescription = "USDA Food Icon",
                        tint = UsdaPurple,
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
                            text = "${item.calories} kcal${if (item.portion > 0) " • ${item.portion}g serving" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = UsdaPurple
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = onLogClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UsdaPurple,
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

                    OutlinedButton(
                        onClick = onSaveClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Save Item",
                            modifier = Modifier.size(16.dp),
                            tint = UsdaPurple
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "SAVE", style = MaterialTheme.typography.labelMedium, color = UsdaPurple)
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
