package com.naha.gpuemu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naha.gpuemu.models.DriverRelease // <-- Make sure this import is present
import com.naha.gpuemu.ui.theme.GPUEMUTheme

@OptIn(ExperimentalMaterial3Api::class) // Add this line to acknowledge the experimental API
class ReleaseDetailActivity : ComponentActivity() {

    private lateinit var release: DriverRelease

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        release = intent.getSerializableExtra("release") as DriverRelease

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
fun ReleaseDetailScreen(release: DriverRelease, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = release.changelog)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Implement download logic here
        }) {
            Text(text = "Download")
        }
    }
}