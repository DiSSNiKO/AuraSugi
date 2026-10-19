package com.example.aurasugi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aurasugi.data.db.AppDatabase
import com.example.aurasugi.data.repository.AppRepository
import com.example.aurasugi.ui.MainViewModel
import com.example.aurasugi.ui.MainViewModelFactory
import com.example.aurasugi.ui.navigation.Screen
import com.example.aurasugi.ui.screens.DashboardScreen
import com.example.aurasugi.ui.screens.EntryRecordScreen
import com.example.aurasugi.ui.screens.HistoryScreen
import com.example.aurasugi.ui.screens.ImportExport
import com.example.aurasugi.ui.screens.SearchFoodScreen
import com.example.aurasugi.ui.screens.Statistics
import com.example.aurasugi.ui.screens.USDAapi
import kotlinx.coroutines.launch

val NeoGreen = Color(0xFF00E13A)
val DarkNeoGreen = Color(0xFF1B592A)
val NeonPink = Color(0xFFFF2A85)
val GreyishBlackBg = Color(0xFF16191B)
val GreyishBlackSurface = Color(0xFF1E2225)
val GreyishBlackCard = Color(0xFF19231D)
val SoftText = Color(0xFFE1E3E4)

fun calculateCalories(protein: Int, carbs: Int, fat: Int): Int {
    return (protein * 4) + (carbs * 4) + (fat * 9)
}

private val CustomDarkColorScheme = darkColorScheme(
    primary = NeoGreen,
    onPrimary = Color.Black,
    primaryContainer = DarkNeoGreen,
    onPrimaryContainer = SoftText,
    background = GreyishBlackBg,
    onBackground = SoftText,
    surface = GreyishBlackSurface,
    onSurface = NeoGreen,
    surfaceVariant = GreyishBlackSurface,
    onSurfaceVariant = SoftText
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(database.entryDao(), database.savedItemsDao(), database.apiKeyDao())
        val usdaPreferences = com.example.aurasugi.data.preferences.UsdaPreferences(applicationContext)
        val viewModelFactory = MainViewModelFactory(repository, usdaPreferences)

        setContent {
            MaterialTheme(colorScheme = CustomDarkColorScheme) {
                MainAppLayout(viewModelFactory)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(viewModelFactory: MainViewModelFactory) {
    val mainViewModel: MainViewModel = viewModel(factory = viewModelFactory)
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val screens = remember {
        listOf(
            Screen.Dashboard,
            Screen.EntryRecord,
            Screen.SearchFood,
            Screen.History,
            Screen.ImportExport,
            Screen.USDAapi,
            Screen.Statistics
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = screens.find { it.route == currentRoute } ?: Screen.Dashboard

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = GreyishBlackSurface,
                drawerContentColor = SoftText
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "aurasugi",
                    style = MaterialTheme.typography.titleLarge,
                    color = NeoGreen,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(color = GreyishBlackCard)
                Spacer(modifier = Modifier.height(8.dp))

                screens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationDrawerItem(
                        icon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            color = if (isSelected) NeoGreen else Color.Transparent,
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(screen.icon, contentDescription = screen.title)
                            }
                        },
                        label = { Text(screen.title) },
                        selected = isSelected,
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = GreyishBlackCard,
                            selectedIconColor = NeoGreen,
                            selectedTextColor = NeoGreen,
                            unselectedIconColor = SoftText.copy(alpha = 0.7f),
                            unselectedTextColor = SoftText.copy(alpha = 0.7f)
                        ),
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.title, color = SoftText) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Toggle Menu", tint = NeoGreen)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = GreyishBlackSurface)
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding()
            ) {
                composable(Screen.Dashboard.route) { DashboardScreen(mainViewModel) }
                composable(Screen.EntryRecord.route) { EntryRecordScreen(mainViewModel) }
                composable(Screen.SearchFood.route) { SearchFoodScreen(mainViewModel) }
                composable(Screen.History.route) { HistoryScreen(mainViewModel) }
                composable(Screen.ImportExport.route) { ImportExport(mainViewModel) }
                composable(Screen.USDAapi.route) { USDAapi(mainViewModel) }
                composable(Screen.Statistics.route) { Statistics(mainViewModel) }
            }
        }
    }
}
