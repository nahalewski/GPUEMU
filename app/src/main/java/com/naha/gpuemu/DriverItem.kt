import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.ExperimentalComposeUiApi

// Updated GithubAsset data class
data class GithubAsset(
    val name: String,
    val browser_download_url: String,
    // Add any other properties your asset might have
)

@OptIn(ExperimentalComposeUiApi::class)
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
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        navController.navigate(
                            "detail/${asset.name}/$releaseDate/$changelog"
                        )
                    },
                    onTap = {
                        startDownload(context, asset)
                    }
                )
            }
    ) {
        Text(
            text = asset.name,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Released on: $releaseDate",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
        )
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