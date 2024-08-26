package com.naha.gpuemu

import com.naha.gpuemu.models.GithubAsset
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.naha.gpuemu.network.RetrofitClient
import com.naha.gpuemu.models.GithubRelease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FetchDriversScreen(navController: NavController, modifier: Modifier = Modifier) {
    var releases by remember { mutableStateOf<List<Pair<GithubAsset, GithubRelease>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.githubApiService.getReleases()
                if (response.isNotEmpty()) {
                    releases = response.flatMap { release: GithubRelease ->
                        release.assets.map { asset: GithubAsset ->
                            Pair(asset, release)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(releases) { (asset, release) ->
            DriverItemDisplay(
                asset = asset,
                releaseDate = release.publishedAt,
                changelog = release.body ?: "No changelog available",
                navController = navController
            )
        }
    }
}

@Composable
fun DriverItemDisplay(
    asset: GithubAsset,
    releaseDate: String,
    changelog: String,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("detail/${asset.name}/$releaseDate/$changelog")
            }
    ) {
        Text(text = asset.name ?: "Unknown", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Released on: $releaseDate",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = asset.browserDownloadUrl ?: "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}