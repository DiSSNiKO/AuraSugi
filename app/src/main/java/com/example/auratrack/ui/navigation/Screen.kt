package com.example.aurasugi.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object EntryRecord : Screen("entry_record", "Record", Icons.Default.AddCard)
    object SearchFood : Screen("search_food", "Search", Icons.Default.Search)
    object History : Screen("history", "History", Icons.Default.History)
    object ImportExport : Screen("importexport", "Import/Export", Icons.Default.Archive)
    object USDAapi : Screen("usdaapi", "USDA API", Icons.Default.Api)
    object Statistics : Screen("statistics", "Statistics", Icons.Default.QueryStats)
}
