
package com.example.healthmate.repository

import android.content.Context
import com.example.healthmate.database.DatabaseHelper
import com.example.healthmate.model.HealthRecord
import com.example.healthmate.model.DailyGoals
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HealthRepository(context: Context) {

    private val databaseHelper = DatabaseHelper(
        context.applicationContext
    )

    // INSERT HEALTH RECORD
    fun insertRecord(record: HealthRecord): Long {
        return databaseHelper.insertRecord(record)
    }

    // GET ALL HEALTH RECORDS
    fun getAllRecords(): List<HealthRecord> {
        return databaseHelper.getAllRecords()
    }

    // GET RECORD BY ID
    fun getRecordById(id: Int): HealthRecord? {
        return databaseHelper.getRecordById(id)
    }

    // UPDATE HEALTH RECORD
    fun updateRecord(record: HealthRecord): Int {
        return databaseHelper.updateRecord(record)
    }

    // DELETE HEALTH RECORD
    fun deleteRecord(id: Int): Int {
        return databaseHelper.deleteRecord(id)
    }

    // SEARCH RECORDS BY DATE
    fun searchRecordsByDate(date: String): List<HealthRecord> {
        return databaseHelper.searchRecordsByDate(date)
    }

    // GET TODAY'S RECORDS
    fun getTodayRecords(): List<HealthRecord> {

        val today = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())

        return databaseHelper.searchRecordsByDate(today)
    }

    // GET RECORDS FOR LAST 7 DAYS
    fun getWeeklyRecords(): List<HealthRecord> {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        val startDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(calendar.time)

        return databaseHelper.getAllRecords()
            .filter { record ->
                record.date >= startDate
            }
            .sortedBy { it.date }
    }

    // GET RECORDS FOR LAST 30 DAYS
    fun getMonthlyRecords(): List<HealthRecord> {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -29)

        val startDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(calendar.time)

        return databaseHelper.getAllRecords()
            .filter { record ->
                record.date >= startDate
            }
            .sortedBy { it.date }
    }

    // GET DAILY GOALS
    fun getDailyGoals(): DailyGoals {
        return databaseHelper.getDailyGoals()
    }

    // UPDATE DAILY GOALS
    fun updateDailyGoals(goals: DailyGoals): Int {
        return databaseHelper.updateDailyGoals(goals)
    }

    // CLOSE DATABASE
    fun closeDatabase() {
        databaseHelper.close()
    }
}
