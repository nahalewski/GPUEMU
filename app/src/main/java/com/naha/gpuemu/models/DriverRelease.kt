package com.naha.gpuemu.models

import java.io.Serializable

data class DriverRelease(
    val name: String,
    val changelog: String,
    val downloadUrl: String,
    var isDownloaded: Boolean = false
) : Serializable