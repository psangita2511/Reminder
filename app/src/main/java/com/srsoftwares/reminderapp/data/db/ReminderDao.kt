package com.srsoftwares.reminderapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.srsoftwares.reminderapp.data.model.Reminder
import com.srsoftwares.reminderapp.data.model.ReminderStatus

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY timeMillis ASC")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders ORDER BY timeMillis ASC")
    suspend fun getAllRemindersSync(): List<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("UPDATE reminders SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: ReminderStatus)

    @Query("UPDATE reminders SET status = 'EXPIRED' WHERE timeMillis < :now AND status = 'ACTIVE'")
    suspend fun expireOldReminders(now: Long)

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Int): Reminder?
}
