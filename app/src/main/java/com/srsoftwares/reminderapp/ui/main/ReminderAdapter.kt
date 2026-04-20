package com.srsoftwares.reminderapp.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.srsoftwares.reminderapp.R
import com.srsoftwares.reminderapp.data.model.Reminder
import com.srsoftwares.reminderapp.data.model.ReminderStatus
import com.srsoftwares.reminderapp.databinding.ItemReminderBinding
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(
    private val onMarkCompleted: (Reminder) -> Unit,
    private val onDelete: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder) = with(binding) {
            tvTitle.text = reminder.title
            tvName.text = reminder.name

            val cal = Calendar.getInstance().apply { timeInMillis = reminder.timeMillis }
            tvTime.text = timeFormat.format(cal.time)
            tvDate.text = dateFormat.format(cal.time)

            // Notification badge
            ivNotification.setImageResource(
                if (reminder.notificationEnabled) R.drawable.ic_bell_on
                else R.drawable.ic_bell_off
            )

            // Status chip
            when (reminder.status) {
                ReminderStatus.ACTIVE -> {
                    chipStatus.text = root.context.getString(R.string.status_active)
                    chipStatus.setChipBackgroundColorResource(R.color.status_active_bg)
                    chipStatus.setTextColor(root.context.getColor(R.color.status_active_text))
                    root.alpha = 1f
                }
                ReminderStatus.COMPLETED -> {
                    chipStatus.text = root.context.getString(R.string.status_completed)
                    chipStatus.setChipBackgroundColorResource(R.color.status_completed_bg)
                    chipStatus.setTextColor(root.context.getColor(R.color.status_completed_text))
                    root.alpha = 0.75f
                }
                ReminderStatus.EXPIRED -> {
                    chipStatus.text = root.context.getString(R.string.status_expired)
                    chipStatus.setChipBackgroundColorResource(R.color.status_expired_bg)
                    chipStatus.setTextColor(root.context.getColor(R.color.status_expired_text))
                    root.alpha = 0.6f
                }
            }

            // Actions
            btnComplete.isEnabled = reminder.status == ReminderStatus.ACTIVE
            btnComplete.setOnClickListener { onMarkCompleted(reminder) }
            btnDelete.setOnClickListener { onDelete(reminder) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem == newItem
    }
}
