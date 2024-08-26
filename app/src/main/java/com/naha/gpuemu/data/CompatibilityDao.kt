package com.naha.gpuemu.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CompatibilityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CompatibilityEntry): Long

    @Update
    suspend fun update(entry: CompatibilityEntry)

    @Query("SELECT * FROM compatibility_entries WHERE gameName = :gameName AND versionNumber = :versionNumber")
    suspend fun getEntry(gameName: String, versionNumber: String): CompatibilityEntry?

    @Query("SELECT * FROM compatibility_entries")
    suspend fun getAllEntries(): List<CompatibilityEntry>
}