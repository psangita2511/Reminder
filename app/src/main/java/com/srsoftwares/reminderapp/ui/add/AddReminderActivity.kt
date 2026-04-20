package com.srsoftwares.reminderapp.ui.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.srsoftwares.reminderapp.data.model.Reminder
import com.srsoftwares.reminderapp.databinding.ActivityAddReminderBinding
import java.text.SimpleDateFormat
import java.util.*

class AddReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReminderBinding
    private val viewModel: AddReminderViewModel by viewModels()

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Reminder"
        binding.toolbar.setNavigationOnClickListener { finish() }

        updateDateTimeDisplays()
        setupListeners()
    }

    private fun setupListeners() {
        binding.tilDate.setEndIconOnClickListener { showDatePicker() }
        binding.etDate.setOnClickListener { showDatePicker() }

        binding.tilTime.setEndIconOnClickListener { showTimePicker() }
        binding.etTime.setOnClickListener { showTimePicker() }

        binding.btnSave.setOnClickListener { saveReminder() }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateDateTimeDisplays()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                updateDateTimeDisplays()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun updateDateTimeDisplays() {
        binding.etDate.setText(dateFormat.format(calendar.time))
        binding.etTime.setText(timeFormat.format(calendar.time))
    }

    private fun saveReminder() {
        val title = binding.etTitle.text?.toString()?.trim() ?: ""
        val name = binding.etName.text?.toString()?.trim() ?: ""

        if (title.isEmpty()) {
            binding.tilTitle.error = "Title is required"
            return
        } else {
            binding.tilTitle.error = null
        }
        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            return
        } else {
            binding.tilName.error = null
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Please select a future date & time", Toast.LENGTH_SHORT).show()
            return
        }

        val notificationEnabled = binding.switchNotification.isChecked

        val reminder = Reminder(
            title = title,
            name = name,
            timeMillis = calendar.timeInMillis,
            notificationEnabled = notificationEnabled
        )

        viewModel.saveReminder(reminder) {
            Toast.makeText(this, "Reminder saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
