package com.naha.gpuemu

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val androidGreen = Color(0xFFA4C639) // Android green color

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF121212)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your app logo
            contentDescription = "App Logo",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(androidGreen),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "GPUEMU",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = androidGreen
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Version 0.3.0",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = androidGreen,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Support the development",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = androidGreen,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "ko-fi.com/naha0",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = androidGreen,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/naha0"))
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Thank you for using GPUEMU!",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = androidGreen,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}