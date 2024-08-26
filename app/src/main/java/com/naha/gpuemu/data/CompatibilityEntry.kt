package com.naha.gpuemu.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compatibility_entries")
data class CompatibilityEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameName: String,
    val versionNumber: String,
    val emulatorUsed: String,
    val emulatorVersion: String,
    val turnipDriverUsed: String,
    val status: String
)