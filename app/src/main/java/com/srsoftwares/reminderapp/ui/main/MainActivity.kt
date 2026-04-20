package com.srsoftwares.reminderapp.ui.main

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.srsoftwares.reminderapp.databinding.ActivityMainBinding
import com.srsoftwares.reminderapp.ui.add.AddReminderActivity
import com.srsoftwares.reminderapp.util.NotificationHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: ReminderAdapter

    private val notifPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) showNotifPermissionRationale()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Notification channel
        NotificationHelper.createChannel(this)

        // Request notification permission (Android 13+)
        requestNotificationPermission()

        setupRecyclerView()
        observeReminders()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddReminderActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Expire old reminders every time we come back
        viewModel.expireOldReminders()
    }

    private fun setupRecyclerView() {
        adapter = ReminderAdapter(
            onMarkCompleted = { reminder ->
                viewModel.markCompleted(reminder)
                Snackbar.make(binding.root, "Marked as completed", Snackbar.LENGTH_SHORT).show()
            },
            onDelete = { reminder ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Reminder")
                    .setMessage("Are you sure you want to delete \"${reminder.title}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteReminder(reminder)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun observeReminders() {
        viewModel.reminders.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) android.view.View.VISIBLE
            else android.view.View.GONE
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> Unit
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->
                    showNotifPermissionRationale()
                else -> notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showNotifPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission")
            .setMessage("Notifications are required to alert you about reminders. Please enable in Settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun checkExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(AlarmManager::class.java)
            if (!am.canScheduleExactAlarms()) {
                AlertDialog.Builder(this)
                    .setTitle("Exact Alarm Permission")
                    .setMessage("To schedule precise reminders, please allow exact alarms in Settings.")
                    .setPositiveButton("Open Settings") { _, _ ->
                        startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.fromParts("package", packageName, null)
                        })
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                false
            } else true
        } else true
    }
}
