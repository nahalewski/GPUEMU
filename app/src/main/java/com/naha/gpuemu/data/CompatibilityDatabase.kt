package com.naha.gpuemu.data


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [CompatibilityEntry::class], version = 1, exportSchema = false)
abstract class CompatibilityDatabase : RoomDatabase() {

    abstract fun compatibilityDao(): CompatibilityDao

    companion object {
        @Volatile
        private var INSTANCE: CompatibilityDatabase? = null

        fun getInstance(context: Context): CompatibilityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CompatibilityDatabase::class.java,
                    "compatibility_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}