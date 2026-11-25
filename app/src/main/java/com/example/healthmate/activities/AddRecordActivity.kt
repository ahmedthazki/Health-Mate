
package com.example.healthmate.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.healthmate.R
import com.example.healthmate.model.HealthRecord
import com.example.healthmate.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddRecordActivity : ComponentActivity() {

    private lateinit var viewModel: HealthViewModel

    private lateinit var tvRecordDate: TextView

    private lateinit var etSteps: EditText
    private lateinit var etCalories: EditText
    private lateinit var etWater: EditText

    private lateinit var etHeartRate: EditText
    private lateinit var etSleepHours: EditText
    private lateinit var etWeight: EditText

    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_record)

        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]

        // DATE
        tvRecordDate = findViewById(R.id.tvRecordDate)

        // EXISTING HEALTH INPUTS
        etSteps = findViewById(R.id.etSteps)
        etCalories = findViewById(R.id.etCalories)
        etWater = findViewById(R.id.etWater)

        // NEW HEALTH INPUTS
        etHeartRate = findViewById(R.id.etHeartRate)
        etSleepHours = findViewById(R.id.etSleepHours)
        etWeight = findViewById(R.id.etWeight)

        val btnSave = findViewById<Button>(R.id.btnSaveRecord)
        val btnCancel = findViewById<Button>(R.id.btnCancelRecord)

        // SET TODAY'S DATE
        selectedDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Calendar.getInstance().time)

        tvRecordDate.text = selectedDate

        // DATE PICKER
        tvRecordDate.setOnClickListener {
            showDatePicker()
        }

        // SAVE RECORD
        btnSave.setOnClickListener {
            saveRecord()
        }

        // CANCEL
        btnCancel.setOnClickListener {
            finish()
        }
    }

    // SHOW DATE PICKER
    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->

                selectedDate = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    day
                )

                tvRecordDate.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    // SAVE HEALTH RECORD
    private fun saveRecord() {

        val stepsText = etSteps.text.toString().trim()
        val caloriesText = etCalories.text.toString().trim()
        val waterText = etWater.text.toString().trim()

        val heartRateText = etHeartRate.text.toString().trim()
        val sleepText = etSleepHours.text.toString().trim()
        val weightText = etWeight.text.toString().trim()

        // VALIDATE REQUIRED FIELDS
        if (stepsText.isEmpty()) {
            etSteps.error = "Enter daily steps"
            etSteps.requestFocus()
            return
        }

        if (caloriesText.isEmpty()) {
            etCalories.error = "Enter calories burned"
            etCalories.requestFocus()
            return
        }

        if (waterText.isEmpty()) {
            etWater.error = "Enter water intake"
            etWater.requestFocus()
            return
        }

        // CONVERT VALUES
        val steps = stepsText.toIntOrNull()
        val calories = caloriesText.toIntOrNull()
        val water = waterText.toIntOrNull()

        val heartRate = if (heartRateText.isEmpty()) {
            0
        } else {
            heartRateText.toIntOrNull()
        }

        val sleepHours = if (sleepText.isEmpty()) {
            0f
        } else {
            sleepText.toFloatOrNull()
        }

        val weight = if (weightText.isEmpty()) {
            0f
        } else {
            weightText.toFloatOrNull()
        }

        // CHECK INVALID NUMBERS
        if (
            steps == null ||
            calories == null ||
            water == null ||
            heartRate == null ||
            sleepHours == null ||
            weight == null
        ) {
            Toast.makeText(
                this,
                "Please enter valid numbers",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // VALIDATE NON-NEGATIVE VALUES
        if (
            steps < 0 ||
            calories < 0 ||
            water < 0 ||
            heartRate < 0 ||
            sleepHours < 0f ||
            weight < 0f
        ) {
            Toast.makeText(
                this,
                "Values cannot be negative",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // CREATE HEALTH RECORD
        val record = HealthRecord(
            date = selectedDate,
            steps = steps,
            calories = calories,
            water = water,
            heartRate = heartRate,
            sleepHours = sleepHours,
            weightKg = weight
        )

        // SAVE THROUGH VIEWMODEL
        val success = viewModel.addRecord(record)

        if (success) {

            Toast.makeText(
                this,
                "Health record saved successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()

        } else {

            Toast.makeText(
                this,
                viewModel.message.value ?: "Unable to save health record",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
