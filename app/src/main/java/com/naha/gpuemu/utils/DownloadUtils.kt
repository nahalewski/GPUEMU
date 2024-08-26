package com.naha.gpuemu.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
suspend fun downloadAndInstallApk(context: Context, downloadUrl: String, title: String) {
    withContext(Dispatchers.IO) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(downloadUrl))
            request.setTitle("$title APK")
            request.setDescription("Downloading $title APK")
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${title.replace(" ", "_").lowercase()}.apk")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val downloadId = downloadManager.enqueue(request)

            val onComplete = object : BroadcastReceiver() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onReceive(ctxt: Context?, intent: Intent?) {
                    val fileUri = downloadManager.getUriForDownloadedFile(downloadId)
                    if (fileUri != null) {
                        installApk(context, File(fileUri.path!!))
                        context.unregisterReceiver(this)
                    } else {
                        Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED)
        } catch (e: Exception) {
            Log.e("Download", "Failed to download $title: ${e.message}", e)
            Toast.makeText(context, "Failed to download $title: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun installApk(context: Context, apkFile: File) {
    val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile)

    val installIntent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
        data = apkUri
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    if (context.packageManager.canRequestPackageInstalls()) {
        context.startActivity(installIntent)
    } else {
        Toast.makeText(context, "Please allow permission to install unknown apps.", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }
}