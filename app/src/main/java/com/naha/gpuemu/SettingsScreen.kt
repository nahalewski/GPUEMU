package com.naha.gpuemu

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.jaredrummler.android.device.DeviceName

fun getDetailedDeviceInfo(context: Context): String {
    val deviceName = DeviceName.getDeviceName() // Get the human-readable device name
    val manufacturer = Build.MANUFACTURER ?: "Unknown"
    val model = Build.MODEL ?: "Unknown"
    val cpuInfo = getCpuInfoFromBuild() // Get CPU information based on the Build class

    val totalRam = getTotalRam(context)

    return """
        Manufacturer: $manufacturer
        Model: $model
        Device Name: $deviceName
        CPU: $cpuInfo
        RAM: $totalRam MB
    """.trimIndent()
}

fun getCpuInfoFromBuild(): String {
    val cpuModel = Build.SOC_MODEL ?: Build.HARDWARE ?: "Unknown CPU"
    return when {
        cpuModel.contains("Snapdragon", ignoreCase = true) -> "Qualcomm Snapdragon ${extractSnapdragonGeneration(cpuModel)}"
        cpuModel.contains("Exynos", ignoreCase = true) -> "Samsung Exynos"
        cpuModel.contains("Kirin", ignoreCase = true) -> "Huawei Kirin"
        cpuModel.contains("Mediatek", ignoreCase = true) -> "Mediatek"
        cpuModel.contains("Mali", ignoreCase = true) -> "Mali"
        else -> "Unknown CPU"
    }
}

fun extractSnapdragonGeneration(model: String): String {
    return model.replace(Regex("[^\\d]"), "").takeIf { it.isNotBlank() } ?: "Unknown"
}

fun getTotalRam(context: Context): Long {
    val memoryInfo = ActivityManager.MemoryInfo()
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(memoryInfo)
    return memoryInfo.totalMem / (1024 * 1024) // Convert to MB
}

fun getAdditionalDeviceInfo(context: Context): String {
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    val isCharging = batteryManager.isCharging(context)

    val storageInfo = getStorageInfo()
    val screenInfo = getScreenInfo(context)

    return """
        Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})
        Security Patch: ${Build.VERSION.SECURITY_PATCH}
        Battery Level: $batteryLevel%
        Charging: $isCharging
        ${storageInfo}
        ${screenInfo}
        Build Number: ${Build.DISPLAY}
        Kernel Version: ${System.getProperty("os.version")}
        Bluetooth: ${getBluetoothVersion()}
        NFC Support: ${if (context.packageManager.hasSystemFeature("android.hardware.nfc")) "Yes" else "No"}
    """.trimIndent()
}

fun BatteryManager.isCharging(context: Context): Boolean {
    val intent = context.registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
    val status = intent?.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1) ?: -1
    return status == android.os.BatteryManager.BATTERY_STATUS_CHARGING || status == android.os.BatteryManager.BATTERY_STATUS_FULL
}

fun getStorageInfo(): String {
    val statFs = StatFs(Environment.getDataDirectory().absolutePath)
    val totalBytes = statFs.blockCountLong * statFs.blockSizeLong
    val availableBytes = statFs.availableBlocksLong * statFs.blockSizeLong
    val totalStorage = totalBytes / (1024 * 1024 * 1024) // Convert to GB
    val availableStorage = availableBytes / (1024 * 1024 * 1024) // Convert to GB

    return "Storage: ${availableStorage}GB / ${totalStorage}GB available"
}

fun getScreenInfo(context: Context): String {
    val display = context.resources.displayMetrics
    val width = display.widthPixels
    val height = display.heightPixels
    val densityDpi = display.densityDpi
    val refreshRate = context.display?.refreshRate?.toInt() ?: 60

    return "Screen: ${width}x${height} pixels, $densityDpi dpi, $refreshRate Hz"
}

fun getBluetoothVersion(): String {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
            val adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
            adapter?.let {
                when {
                    it.isLe2MPhySupported -> "5.0+"
                    it.isLeCodedPhySupported -> "5.0+"
                    it.isLeExtendedAdvertisingSupported -> "5.0+"
                    it.isLePeriodicAdvertisingSupported -> "5.0+"
                    else -> "4.2"
                }
            } ?: "Unknown"
        }
        else -> "Unknown"
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val deviceInfo = remember { mutableStateOf("Loading...") }
    val additionalInfo = getAdditionalDeviceInfo(context)
    val androidGreen = Color(0xFFA4C639) // Android green color

    LaunchedEffect(Unit) {
        deviceInfo.value = getDetailedDeviceInfo(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF121212)), // Background dark color for a modern look
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Device Information",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = androidGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1F1F1F)
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = deviceInfo.value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = androidGreen,
                        fontSize = 18.sp,
                        lineHeight = 24.sp
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1F1F1F)
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = additionalInfo,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = androidGreen,
                        fontSize = 18.sp,
                        lineHeight = 24.sp
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 10
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}