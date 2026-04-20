package com.srsoftwares.reminderapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.srsoftwares.reminderapp.data.model.ReminderStatus
import com.srsoftwares.reminderapp.data.repository.ReminderRepository
import com.srsoftwares.reminderapp.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val repo = ReminderRepository(context)
        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis()
            // Expire old reminders
            repo.expireOldReminders(now)
            // Reschedule active future reminders
            repo.getAllSync()
                .filter { it.status == ReminderStatus.ACTIVE && it.timeMillis > now && it.notificationEnabled }
                .forEach { AlarmScheduler.schedule(context, it) }
        }
    }
}
