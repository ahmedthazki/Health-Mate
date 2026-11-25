package com.example.healthmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.healthmate.model.HealthRecord
import com.example.healthmate.model.DailyGoals
import com.example.healthmate.repository.HealthRepository

class HealthViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository = HealthRepository(application)

    // LIVE DATA
    val records = MutableLiveData<List<HealthRecord>>()
    val todayRecords = MutableLiveData<List<HealthRecord>>()
    val weeklyRecords = MutableLiveData<List<HealthRecord>>()
    val dailyGoals = MutableLiveData<DailyGoals>()
    val message = MutableLiveData<String>()

    init {
        refreshData()
        loadDailyGoals()
    }

    // LOAD ALL RECORDS
    fun loadRecords() {
        records.value = repository.getAllRecords()
    }

    // LOAD TODAY'S RECORDS
    fun loadTodayRecords() {
        todayRecords.value = repository.getTodayRecords()
    }

    // LOAD WEEKLY RECORDS
    fun loadWeeklyRecords() {
        weeklyRecords.value = repository.getWeeklyRecords()
    }

    // LOAD DAILY GOALS
    fun loadDailyGoals() {
        dailyGoals.value = repository.getDailyGoals()
    }

    // UPDATE DAILY GOALS
    fun updateDailyGoals(goals: DailyGoals): Boolean {

        if (
            goals.stepsGoal <= 0 ||
            goals.caloriesGoal <= 0 ||
            goals.waterGoal <= 0
        ) {
            message.value = "Daily goals must be greater than zero"
            return false
        }

        val result = repository.updateDailyGoals(goals)

        if (result > 0) {
            message.value = "Daily goals updated successfully"
            loadDailyGoals()
            return true
        }

        message.value = "Unable to update daily goals"
        return false
    }

    // GET RECORD BY ID
    fun getRecordById(id: Int): HealthRecord? {
        return repository.getRecordById(id)
    }

    // VALIDATE HEALTH RECORD
    private fun validateRecord(record: HealthRecord): Boolean {

        if (
            record.steps < 0 ||
            record.calories < 0 ||
            record.water < 0
        ) {
            message.value = "Health values cannot be negative"
            return false
        }

        if (record.heartRate < 0 || record.heartRate > 250) {
            message.value = "Please enter a valid heart rate"
            return false
        }

        if (
            record.sleepHours < 0f ||
            record.sleepHours > 24f
        ) {
            message.value = "Sleep duration must be between 0 and 24 hours"
            return false
        }

        if (
            record.weightKg < 0f ||
            record.weightKg > 500f
        ) {
            message.value = "Please enter a valid weight"
            return false
        }

        return true
    }

    // ADD HEALTH RECORD
    fun addRecord(record: HealthRecord): Boolean {

        if (!validateRecord(record)) {
            return false
        }

        val result = repository.insertRecord(record)

        if (result > 0) {
            message.value = "Health record added successfully"
            refreshData()
            return true
        }

        message.value = "Unable to add health record"
        return false
    }

    // UPDATE HEALTH RECORD
    fun updateRecord(record: HealthRecord): Boolean {

        if (!validateRecord(record)) {
            return false
        }

        val result = repository.updateRecord(record)

        if (result > 0) {
            message.value = "Health record updated successfully"
            refreshData()
            return true
        }

        message.value = "Unable to update health record"
        return false
    }

    // DELETE HEALTH RECORD
    fun deleteRecord(id: Int): Boolean {

        val result = repository.deleteRecord(id)

        if (result > 0) {
            message.value = "Health record deleted successfully"
            refreshData()
            return true
        }

        message.value = "Unable to delete health record"
        return false
    }

    // SEARCH RECORDS BY DATE
    fun searchByDate(date: String) {
        records.value = repository.searchRecordsByDate(date)
    }

    // REFRESH ALL DATA
    fun refreshData() {
        loadRecords()
        loadTodayRecords()
        loadWeeklyRecords()
    }

    override fun onCleared() {
        super.onCleared()
        repository.closeDatabase()
    }
}
