package com.naha.gpuemu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController, backStackEntry: NavBackStackEntry) {
    val name = backStackEntry.arguments?.getString("name") ?: "Unknown"
    val date = backStackEntry.arguments?.getString("date") ?: "Unknown"
    val changelog = backStackEntry.arguments?.getString("changelog") ?: "No changelog available"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text(text = "Release: $name", style = MaterialTheme.typography.titleLarge)
            Text(text = "Released on: $date", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Changelog:", style = MaterialTheme.typography.titleMedium)
            Text(
                text = changelog,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Adding the DownloadButton
            DownloadButton(
                url = "https://github.com/K11MCH1/AdrenoToolsDrivers/releases/download/$name.zip",
                fileName = "$name.zip"
            )
        }
    }
}