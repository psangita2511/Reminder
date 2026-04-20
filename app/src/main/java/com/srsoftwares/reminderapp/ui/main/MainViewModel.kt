package com.srsoftwares.reminderapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.srsoftwares.reminderapp.data.model.Reminder
import com.srsoftwares.reminderapp.data.model.ReminderStatus
import com.srsoftwares.reminderapp.data.repository.ReminderRepository
import com.srsoftwares.reminderapp.util.AlarmScheduler
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ReminderRepository(application)
    val reminders: LiveData<List<Reminder>> = repo.allReminders

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        AlarmScheduler.cancel(getApplication(), reminder.id)
        repo.delete(reminder)
    }

    fun markCompleted(reminder: Reminder) = viewModelScope.launch {
        AlarmScheduler.cancel(getApplication(), reminder.id)
        repo.updateStatus(reminder.id, ReminderStatus.COMPLETED)
    }

    fun expireOldReminders() = viewModelScope.launch {
        repo.expireOldReminders(System.currentTimeMillis())
    }
}
