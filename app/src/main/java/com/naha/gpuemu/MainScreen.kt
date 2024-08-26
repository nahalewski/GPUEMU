package com.naha.gpuemu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.naha.gpuemu.data.CompatibilityDatabase
import com.naha.gpuemu.data.CompatibilityRepository

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val tabs = listOf("Drivers", "Compat Entry", "Database", "About", "Settings")
    var selectedTab by remember { mutableStateOf(0) }
    val androidGreen = Color(0xFFA4C639) // Android green color

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp), // Adjust the padding to move content down
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                when(index) {
                                    0 -> navController.navigate("drivers")
                                    1 -> navController.navigate("compat_entry")
                                    2 -> navController.navigate("database")
                                    3 -> navController.navigate("about")
                                    4 -> navController.navigate("settings")
                                }
                            },
                            text = {
                                Text(
                                    title,
                                    color = androidGreen // Set text color to Android green
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // Add space between tabs and content

                // Navigation logic
                NavHost(navController = navController, startDestination = "drivers") {
                    composable("drivers") {
                        DriverScreen(navController)
                    }
                    composable("compat_entry") {
                        CompatEntryScreen(
                            repository = CompatibilityRepository(
                                CompatibilityDatabase.getInstance(LocalContext.current).compatibilityDao()
                            )
                        )
                    }
                    composable("database") {
                        DatabaseScreen(
                            repository = CompatibilityRepository(
                                CompatibilityDatabase.getInstance(LocalContext.current).compatibilityDao()
                            )
                        )
                    }
                    composable("about") {
                        AboutScreen()
                    }
                    composable("settings") {
                        SettingsScreen()
                    }
                }
            }
        }
    )
}