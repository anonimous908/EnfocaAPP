package com.protas.enfocaapp.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.protas.enfocaapp.R
import com.protas.enfocaapp.core.navigation.Screen

sealed class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, R.string.nav_home, Icons.Default.Home)
    object AppBlock : BottomNavItem(Screen.AppBlock.route, R.string.nav_app_block, Icons.Default.Lock)
    object Stats : BottomNavItem(Screen.Stats.route, R.string.nav_stats, Icons.Default.BarChart)
    object Settings : BottomNavItem(Screen.Settings.route, R.string.nav_settings, Icons.Default.Settings)
}

@Composable
fun MainScreen(onNavigateToIntervention: () -> Unit = {}) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.AppBlock,
        BottomNavItem.Stats,
        BottomNavItem.Settings
    )

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp),
        bottomBar = {
            NavigationBar(
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(id = item.titleResId)) },
                        label = { Text(stringResource(id = item.titleResId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) { HomeScreen(onNavigateToIntervention = onNavigateToIntervention) }
                composable(Screen.Stats.route) { StatsScreen() }
                composable(Screen.AppBlock.route) { AppBlockScreen() }
                composable(Screen.Settings.route) { SettingsScreen() }
            }
        }
    }
}
