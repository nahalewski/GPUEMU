package com.naha.gpuemu

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.naha.gpuemu.components.EmulatorItem
import com.naha.gpuemu.network.*
import com.naha.gpuemu.utils.downloadAndInstallApk
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EmulatorsScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var dolphinLinks by remember { mutableStateOf<List<String>>(emptyList()) }
    var suyuLinks by remember { mutableStateOf<List<String>>(emptyList()) }
    var ppssppLink by remember { mutableStateOf<String?>(null) }
    var vita3kLinks by remember { mutableStateOf<List<String>>(emptyList()) }
    var citraLinks by remember { mutableStateOf<List<String>>(emptyList()) }
    var lime3dsLinks by remember { mutableStateOf<List<String>>(emptyList()) }

    var showDolphinLinks by remember { mutableStateOf(false) }
    var showSuyuLinks by remember { mutableStateOf(false) }
    var showPPSSPPLink by remember { mutableStateOf(false) }
    var showVita3kLinks by remember { mutableStateOf(false) }
    var showCitraLinks by remember { mutableStateOf(false) }
    var showLime3dsLinks by remember { mutableStateOf(false) }
    var showDuckStationLink by remember { mutableStateOf(false) }

    val downloadLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                dolphinLinks = fetchDolphinEmulatorApks()
                suyuLinks = fetchSuyuApks()
                ppssppLink = fetchPPSSPPApk()
                vita3kLinks = fetchVita3kApks()
                citraLinks = fetchCitra3DSApks()
                lime3dsLinks = fetchLime3DSApks()
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Failed to load files: ${e.message}"
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = errorMessage ?: "Unknown error")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Dolphin Emulator
            item {
                Text(
                    text = "Dolphin Emulator",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { showDolphinLinks = !showDolphinLinks }
                )
            }
            if (showDolphinLinks) {
                items(dolphinLinks) { link ->
                    EmulatorItem(
                        title = "Dolphin Emulator",
                        link = link,
                        onDownloadClicked = {
                            downloadLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            coroutineScope.launch {
                                downloadAndInstallApk(context, link, "Dolphin Emulator")
                            }
                        }
                    )
                }
            }

            // Suyu Emulator
            item {
                Text(
                    text = "Suyu Emulator",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { showSuyuLinks = !showSuyuLinks }
                )
            }
            if (showSuyuLinks) {
                items(suyuLinks) { link ->
                    EmulatorItem(
                        title = "Suyu Emulator",
                        link = link,
                        onDownloadClicked = {
                            downloadLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            coroutineScope.launch {
                                downloadAndInstallApk(context, link, "Suyu Emulator")
                            }
                        }
                    )
                }
            }

            // PPSSPP
            item {
                Text(
                    text = "PPSSPP",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { showPPSSPPLink = !showPPSSPPLink }
                )
            }
            if (showPPSSPPLink && ppssppLink != null) {
                item {
                    EmulatorItem(
                        title = "PPSSPP",
                        link = ppssppLink!!,
                        onDownloadClicked = {
                            downloadLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            coroutineScope.launch {
                                downloadAndInstallApk(context, ppssppLink!!, "PPSSPP")
                            }
                        }
                    )
                }
            }

            // Vita3K Emulator
            item {
                Text(
                    text = "Vita3K Emulator",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { showVita3kLinks = !showVita3kLinks }
                )
            }
            if (showVita3kLinks) {
                items(vita3kLinks) { link ->
                    EmulatorItem(
                        title = "Vita3K Emulator",
                        link = link,
                        onDownloadClicked = {
                            downloadLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            coroutineScope.launch {
                                downloadAndInstallApk(context, link, "Vita3K Emulator")
                            }
                        }
                    )
                }
            }

            // Citra Emulator
            item {
                Text(
                    text = "Citra Emulator",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { showCitraLinks = !showCitraLinks }
                )
            }
            if (showCitraLinks) {
                items(citraLinks) { link ->
                    EmulatorItem(
                        title = "Citra Emulator",
                        link = link,
                        onDownloadClicked = {
                            downloadLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            coroutineScope.launch {
                                downloadAndInstallApk(context, link, "Citra Emulator")
                            }
                        }
                    )
                }
            }

            // Lime3DS Emulator
            item {
                Text(
                    text = "Lime3DS Emulator",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { showLime3dsLinks = !showLime3dsLinks }
                )
            }
            if (showLime3dsLinks) {
                items(lime3dsLinks) { link ->
                    EmulatorItem(
                        title = "Lime3DS Emulator",
                        link = link,
                        onDownloadClicked = {
                            downloadLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            coroutineScope.launch {
                                downloadAndInstallApk(context, link, "Lime3DS Emulator")
                            }
                        }
                    )
                }
            }

            // DuckStation Emulator
            item {
                Text(
                    text = "DuckStation",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { showDuckStationLink = !showDuckStationLink }
                )
            }
            if (showDuckStationLink) {
                item {
                    Text(
                        text = "Get DuckStation from Play Store",
                        modifier = Modifier.clickable {
                            val playStoreUrl =
                                "https://play.google.com/store/apps/details?id=com.github.stenzek.duckstation"
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl))
                            )
                        }
                    )
                }
            }
        }
    }
}