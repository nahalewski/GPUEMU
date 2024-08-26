package com.naha.gpuemu.models

import com.google.gson.annotations.SerializedName

data class GithubRelease(
    @SerializedName("name") val name: String,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("body") val body: String?,
    @SerializedName("assets") val assets: List<GithubAsset>
)

