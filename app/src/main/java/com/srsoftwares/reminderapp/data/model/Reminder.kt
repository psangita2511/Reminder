package com.srsoftwares.reminderapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ReminderStatus {
    ACTIVE, COMPLETED, EXPIRED
}

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val name: String,
    val timeMillis: Long,           // epoch millis for alarm scheduling
    val notificationEnabled: Boolean,
    val status: ReminderStatus = ReminderStatus.ACTIVE
)
