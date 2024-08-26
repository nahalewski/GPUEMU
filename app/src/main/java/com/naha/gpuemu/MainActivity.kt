package com.naha.gpuemu


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import com.google.firebase.FirebaseApp
import com.naha.gpuemu.ui.theme.GPUEMUTheme
import com.naha.gpuemu.data.CompatibilityDatabase
import com.naha.gpuemu.data.CompatibilityRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            GPUEMUTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val coroutineScope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            // Drawer content with navigation options
                            NavigationDrawerItem(
                                label = { Text("Drivers") },
                                selected = false,
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate("drivers")
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text("Compat Entry") },
                                selected = false,
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate("compat_entry")
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text("Database") },
                                selected = false,
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate("database")
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text("About") },
                                selected = false,
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate("about")
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text("Settings") },
                                selected = false,
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate("settings")
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text("Emulators") },
                                selected = false,
                                onClick = {
                                    coroutineScope.launch { drawerState.close() }
                                    navController.navigate("emulators")
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("GPUEMU") },
                                navigationIcon = {
                                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                                    }
                                }
                            )
                        },
                        content = { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = "drivers",  // Set Drivers as the initial screen
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("drivers") {
                                    DriverScreen(navController = navController)
                                }
                                composable("compat_entry") {
                                    CompatEntryScreen(
                                        repository = CompatibilityRepository(
                                            CompatibilityDatabase.getInstance(applicationContext).compatibilityDao()
                                        )
                                    )
                                }
                                composable("database") {
                                    DatabaseScreen(
                                        repository = CompatibilityRepository(
                                            CompatibilityDatabase.getInstance(applicationContext).compatibilityDao()
                                        )
                                    )
                                }
                                composable("about") { AboutScreen() }
                                composable("settings") { SettingsScreen() }
                                composable("emulators") {
                                    EmulatorsScreen()  // No navController passed here
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}