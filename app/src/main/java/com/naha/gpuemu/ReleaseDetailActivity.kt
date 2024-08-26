package com.naha.gpuemu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naha.gpuemu.models.GithubRelease
import com.naha.gpuemu.ui.theme.GPUEMUTheme

@OptIn(ExperimentalMaterial3Api::class)
class ReleaseDetailActivity : ComponentActivity() {

    private lateinit var release: GithubRelease

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        release = intent.getSerializableExtra("release") as GithubRelease

        setContent {
            GPUEMUTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(release.name) }
                        )
                    },
                    content = { innerPadding ->
                        ReleaseDetailScreen(
                            release = release,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ReleaseDetailScreen(release: GithubRelease, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = release.body ?: "No changelog available")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Implement download logic here
        }) {
            Text(text = "Download")
        }
    }
}