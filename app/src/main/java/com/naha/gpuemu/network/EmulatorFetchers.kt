package com.naha.gpuemu.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.concurrent.ConcurrentHashMap

// In-memory cache to store APK lists
private val apkCache = ConcurrentHashMap<String, List<String>>()

// Time-to-live (TTL) for cache in milliseconds (e.g., 10 minutes)
private const val CACHE_TTL = 10 * 60 * 1000
private val cacheTimestamps = ConcurrentHashMap<String, Long>()

suspend fun fetchDolphinEmulatorApks(forceRefresh: Boolean = false): List<String> {
    return fetchApks("https://dolphin-emu.org/download/list/releases/1/", "a:contains(Android)", forceRefresh) { link: Element ->
        "https://dolphin-emu.org${link.attr("href")}"
    }
}

suspend fun fetchSuyuApks(forceRefresh: Boolean = false): List<String> {
    return fetchApks("https://git.suyu.dev/suyu/suyu/releases", "a[href$=.apk]", forceRefresh) { link: Element ->
        link.attr("href")
    }
}

suspend fun fetchPPSSPPApk(forceRefresh: Boolean = false): String? {
    return withContext(Dispatchers.IO) {
        if (!forceRefresh && apkCache.containsKey("ppsspp") && !isCacheExpired("ppsspp")) {
            return@withContext apkCache["ppsspp"]?.firstOrNull()
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://www.ppsspp.org/files/1_17_1/ppsspp.apk")
            .build()

        val response = client.newCall(request).execute()
        return@withContext if (response.isSuccessful) {
            val apkUrl = request.url.toString()
            apkCache["ppsspp"] = listOf(apkUrl)
            cacheTimestamps["ppsspp"] = System.currentTimeMillis()
            apkUrl
        } else {
            println("Failed to fetch PPSSPP APK")
            null
        }
    }
}

suspend fun fetchVita3kApks(forceRefresh: Boolean = false): List<String> {
    return fetchApks("https://github.com/Vita3K/Vita3K-Android/releases", "a[href*=/releases/download/]", forceRefresh) { link: Element ->
        "https://github.com${link.attr("href")}"
    }
}

suspend fun fetchCitra3DSApks(forceRefresh: Boolean = false): List<String> {
    return fetchApks("https://github.com/PabloMK7/citra/releases", "a[href*=/releases/download/]", forceRefresh) { link: Element ->
        "https://github.com${link.attr("href")}"
    }
}

suspend fun fetchLime3DSApks(forceRefresh: Boolean = false): List<String> {
    return fetchApks("https://github.com/Lime3DS/Lime3DS/releases", "a[href*=/releases/download/]", forceRefresh) { link: Element ->
        "https://github.com${link.attr("href")}"
    }
}

// Generic function to fetch APK links with caching and refresh support
private suspend fun fetchApks(url: String, selector: String, forceRefresh: Boolean, transform: (Element) -> String): List<String> {
    return withContext(Dispatchers.IO) {
        val cacheKey = url.hashCode().toString()

        if (!forceRefresh && apkCache.containsKey(cacheKey) && !isCacheExpired(cacheKey)) {
            println("Returning cached data for $url")
            return@withContext apkCache[cacheKey]!!
        }

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        val response = client.newCall(request).execute()
        val html = response.body?.string()
        val files = mutableListOf<String>()

        if (!html.isNullOrEmpty()) {
            val document = Jsoup.parse(html)
            val links = document.select(selector)

            for (link in links) {
                val downloadUrl = transform(link)
                files.add(downloadUrl)
                println("Found APK: $downloadUrl") // Log the download URL
            }

            // Update cache
            apkCache[cacheKey] = files
            cacheTimestamps[cacheKey] = System.currentTimeMillis()
        } else {
            println("Failed to fetch page or the HTML is empty for $url")
        }

        return@withContext files
    }
}

// Check if the cache is expired based on TTL
private fun isCacheExpired(cacheKey: String): Boolean {
    val currentTime = System.currentTimeMillis()
    val cacheTime = cacheTimestamps[cacheKey] ?: return true
    return currentTime - cacheTime > CACHE_TTL
}
