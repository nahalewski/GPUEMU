package com.naha.gpuemu.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class GithubRelease(
    val name: String,
    val body: String,
    val assets: List<GithubAsset>
)

data class GithubAsset(
    val name: String,
    val browser_download_url: String
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