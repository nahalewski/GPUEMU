package com.naha.gpuemu

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naha.gpuemu.models.GithubRelease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Define the GithubApiService interface
interface GithubApiService {
    @GET("repos/K11MCH1/AdrenoToolsDrivers/releases")
    suspend fun getReleases(): List<GithubRelease>
}

// Create the RetrofitClient object
object RetrofitClient {
    private const val BASE_URL = "https://api.github.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val githubApiService: GithubApiService = retrofit.create(GithubApiService::class.java)
}

@Composable
fun DriverScreen(navController: NavHostController) {
    val releases = remember { mutableStateOf<List<GithubRelease>>(emptyList()) }
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            releases.value = withContext(Dispatchers.IO) {
                RetrofitClient.githubApiService.getReleases()
            }
        } catch (e: HttpException) {
            errorMessage = if (e.code() == 404) {
                "Releases not found. Please check the repository URL."
            } else {
                "Failed to load releases: ${e.message}"
            }
        } catch (e: Exception) {
            errorMessage = "An error occurred: ${e.message}"
        }
    }

    if (errorMessage != null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = errorMessage ?: "Unknown error")
        }
    } else if (releases.value.isEmpty()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "No releases available")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(releases.value) { release ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = release.name)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            navController.navigate("detail/${release.name}/${release.publishedAt}/${release.body ?: ""}")
                        }
                    ) {
                        Text(text = "View Details")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val asset = release.assets.firstOrNull()
                            if (asset != null) {
                                val downloadUrl = asset.browserDownloadUrl.orEmpty()
                                val fileName = asset.name ?: "unknown_file"
                                downloadAsset(context = context, url = downloadUrl, fileName = fileName)
                            } else {
                                Toast.makeText(context, "No assets available for download", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text(text = "Download")
                    }
                }
            }
        }
    }
}

fun downloadAsset(context: Context, url: String, fileName: String) {
    try {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(fileName)
        request.setDescription("Downloading $fileName")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)

        Toast.makeText(context, "Download started: $fileName", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to download: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}