package com.naha.gpuemu.network

import com.naha.gpuemu.models.GithubRelease
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface GithubApiService {
    @GET("repos/K11MCH1/AdrenoToolsDrivers/releases")
    suspend fun getReleases(): List<GithubRelease>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.github.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val githubApiService: GithubApiService = retrofit.create(GithubApiService::class.java)
}