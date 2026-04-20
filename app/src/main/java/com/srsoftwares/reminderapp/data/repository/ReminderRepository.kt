package com.srsoftwares.reminderapp.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.srsoftwares.reminderapp.data.db.ReminderDatabase
import com.srsoftwares.reminderapp.data.model.Reminder
import com.srsoftwares.reminderapp.data.model.ReminderStatus

class ReminderRepository(context: Context) {

    private val dao = ReminderDatabase.getInstance(context).reminderDao()

    val allReminders: LiveData<List<Reminder>> = dao.getAllReminders()

    suspend fun insert(reminder: Reminder): Long = dao.insert(reminder)

    suspend fun update(reminder: Reminder) = dao.update(reminder)

    suspend fun delete(reminder: Reminder) = dao.delete(reminder)

    suspend fun updateStatus(id: Int, status: ReminderStatus) = dao.updateStatus(id, status)

    suspend fun expireOldReminders(now: Long) = dao.expireOldReminders(now)

    suspend fun getAllSync(): List<Reminder> = dao.getAllRemindersSync()

    suspend fun getById(id: Int): Reminder? = dao.getById(id)
}
