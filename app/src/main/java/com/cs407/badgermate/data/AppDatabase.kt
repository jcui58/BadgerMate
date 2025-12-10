package com.cs407.badgermate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cs407.badgermate.data.profile.ProfileDao
import com.cs407.badgermate.data.profile.ProfileEntity
import com.cs407.badgermate.data.event.EventDao
import com.cs407.badgermate.data.event.EventEntity

@Database(
    entities = [
        ProfileEntity::class,
        EventEntity::class
    ],
    version = 4,  // 增加到版本4
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "badgermate_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}