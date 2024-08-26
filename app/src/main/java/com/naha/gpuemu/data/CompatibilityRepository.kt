package com.naha.gpuemu.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CompatibilityRepository(
    private val compatibilityDao: CompatibilityDao
) {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllEntries(): List<CompatibilityEntry> {
        return compatibilityDao.getAllEntries()
    }

    suspend fun addEntry(entry: CompatibilityEntry): Boolean {
        // Check if entry already exists locally
        val existingEntry = compatibilityDao.getEntry(entry.gameName, entry.versionNumber)
        return if (existingEntry == null) {
            // Add to local database
            compatibilityDao.insert(entry)

            // Upload to Firestore
            try {
                firestore.collection("compatibilityEntries")
                    .document("${entry.gameName}_${entry.versionNumber}")
                    .set(entry)
                    .await() // Wait for the upload to complete
                true
            } catch (e: Exception) {
                // Handle upload failure, e.g., log it or notify the user
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }

    // New function to update an existing entry
    suspend fun updateEntry(entry: CompatibilityEntry): Boolean {
        // Update the local database
        compatibilityDao.update(entry)

        // Update Firestore
        return try {
            firestore.collection("compatibilityEntries")
                .document("${entry.gameName}_${entry.versionNumber}")
                .set(entry)
                .await() // Wait for the update to complete
            true
        } catch (e: Exception) {
            // Handle update failure, e.g., log it or notify the user
            e.printStackTrace()
            false
        }
    }
}