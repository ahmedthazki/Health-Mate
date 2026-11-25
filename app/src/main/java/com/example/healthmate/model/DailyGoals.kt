
package com.example.healthmate.model

data class DailyGoals(
    val stepsGoal: Int = 10000,
    val caloriesGoal: Int = 500,
    val waterGoal: Int = 2000,
    val sleepGoalHours: Float = 8f,
    val weightGoalKg: Float = 0f
)
