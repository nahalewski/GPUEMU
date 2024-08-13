package com.naha.gpuemu

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naha.gpuemu.ui.theme.GPUEMUTheme
import com.naha.gpuemu.network.GithubAsset
import com.naha.gpuemu.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GPUEMUTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val tabs = listOf("Drivers", "About")
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> DriversScreen(Modifier.padding(innerPadding))
            1 -> AboutScreen(Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun DriversScreen(modifier: Modifier = Modifier) {
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
            DriverItem(asset)
        }
    }
}

@Composable
fun DriverItem(asset: GithubAsset) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                startDownload(context, asset)
            }
    ) {
        Text(text = asset.name, style = MaterialTheme.typography.titleMedium)
        Text(text = asset.browser_download_url, style = MaterialTheme.typography.bodyMedium)
    }
}

fun startDownload(context: Context, asset: GithubAsset) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = Uri.parse(asset.browser_download_url)

    val request = DownloadManager.Request(uri).apply {
        setTitle(asset.name)
        setDescription("Downloading ${asset.name}")
        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, asset.name)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    }

    downloadManager.enqueue(request)
}

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    Text(
        text = "MesaTurnipDrivers v1.0\n\nThis app helps you manage GPU drivers.\n\nIf you like this app and want to show support for updates.",
        modifier = modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GPUEMUTheme {
        MainScreen()
    }
}