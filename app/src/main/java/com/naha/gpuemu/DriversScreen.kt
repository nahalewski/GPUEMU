package com.naha.gpuemu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naha.gpuemu.network.GithubAsset
import com.naha.gpuemu.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FetchDriversScreen(modifier: Modifier = Modifier) {
    var releases by remember { mutableStateOf<List<GithubAsset>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getReleases()
                if (response.isNotEmpty()) {
                    releases = response.flatMap { it.assets }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Handle error
            }
        }
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(releases) { asset ->
            DriverItemDisplay(asset) // Updated function call to DriverItemDisplay
        }
    }
}

@Composable
fun DriverItemDisplay(asset: GithubAsset) { // Renamed to DriverItemDisplay
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Handle item click, e.g., navigate to detail screen or download the file
            }
    ) {
        Text(text = asset.name, style = MaterialTheme.typography.titleMedium)
        Text(text = asset.browser_download_url, style = MaterialTheme.typography.bodyMedium)
    }
}