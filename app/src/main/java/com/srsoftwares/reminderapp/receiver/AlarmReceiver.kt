package com.srsoftwares.reminderapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.srsoftwares.reminderapp.data.model.ReminderStatus
import com.srsoftwares.reminderapp.data.repository.ReminderRepository
import com.srsoftwares.reminderapp.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminder_id", -1)
        val title = intent.getStringExtra("reminder_title") ?: "Reminder"
        val name = intent.getStringExtra("reminder_name") ?: ""

        if (reminderId == -1) return

        // Show notification
        NotificationHelper.showNotification(context, reminderId, title, name)

        // Update status to EXPIRED in DB
        val repo = ReminderRepository(context)
        CoroutineScope(Dispatchers.IO).launch {
            repo.updateStatus(reminderId, ReminderStatus.EXPIRED)
        }
    }
}
