package com.example.healthmate.activities

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

class EditRecordActivity : ComponentActivity() {

    private lateinit var viewModel: HealthViewModel

    private var recordId: Int = -1
    private var recordDate: String = ""

    private lateinit var tvEditDate: TextView
    private lateinit var etEditSteps: EditText
    private lateinit var etEditCalories: EditText
    private lateinit var etEditWater: EditText
    private lateinit var etEditHeartRate: EditText
    private lateinit var etEditSleepHours: EditText
    private lateinit var etEditWeight: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_record)

        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]

        tvEditDate = findViewById(R.id.tvEditDate)
        etEditSteps = findViewById(R.id.etEditSteps)
        etEditCalories = findViewById(R.id.etEditCalories)
        etEditWater = findViewById(R.id.etEditWater)
        etEditHeartRate = findViewById(R.id.etEditHeartRate)
        etEditSleepHours = findViewById(R.id.etEditSleepHours)
        etEditWeight = findViewById(R.id.etEditWeight)

        val btnUpdate = findViewById<Button>(R.id.btnUpdateRecord)
        val btnCancel = findViewById<Button>(R.id.btnCancelEdit)

        recordId = intent.getIntExtra("RECORD_ID", -1)

        if (recordId == -1) {
            Toast.makeText(
                this,
                "Invalid record",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        loadRecord()

        btnUpdate.setOnClickListener {
            updateRecord()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    // LOAD EXISTING RECORD
    private fun loadRecord() {

        val record = viewModel.getRecordById(recordId)

        if (record == null) {
            Toast.makeText(
                this,
                "Record not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        recordDate = record.date

        tvEditDate.text = record.date
        etEditSteps.setText(record.steps.toString())
        etEditCalories.setText(record.calories.toString())
        etEditWater.setText(record.water.toString())

        etEditHeartRate.setText(
            if (record.heartRate == 0) "" else record.heartRate.toString()
        )

        etEditSleepHours.setText(
            if (record.sleepHours == 0f) "" else record.sleepHours.toString()
        )

        etEditWeight.setText(
            if (record.weightKg == 0f) "" else record.weightKg.toString()
        )
    }

    // UPDATE RECORD
    private fun updateRecord() {

        val stepsText = etEditSteps.text.toString().trim()
        val caloriesText = etEditCalories.text.toString().trim()
        val waterText = etEditWater.text.toString().trim()

        val heartRateText = etEditHeartRate.text.toString().trim()
        val sleepHoursText = etEditSleepHours.text.toString().trim()
        val weightText = etEditWeight.text.toString().trim()

        if (stepsText.isEmpty()) {
            etEditSteps.error = "Enter daily steps"
            etEditSteps.requestFocus()
            return
        }

        if (caloriesText.isEmpty()) {
            etEditCalories.error = "Enter calories burned"
            etEditCalories.requestFocus()
            return
        }

        if (waterText.isEmpty()) {
            etEditWater.error = "Enter water intake"
            etEditWater.requestFocus()
            return
        }

        val steps = stepsText.toIntOrNull()
        val calories = caloriesText.toIntOrNull()
        val water = waterText.toIntOrNull()

        val heartRate = if (heartRateText.isEmpty()) {
            0
        } else {
            heartRateText.toIntOrNull()
        }

        val sleepHours = if (sleepHoursText.isEmpty()) {
            0f
        } else {
            sleepHoursText.toFloatOrNull()
        }

        val weightKg = if (weightText.isEmpty()) {
            0f
        } else {
            weightText.toFloatOrNull()
        }

        if (steps == null || calories == null || water == null ||
            heartRate == null || sleepHours == null || weightKg == null
        ) {
            Toast.makeText(
                this,
                "Enter valid numbers",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (steps < 0 || calories < 0 || water < 0 ||
            heartRate < 0 || sleepHours < 0f || weightKg < 0f
        ) {
            Toast.makeText(
                this,
                "Values cannot be negative",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (heartRate > 250 || sleepHours > 24f || weightKg > 500f) {
            Toast.makeText(
                this,
                "Please check your health values",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val updatedRecord = HealthRecord(
            id = recordId,
            date = recordDate,
            steps = steps,
            calories = calories,
            water = water,
            heartRate = heartRate,
            sleepHours = sleepHours,
            weightKg = weightKg
        )

        val success = viewModel.updateRecord(updatedRecord)

        if (success) {

            Toast.makeText(
                this,
                "Record updated successfully",
                Toast.LENGTH_SHORT
            ).show()

            setResult(RESULT_OK)
            finish()

        } else {

            Toast.makeText(
                this,
                "Unable to update record",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
