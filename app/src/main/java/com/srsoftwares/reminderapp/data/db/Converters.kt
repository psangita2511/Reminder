package com.srsoftwares.reminderapp.data.db

import androidx.room.TypeConverter
import com.srsoftwares.reminderapp.data.model.ReminderStatus

class Converters {
    @TypeConverter
    fun fromStatus(status: ReminderStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): ReminderStatus = ReminderStatus.valueOf(value)
}
