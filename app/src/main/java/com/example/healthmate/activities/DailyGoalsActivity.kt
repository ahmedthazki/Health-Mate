
package com.example.healthmate.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.healthmate.R
import com.example.healthmate.model.DailyGoals
import com.example.healthmate.viewmodel.HealthViewModel

class DailyGoalsActivity : ComponentActivity() {

    private lateinit var viewModel: HealthViewModel

    private lateinit var etStepsGoal: EditText
    private lateinit var etCaloriesGoal: EditText
    private lateinit var etWaterGoal: EditText
    private lateinit var etSleepGoal: EditText
    private lateinit var etWeightGoal: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_daily_goals)

        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]

        etStepsGoal = findViewById(R.id.etStepsGoal)
        etCaloriesGoal = findViewById(R.id.etCaloriesGoal)
        etWaterGoal = findViewById(R.id.etWaterGoal)
        etSleepGoal = findViewById(R.id.etSleepGoal)
        etWeightGoal = findViewById(R.id.etWeightGoal)

        val btnSaveGoals = findViewById<Button>(R.id.btnSaveGoals)
        val btnCancelGoals = findViewById<Button>(R.id.btnCancelGoals)

        // LOAD SAVED GOALS
        viewModel.dailyGoals.observe(this) { goals ->

            etStepsGoal.setText(goals.stepsGoal.toString())
            etCaloriesGoal.setText(goals.caloriesGoal.toString())
            etWaterGoal.setText(goals.waterGoal.toString())
            etSleepGoal.setText(goals.sleepGoalHours.toString())

            etWeightGoal.setText(
                if (goals.weightGoalKg > 0f) {
                    goals.weightGoalKg.toString()
                } else {
                    ""
                }
            )
        }

        // SAVE GOALS
        btnSaveGoals.setOnClickListener {

            val stepsText = etStepsGoal.text.toString().trim()
            val caloriesText = etCaloriesGoal.text.toString().trim()
            val waterText = etWaterGoal.text.toString().trim()
            val sleepText = etSleepGoal.text.toString().trim()
            val weightText = etWeightGoal.text.toString().trim()

            val steps = stepsText.toIntOrNull()
            val calories = caloriesText.toIntOrNull()
            val water = waterText.toIntOrNull()
            val sleep = sleepText.toFloatOrNull()

            val weight = if (weightText.isEmpty()) {
                0f
            } else {
                weightText.toFloatOrNull()
            }

            // VALIDATION
            if (
                steps == null ||
                calories == null ||
                water == null ||
                sleep == null ||
                weight == null
            ) {

                Toast.makeText(
                    this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // CHECK ALLOWED LIMITS
            if (
                steps !in 1..100000 ||
                calories !in 1..10000 ||
                water !in 1..10000 ||
                sleep !in 1f..24f ||
                weight < 0f ||
                weight > 500f
            ) {

                Toast.makeText(
                    this,
                    "Please enter values within the allowed limits",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // CREATE DAILY GOALS
            val goals = DailyGoals(
                stepsGoal = steps,
                caloriesGoal = calories,
                waterGoal = water,
                sleepGoalHours = sleep,
                weightGoalKg = weight
            )

            // SAVE TO DATABASE
            if (viewModel.updateDailyGoals(goals)) {

                Toast.makeText(
                    this,
                    "Daily goals saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } else {

                Toast.makeText(
                    this,
                    viewModel.message.value ?: "Unable to save goals",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // CANCEL
        btnCancelGoals.setOnClickListener {
            finish()
        }
    }
}
