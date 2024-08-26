package com.naha.gpuemu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naha.gpuemu.data.CompatibilityEntry
import com.naha.gpuemu.data.CompatibilityRepository
import kotlinx.coroutines.launch

@Composable
fun DatabaseScreen(
    repository: CompatibilityRepository
) {
    var compatibilityList by remember { mutableStateOf<List<CompatibilityEntry>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            compatibilityList = repository.getAllEntries()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Database Entries", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            items(compatibilityList) { entry ->
                Text(
                    text = "${entry.gameName} (${entry.versionNumber}) - ${entry.status}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}