package com.marco.calignano.nocinoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Mix::class, Spirit::class, Spice::class],
    views = [MixSummary::class],
    version = 4, exportSchema = false
)
abstract class NocinoDatabase : RoomDatabase() {

    abstract fun nocinoDao(): NocinoDao

    companion object {
        @Volatile
        private var INSTANCE: NocinoDatabase? = null

        fun getDatabase(context: Context): NocinoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NocinoDatabase::class.java,
                    "nocino_database"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
