package com.srsoftwares.reminderapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.srsoftwares.reminderapp.data.model.Reminder

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    companion object {
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        fun getInstance(context: Context): ReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminder_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
