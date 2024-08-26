package com.naha.gpuemu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naha.gpuemu.data.CompatibilityEntry
import com.naha.gpuemu.data.CompatibilityRepository
import kotlinx.coroutines.launch

@Composable
fun CompatEntryScreen(
    modifier: Modifier = Modifier,
    repository: CompatibilityRepository
) {
    var gameName by remember { mutableStateOf("") }
    var versionNumber by remember { mutableStateOf("") }
    var emulatorUsed by remember { mutableStateOf("") }
    var emulatorVersion by remember { mutableStateOf("") }
    var turnipDriverUsed by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.padding(16.dp)) {
        // Input fields
        CompatibilityTextField(value = gameName, label = "Game Name", onValueChange = { gameName = it })
        CompatibilityTextField(value = versionNumber, label = "Version Number", onValueChange = { versionNumber = it })
        CompatibilityTextField(value = emulatorUsed, label = "Emulator Used", onValueChange = { emulatorUsed = it })
        CompatibilityTextField(value = emulatorVersion, label = "Emulator Version", onValueChange = { emulatorVersion = it })
        CompatibilityTextField(value = turnipDriverUsed, label = "Turnip Driver Used", onValueChange = { turnipDriverUsed = it })
        CompatibilityTextField(value = status, label = "Status", onValueChange = { status = it })

        // Add Entry Button
        Button(onClick = {
            if (gameName.isNotEmpty() && versionNumber.isNotEmpty()) {
                coroutineScope.launch {
                    val entry = CompatibilityEntry(
                        gameName = gameName,
                        versionNumber = versionNumber, // If versionNumber is Int in CompatibilityEntry, convert it here
                        emulatorUsed = emulatorUsed,
                        emulatorVersion = emulatorVersion,
                        turnipDriverUsed = turnipDriverUsed,
                        status = status
                    )
                    repository.addEntry(entry)
                    // Optionally clear the fields
                    gameName = ""
                    versionNumber = ""
                    emulatorUsed = ""
                    emulatorVersion = ""
                    turnipDriverUsed = ""
                    status = ""
                }
            }
        }, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Save Entry")
        }
    }
}

// Make sure this function is defined somewhere in your project
@Composable
fun CompatibilityTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}