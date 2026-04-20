package com.srsoftwares.reminderapp.ui.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.srsoftwares.reminderapp.data.model.Reminder
import com.srsoftwares.reminderapp.data.repository.ReminderRepository
import com.srsoftwares.reminderapp.util.AlarmScheduler
import kotlinx.coroutines.launch

class AddReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ReminderRepository(application)

    fun saveReminder(reminder: Reminder, onSaved: (Long) -> Unit) = viewModelScope.launch {
        val id = repo.insert(reminder)
        // Schedule alarm with real DB id
        val saved = reminder.copy(id = id.toInt())
        AlarmScheduler.schedule(getApplication(), saved)
        onSaved(id)
    }
}
