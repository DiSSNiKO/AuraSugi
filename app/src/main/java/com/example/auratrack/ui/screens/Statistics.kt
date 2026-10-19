package com.example.aurasugi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aurasugi.GreyishBlackCard
import com.example.aurasugi.GreyishBlackSurface
import com.example.aurasugi.NeoGreen
import com.example.aurasugi.NeonPink
import com.example.aurasugi.SoftText
import com.example.aurasugi.ui.MainViewModel
import com.example.aurasugi.ui.components.neonGlow
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Statistics(viewModel: MainViewModel) {
    val allEntries by viewModel.allEntries.collectAsState()

    var selectedYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var showMonthMenu by remember { mutableStateOf(false) }
    var showYearMenu by remember { mutableStateOf(false) }

    var week1Enabled by remember { mutableStateOf(true) }
    var week2Enabled by remember { mutableStateOf(true) }
    var week3Enabled by remember { mutableStateOf(true) }
    var week4Enabled by remember { mutableStateOf(true) }

    val today = remember { LocalDate.now() }
    val currentYearMonth = remember { YearMonth.now() }
    val daysInMonth = selectedYearMonth.lengthOfMonth()
    val allWeeksEnabled = week1Enabled && week2Enabled && week3Enabled && week4Enabled

    // Calculate active days up to current day (excluding future days)
    val activeDays = remember(selectedYearMonth, week1Enabled, week2Enabled, week3Enabled, week4Enabled, today, currentYearMonth) {
        val days = mutableSetOf<Int>()
        if (selectedYearMonth <= currentYearMonth) {
            if (week1Enabled) days.addAll(1..7)
            if (week2Enabled) days.addAll(8..14)
            if (week3Enabled) days.addAll(15..21)
            if (week4Enabled) {
                val endDay = if (allWeeksEnabled) daysInMonth else 28
                days.addAll(22..endDay)
            }

            // Exclude future days in the current month
            if (selectedYearMonth == currentYearMonth) {
                days.retainAll { day -> day <= today.dayOfMonth }
            }
        }
        days
    }

    val totalActiveDays = activeDays.size

    // Filter food entries matching selected YearMonth and active days
    val filteredEntries = remember(allEntries, selectedYearMonth, activeDays) {
        allEntries.filter { entry ->
            try {
                val date = LocalDate.parse(entry.date)
                (date.year == selectedYearMonth.year) &&
                (date.month == selectedYearMonth.month) &&
                activeDays.contains(date.dayOfMonth)
            } catch (_: Exception) {
                false
            }
        }
    }

    val totalCalories = filteredEntries.sumOf { it.calories }
    val totalProtein = filteredEntries.sumOf { it.protein }
    val totalCarbs = filteredEntries.sumOf { it.carbs }
    val totalFat = filteredEntries.sumOf { it.fat }
    val totalFiber = filteredEntries.sumOf { it.fiber }

    val avgCalories = if (totalActiveDays > 0) kotlin.math.round(totalCalories.toDouble() / totalActiveDays).toInt() else 0
    val avgProtein = if (totalActiveDays > 0) kotlin.math.round(totalProtein.toDouble() / totalActiveDays).toInt() else 0
    val avgCarbs = if (totalActiveDays > 0) kotlin.math.round(totalCarbs.toDouble() / totalActiveDays).toInt() else 0
    val avgFat = if (totalActiveDays > 0) kotlin.math.round(totalFat.toDouble() / totalActiveDays).toInt() else 0
    val avgFiber = if (totalActiveDays > 0) kotlin.math.round(totalFiber.toDouble() / totalActiveDays).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month & Year Selector Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { selectedYearMonth = selectedYearMonth.minusMonths(1) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = NeoGreen)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Separate Month Picker Input
                Box {
                    OutlinedButton(
                        onClick = { showMonthMenu = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftText)
                    ) {
                        Text(
                            text = selectedYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftText
                        )
                    }
                    DropdownMenu(
                        expanded = showMonthMenu,
                        onDismissRequest = { showMonthMenu = false }
                    ) {
                        Month.entries.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m.getDisplayName(TextStyle.FULL, Locale.getDefault())) },
                                onClick = {
                                    selectedYearMonth = selectedYearMonth.withMonth(m.value)
                                    showMonthMenu = false
                                }
                            )
                        }
                    }
                }

                // Separate Year Picker Input
                Box {
                    OutlinedButton(
                        onClick = { showYearMenu = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftText)
                    ) {
                        Text(
                            text = selectedYearMonth.year.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftText
                        )
                    }
                    DropdownMenu(
                        expanded = showYearMenu,
                        onDismissRequest = { showYearMenu = false }
                    ) {
                        val currentYear = LocalDate.now().year
                        val years = (currentYear - 5)..(currentYear + 5)
                        years.forEach { y ->
                            DropdownMenuItem(
                                text = { Text(y.toString()) },
                                onClick = {
                                    selectedYearMonth = selectedYearMonth.withYear(y)
                                    showYearMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { selectedYearMonth = selectedYearMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = NeoGreen)
                }
                IconButton(onClick = { selectedYearMonth = YearMonth.now() }) {
                    Icon(Icons.Default.Today, contentDescription = "Jump to Current Month", tint = NeoGreen)
                }
            }
        }

        // Daily Averages Main Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .neonGlow(color = NeoGreen, alpha = 0.2f),
            colors = CardDefaults.cardColors(containerColor = GreyishBlackCard)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Daily Averages",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoGreen
                )
                Text(
                    text = if (totalActiveDays > 0) {
                        "Averaged over $totalActiveDays active day${if (totalActiveDays > 1) "s" else ""}"
                    } else if (selectedYearMonth > currentYearMonth) {
                        "Future month (0 days occurred)"
                    } else {
                        "No days selected"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(120.dp),
                        color = NeonPink,
                        trackColor = GreyishBlackSurface,
                        strokeWidth = 7.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$avgCalories",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NeonPink
                        )
                        Text(
                            text = "kcal / day",
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
                        Text(text = "${avgProtein}g", style = MaterialTheme.typography.titleMedium, color = NeoGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Carbs", style = MaterialTheme.typography.bodySmall, color = SoftText.copy(alpha = 0.7f))
                        Text(text = "${avgCarbs}g", style = MaterialTheme.typography.titleMedium, color = SoftText)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Fat", style = MaterialTheme.typography.bodySmall, color = SoftText.copy(alpha = 0.7f))
                        Text(text = "${avgFat}g", style = MaterialTheme.typography.titleMedium, color = SoftText)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Fiber", style = MaterialTheme.typography.bodySmall, color = SoftText.copy(alpha = 0.7f))
                        Text(text = "${avgFiber}g", style = MaterialTheme.typography.titleMedium, color = SoftText)
                    }
                }
            }
        }

        // Week Toggles Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .neonGlow(color = NeoGreen, alpha = 0.15f),
            colors = CardDefaults.cardColors(containerColor = GreyishBlackCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Include Weeks",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeoGreen
                )

                WeekToggleRow(
                    label = "Week 1",
                    dateRange = "Days 1 – 7",
                    checked = week1Enabled,
                    onCheckedChange = { week1Enabled = it }
                )

                HorizontalDivider(color = GreyishBlackSurface.copy(alpha = 0.5f))

                WeekToggleRow(
                    label = "Week 2",
                    dateRange = "Days 8 – 14",
                    checked = week2Enabled,
                    onCheckedChange = { week2Enabled = it }
                )

                HorizontalDivider(color = GreyishBlackSurface.copy(alpha = 0.5f))

                WeekToggleRow(
                    label = "Week 3",
                    dateRange = "Days 15 – 21",
                    checked = week3Enabled,
                    onCheckedChange = { week3Enabled = it }
                )

                HorizontalDivider(color = GreyishBlackSurface.copy(alpha = 0.5f))

                val week4Range = if (allWeeksEnabled) "Days 22 – $daysInMonth" else "Days 22 – 28"
                WeekToggleRow(
                    label = "Week 4",
                    dateRange = week4Range,
                    checked = week4Enabled,
                    onCheckedChange = { week4Enabled = it }
                )
            }
        }
    }
}

@Composable
private fun WeekToggleRow(
    label: String,
    dateRange: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = SoftText
            )
            Text(
                text = dateRange,
                style = MaterialTheme.typography.bodySmall,
                color = SoftText.copy(alpha = 0.6f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = NeoGreen
            )
        )
    }
}
