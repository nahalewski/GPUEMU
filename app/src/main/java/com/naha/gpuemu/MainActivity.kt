package com.naha.gpuemu

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.naha.gpuemu.ui.theme.GPUEMUTheme
import com.naha.gpuemu.network.GithubAsset
import com.naha.gpuemu.network.GithubRelease
import com.naha.gpuemu.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GPUEMUTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { MainScreen(navController = navController) }
                    composable(
                        "detail/{name}/{date}/{changelog}",
                        arguments = listOf(
                            navArgument("name") { type = NavType.StringType },
                            navArgument("date") { type = NavType.StringType },
                            navArgument("changelog") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        DetailScreen(navController, backStackEntry)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
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
            0 -> DriversScreen(navController, Modifier.padding(innerPadding))
            1 -> AboutScreen(Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun DriversScreen(navController: NavController, modifier: Modifier = Modifier) {
    var releases by remember { mutableStateOf<List<Pair<GithubAsset, GithubRelease>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getReleases()
                if (response.isNotEmpty()) {
                    releases = response.flatMap { release ->
                        release.assets.map { asset ->
                            asset to release // Pair each asset with its release metadata
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Handle error
            }
        }
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(releases) { (asset, release) ->
            DriverItem(
                asset = asset,
                releaseDate = release.created_at,
                changelog = release.body ?: "No changelog available",
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DriverItem(
    asset: GithubAsset,
    releaseDate: String,
    changelog: String,
    navController: NavController
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { startDownload(context, asset) },
                onLongClick = {
                    navController.navigate(
                        "detail/${asset.name}/$releaseDate/${Uri.encode(changelog)}"
                    )
                }
            )
    ) {
        Text(text = asset.name, style = MaterialTheme.typography.titleMedium)
        Text(text = "Released on: $releaseDate", style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController, backStackEntry: NavBackStackEntry) {
    val name = backStackEntry.arguments?.getString("name")
    val date = backStackEntry.arguments?.getString("date")
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
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Release: $name", style = MaterialTheme.typography.titleLarge)
            Text(text = "Released on: $date", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Changelog:", style = MaterialTheme.typography.titleMedium)
            Text(
                text = changelog,
                style = MaterialTheme.typography.bodyMedium
            )
        }
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
        text = "GPUEMU v0.1\n\nThis app helps you manage GPU drivers.\n\nIf you like this app and want to show support for updates.",
        modifier = modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    GPUEMUTheme {
        MainScreen(navController = rememberNavController())
    }
}