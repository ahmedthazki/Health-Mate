
package com.example.healthmate

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.healthmate.activities.AddRecordActivity
import com.example.healthmate.activities.DailyGoalsActivity
import com.example.healthmate.activities.RecordsActivity
import com.example.healthmate.model.HealthRecord
import com.example.healthmate.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: HealthViewModel

    // Dashboard views
    private lateinit var tvTotalSteps: TextView
    private lateinit var tvTotalCalories: TextView
    private lateinit var tvTotalWater: TextView

    private lateinit var tvStepsGoal: TextView
    private lateinit var tvCaloriesGoal: TextView
    private lateinit var tvWaterGoal: TextView

    private lateinit var tvStepsPercentage: TextView
    private lateinit var tvCaloriesPercentage: TextView
    private lateinit var tvWaterPercentage: TextView

    private lateinit var progressSteps: ProgressBar
    private lateinit var progressCalories: ProgressBar
    private lateinit var progressWater: ProgressBar

    // Heart Rate views
    private lateinit var tvTotalHeartRate: TextView
    private lateinit var tvHeartRateTrend: TextView
    private lateinit var heartRateChartContainer: LinearLayout

    // Sleep views
    private lateinit var tvTotalSleep: TextView
    private lateinit var tvSleepGoalDisplay: TextView
    private lateinit var progressSleep: ProgressBar
    private lateinit var tvSleepPercentage: TextView

    // Weight views
    private lateinit var tvTotalWeight: TextView
    private lateinit var tvWeightTarget: TextView
    private lateinit var tvWeightTrend: TextView
    private lateinit var weightChartContainer: LinearLayout

    // Weekly chart views
    private lateinit var tvWeeklySummary: TextView
    private lateinit var weeklyChartContainer: LinearLayout

    // Daily totals
    private var currentSteps = 0
    private var currentCalories = 0
    private var currentWater = 0

    // Custom goals
    private var stepsGoal = 10000
    private var caloriesGoal = 500
    private var waterGoal = 2000

    private var sleepGoalHours = 8f
    private var weightGoalKg = 0f

    // Current health values
    private var currentSleepHours = 0f
    private var currentWeightKg = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]

        // Initialize dashboard views
        tvTotalSteps = findViewById(R.id.tvTotalSteps)
        tvTotalCalories = findViewById(R.id.tvTotalCalories)
        tvTotalWater = findViewById(R.id.tvTotalWater)

        tvStepsGoal = findViewById(R.id.tvStepsGoal)
        tvCaloriesGoal = findViewById(R.id.tvCaloriesGoal)
        tvWaterGoal = findViewById(R.id.tvWaterGoal)

        tvStepsPercentage = findViewById(R.id.tvStepsPercentage)
        tvCaloriesPercentage = findViewById(R.id.tvCaloriesPercentage)
        tvWaterPercentage = findViewById(R.id.tvWaterPercentage)

        progressSteps = findViewById(R.id.progressSteps)
        progressCalories = findViewById(R.id.progressCalories)
        progressWater = findViewById(R.id.progressWater)

        // Heart Rate views
        tvTotalHeartRate = findViewById(R.id.tvTotalHeartRate)
        tvHeartRateTrend = findViewById(R.id.tvHeartRateTrend)
        heartRateChartContainer =
            findViewById(R.id.heartRateChartContainer)

        // Sleep views
        tvTotalSleep = findViewById(R.id.tvTotalSleep)
        tvSleepGoalDisplay = findViewById(R.id.tvSleepGoalDisplay)
        progressSleep = findViewById(R.id.progressSleep)
        tvSleepPercentage = findViewById(R.id.tvSleepPercentage)

        // Weight views
        tvTotalWeight = findViewById(R.id.tvTotalWeight)
        tvWeightTarget = findViewById(R.id.tvWeightTarget)
        tvWeightTrend = findViewById(R.id.tvWeightTrend)
        weightChartContainer =
            findViewById(R.id.weightChartContainer)

        // Weekly chart views
        tvWeeklySummary = findViewById(R.id.tvWeeklySummary)
        weeklyChartContainer = findViewById(R.id.weeklyChartContainer)

        // Buttons
        val btnAddRecord = findViewById<Button>(R.id.btnAddRecord)
        val btnViewRecords = findViewById<Button>(R.id.btnViewRecords)
        val btnDailyGoals = findViewById<Button>(R.id.btnDailyGoals)

        // Observe dashboard data
        observeDashboardData()
        observeDailyGoals()
        observeAllRecords()
        observeWeeklyRecords()

        // Add Health Record
        btnAddRecord.setOnClickListener {
            startActivity(
                Intent(this, AddRecordActivity::class.java)
            )
        }

        // View Health Records
        btnViewRecords.setOnClickListener {
            startActivity(
                Intent(this, RecordsActivity::class.java)
            )
        }

        // Customize Daily Goals
        btnDailyGoals.setOnClickListener {
            startActivity(
                Intent(this, DailyGoalsActivity::class.java)
            )
        }
    }

    // TODAY'S HEALTH SUMMARY
    private fun observeDashboardData() {

        viewModel.todayRecords.observe(this) { records ->

            currentSteps = records.sumOf { it.steps }
            currentCalories = records.sumOf { it.calories }
            currentWater = records.sumOf { it.water }

            tvTotalSteps.text = currentSteps.toString()
            tvTotalCalories.text = currentCalories.toString()
            tvTotalWater.text = "$currentWater ml"

            // Latest heart rate recorded today
            val latestHeartRate = records
                .filter { it.heartRate > 0 }
                .maxByOrNull { it.id }

            tvTotalHeartRate.text =
                latestHeartRate?.let {
                    "${it.heartRate} bpm"
                } ?: "-- bpm"

            // Latest sleep duration recorded today
            val latestSleep = records
                .filter { it.sleepHours > 0f }
                .maxByOrNull { it.id }

            currentSleepHours = latestSleep?.sleepHours ?: 0f

            updateSleepDisplay()
            updateDashboardProgress()
        }
    }

    // LATEST WEIGHT FROM ALL RECORDS
    private fun observeAllRecords() {

        viewModel.records.observe(this) { records ->

            val latestWeight = records
                .filter { it.weightKg > 0f }
                .maxWithOrNull(
                    compareBy<HealthRecord> { it.date }
                        .thenBy { it.id }
                )

            currentWeightKg = latestWeight?.weightKg ?: 0f

            updateWeightDisplay()
        }
    }

    // CUSTOM DAILY GOALS
    private fun observeDailyGoals() {

        viewModel.dailyGoals.observe(this) { goals ->

            stepsGoal = goals.stepsGoal
            caloriesGoal = goals.caloriesGoal
            waterGoal = goals.waterGoal

            sleepGoalHours = goals.sleepGoalHours
            weightGoalKg = goals.weightGoalKg

            tvStepsGoal.text = "Goal: $stepsGoal steps"
            tvCaloriesGoal.text = "Goal: $caloriesGoal kcal"
            tvWaterGoal.text = "Goal: $waterGoal ml"

            updateSleepDisplay()
            updateWeightDisplay()
            updateDashboardProgress()
        }
    }

    // WEEKLY ACTIVITY DATA
    private fun observeWeeklyRecords() {

        viewModel.weeklyRecords.observe(this) { records ->

            updateWeeklyChart(records)
            updateHeartRateTrend(records)
            updateWeightTrend(records)
        }
    }

    // HEART RATE WEEKLY TREND
    private fun updateHeartRateTrend(records: List<HealthRecord>) {

        heartRateChartContainer.removeAllViews()

        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        val dailyHeartRates = mutableListOf<Int>()

        // Collect heart rate readings for the last 7 days
        for (i in 0 until 7) {

            val dateKey = dateFormat.format(calendar.time)

            val readings = records.filter {
                it.date == dateKey && it.heartRate > 0
            }

            val dailyAverage = if (readings.isNotEmpty()) {
                readings.map { it.heartRate }.average().toInt()
            } else {
                0
            }

            dailyHeartRates.add(dailyAverage)

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val validReadings = dailyHeartRates.filter { it > 0 }

        if (validReadings.isEmpty()) {
            tvHeartRateTrend.text = "No weekly readings"
        } else {
            val averageHeartRate =
                validReadings.average().toInt()

            tvHeartRateTrend.text =
                "Weekly average: $averageHeartRate bpm"
        }

        // Create compact weekly trend bars
        for (heartRate in dailyHeartRates) {

            val barHeight = if (heartRate > 0) {

                val minRate = validReadings.minOrNull() ?: heartRate
                val maxRate = validReadings.maxOrNull() ?: heartRate

                if (maxRate == minRate) {
                    24
                } else {
                    (
                            12 +
                                    ((heartRate - minRate).toFloat() /
                                            (maxRate - minRate) * 22)
                            ).toInt().coerceIn(12, 34)
                }

            } else {
                5
            }

            val barBackground = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dpToPx(4).toFloat()

                setColor(
                    Color.parseColor(
                        if (heartRate > 0) "#D95D75"
                        else "#F5DDE2"
                    )
                )
            }

            val bar = View(this).apply {
                background = barBackground

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    dpToPx(barHeight),
                    1f
                ).apply {
                    marginStart = dpToPx(3)
                    marginEnd = dpToPx(3)
                }
            }

            heartRateChartContainer.addView(bar)
        }
    }

    // WEIGHT TREND FOR THE LAST 7 DAYS
    private fun updateWeightTrend(records: List<HealthRecord>) {

        weightChartContainer.removeAllViews()

        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        val dailyWeights = mutableListOf<Float?>()

        // Get the latest weight reading for each day
        for (i in 0 until 7) {

            val dateKey = dateFormat.format(calendar.time)

            val latestDailyWeight = records
                .filter {
                    it.date == dateKey && it.weightKg > 0f
                }
                .maxByOrNull { it.id }

            dailyWeights.add(latestDailyWeight?.weightKg)

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val validWeights = dailyWeights.filterNotNull()

        // Display weekly reading information
        tvWeightTrend.text = if (validWeights.isEmpty()) {
            "No weekly readings"
        } else {
            "Last 7 days • ${validWeights.size} readings"
        }

        val minimumWeight = validWeights.minOrNull() ?: 0f
        val maximumWeight = validWeights.maxOrNull() ?: 0f

        // Create seven weight chart bars
        for (weight in dailyWeights) {

            val barHeight = if (weight != null) {

                if (maximumWeight == minimumWeight) {
                    25
                } else {
                    (
                            12 +
                                    ((weight - minimumWeight) /
                                            (maximumWeight - minimumWeight) * 22)
                            ).toInt().coerceIn(12, 34)
                }

            } else {
                4
            }

            val barBackground = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dpToPx(4).toFloat()

                setColor(
                    Color.parseColor(
                        if (weight != null) "#31AD8A"
                        else "#E1F1EB"
                    )
                )
            }

            val bar = View(this).apply {
                background = barBackground

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    dpToPx(barHeight),
                    1f
                ).apply {
                    marginStart = dpToPx(3)
                    marginEnd = dpToPx(3)
                }
            }

            weightChartContainer.addView(bar)
        }
    }

    // CREATE WEEKLY ACTIVITY CHART
    private fun updateWeeklyChart(records: List<HealthRecord>) {

        weeklyChartContainer.removeAllViews()

        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val dayFormat = SimpleDateFormat(
            "EEE",
            Locale.getDefault()
        )

        val calendar = Calendar.getInstance()

        // Find Monday of the current week
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        val daysFromMonday =
            (currentDay - Calendar.MONDAY + 7) % 7

        calendar.add(
            Calendar.DAY_OF_YEAR,
            -daysFromMonday
        )

        val weeklyData = mutableListOf<Pair<String, Int>>()

        // Collect Monday to Sunday records
        for (i in 0 until 7) {

            val dateKey = dateFormat.format(calendar.time)
            val dayName = dayFormat.format(calendar.time)

            val stepsForDay = records
                .filter { it.date == dateKey }
                .sumOf { it.steps }

            weeklyData.add(
                Pair(dayName, stepsForDay)
            )

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Weekly summary
        val totalWeeklySteps = weeklyData.sumOf {
            it.second
        }

        val today = Calendar.getInstance()

        val elapsedDays =
            ((today.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7) + 1

        val averageSteps = totalWeeklySteps / elapsedDays

        tvWeeklySummary.text =
            "This Week: $totalWeeklySteps steps | Daily Average: $averageSteps"

        val maximumSteps = weeklyData.maxOfOrNull {
            it.second
        } ?: 0

        // Create seven activity columns
        for ((dayName, steps) in weeklyData) {

            val dayColumn = LinearLayout(this).apply {

                orientation = LinearLayout.VERTICAL

                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    dpToPx(150),
                    1f
                )
            }

            // Display step count
            val stepsLabel = TextView(this).apply {

                text = when {
                    steps >= 1000 -> {
                        String.format(
                            Locale.getDefault(),
                            "%.1fk",
                            steps / 1000f
                        )
                    }

                    else -> steps.toString()
                }

                textSize = 9f
                gravity = Gravity.CENTER

                setTextColor(
                    Color.parseColor("#31594D")
                )
            }

            dayColumn.addView(stepsLabel)

            // Chart bar area
            val barArea = FrameLayout(this).apply {

                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(28),
                    dpToPx(105)
                )
            }

            // Calculate activity bar height
            val barHeight = if (maximumSteps > 0 && steps > 0) {

                ((steps.toFloat() / maximumSteps) * 100)
                    .toInt()
                    .coerceAtLeast(5)

            } else {
                3
            }

            // Rounded activity bar
            val barBackground = GradientDrawable().apply {

                shape = GradientDrawable.RECTANGLE

                cornerRadius = dpToPx(6).toFloat()

                setColor(
                    Color.parseColor(
                        if (steps > 0) "#31AD8A"
                        else "#E1F1EB"
                    )
                )
            }

            val activityBar = View(this).apply {

                background = barBackground

                layoutParams = FrameLayout.LayoutParams(
                    dpToPx(18),
                    dpToPx(barHeight),
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                )
            }

            barArea.addView(activityBar)

            dayColumn.addView(barArea)

            // Day name
            val dayLabel = TextView(this).apply {

                text = dayName

                textSize = 10f

                gravity = Gravity.CENTER

                setTextColor(
                    Color.parseColor("#31594D")
                )
            }

            dayColumn.addView(dayLabel)

            weeklyChartContainer.addView(dayColumn)
        }
    }

    // UPDATE SLEEP DISPLAY AND PROGRESS
    private fun updateSleepDisplay() {

        val actualSleep = if (currentSleepHours > 0f) {
            "${currentSleepHours} hrs"
        } else {
            "-- hrs"
        }

        tvTotalSleep.text = actualSleep

        tvSleepGoalDisplay.text =
            "Goal: $sleepGoalHours hrs"

        val sleepPercentage = if (
            currentSleepHours > 0f &&
            sleepGoalHours > 0f
        ) {
            (
                    (currentSleepHours / sleepGoalHours) * 100
                    ).toInt().coerceIn(0, 100)
        } else {
            0
        }

        progressSleep.max = 100
        progressSleep.progress = sleepPercentage

        tvSleepPercentage.text =
            "$sleepPercentage% completed"
    }

    // UPDATE WEIGHT DISPLAY
    private fun updateWeightDisplay() {

        val actualWeight = if (currentWeightKg > 0f) {
            "$currentWeightKg kg"
        } else {
            "-- kg"
        }

        val targetWeight = if (weightGoalKg > 0f) {
            "Target: $weightGoalKg kg"
        } else {
            "Target: Not set"
        }

        tvTotalWeight.text = actualWeight
        tvWeightTarget.text = targetWeight
    }

    // UPDATE DAILY PROGRESS BARS
    private fun updateDashboardProgress() {

        tvStepsPercentage.text =
            "${calculatePercentage(currentSteps, stepsGoal)}% completed"

        tvCaloriesPercentage.text =
            "${calculatePercentage(currentCalories, caloriesGoal)}% completed"

        tvWaterPercentage.text =
            "${calculatePercentage(currentWater, waterGoal)}% completed"

        progressSteps.max = stepsGoal
        progressCalories.max = caloriesGoal
        progressWater.max = waterGoal

        progressSteps.progress =
            currentSteps.coerceAtMost(stepsGoal)

        progressCalories.progress =
            currentCalories.coerceAtMost(caloriesGoal)

        progressWater.progress =
            currentWater.coerceAtMost(waterGoal)
    }

    // CALCULATE PERCENTAGE
    private fun calculatePercentage(
        actualValue: Int,
        goalValue: Int
    ): Int {

        if (goalValue <= 0) return 0

        return ((actualValue.toDouble() / goalValue) * 100)
            .toInt()
            .coerceIn(0, 100)
    }

    // CONVERT DP TO PIXELS
    private fun dpToPx(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    // REFRESH DATA
    override fun onResume() {
        super.onResume()

        if (::viewModel.isInitialized) {
            viewModel.refreshData()
            viewModel.loadDailyGoals()
            viewModel.loadWeeklyRecords()
        }
    }
}
