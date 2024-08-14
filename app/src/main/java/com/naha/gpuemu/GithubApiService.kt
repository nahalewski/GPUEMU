package com.naha.gpuemu.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class GithubRelease(
    val id: Long,                  // The unique ID of the release
    val name: String?,             // The name of the release
    val created_at: String,        // The release date in ISO 8601 format
    val body: String?,             // The description or changelog of the release
    val assets: List<GithubAsset>  // The list of assets associated with the release
)

data class GithubAsset(
    val name: String,                // The name of the asset (e.g., the file name)
    val browser_download_url: String // The URL to download the asset
)

interface GithubApiService {
    @GET("repos/K11MCH1/AdrenoToolsDrivers/releases")
    suspend fun getReleases(): List<GithubRelease>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.github.com/"

    val api: GithubApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApiService::class.java)
    }
}