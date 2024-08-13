import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.Composable // <-- Import for @Composable
import androidx.compose.foundation.layout.* // <-- Import for Column, padding, etc.
import androidx.compose.foundation.clickable // <-- Import for clickable
import androidx.compose.material3.* // <-- Import for Material Design components
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.naha.gpuemu.network.GithubAsset

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