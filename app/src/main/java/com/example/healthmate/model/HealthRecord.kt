package com.example.healthmate.model

data class HealthRecord(

    val id: Int = 0,

    val date: String,

    // Existing health data
    val steps: Int,
    val calories: Int,
    val water: Int,

    // New health tracking data
    val heartRate: Int = 0,
    val sleepHours: Float = 0f,
    val weightKg: Float = 0f
)
