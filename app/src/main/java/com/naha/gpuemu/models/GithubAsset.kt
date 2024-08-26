package com.naha.gpuemu.models

import com.google.gson.annotations.SerializedName

data class GithubAsset(
    @SerializedName("name") val name: String,
    @SerializedName("browser_download_url") val browserDownloadUrl: String?
)