package com.example.healthmate.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.healthmate.model.DailyGoals
import com.example.healthmate.model.HealthRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {

        private const val DATABASE_NAME = "HealthMate.db"
        private const val DATABASE_VERSION = 5

        // HEALTH RECORDS TABLE
        const val TABLE_NAME = "health_records"

        const val COL_ID = "id"
        const val COL_DATE = "date"
        const val COL_STEPS = "steps"
        const val COL_CALORIES = "calories"
        const val COL_WATER = "water"

        // HEALTH TRACKING COLUMNS
        const val COL_HEART_RATE = "heart_rate"
        const val COL_SLEEP_HOURS = "sleep_hours"
        const val COL_WEIGHT_KG = "weight_kg"

        // DAILY GOALS TABLE
        private const val GOALS_TABLE = "daily_goals"

        private const val COL_GOAL_ID = "id"
        private const val COL_STEPS_GOAL = "steps_goal"
        private const val COL_CALORIES_GOAL = "calories_goal"
        private const val COL_WATER_GOAL = "water_goal"

        // NEW GOAL COLUMNS
        private const val COL_SLEEP_GOAL_HOURS = "sleep_goal_hours"
        private const val COL_WEIGHT_GOAL_KG = "weight_goal_kg"
    }

    // CREATE DATABASE TABLES
    override fun onCreate(db: SQLiteDatabase) {

        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DATE TEXT NOT NULL,
                $COL_STEPS INTEGER NOT NULL,
                $COL_CALORIES INTEGER NOT NULL,
                $COL_WATER INTEGER NOT NULL,
                $COL_HEART_RATE INTEGER NOT NULL DEFAULT 0,
                $COL_SLEEP_HOURS REAL NOT NULL DEFAULT 0,
                $COL_WEIGHT_KG REAL NOT NULL DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createTable)

        // Create Daily Goals Table
        createGoalsTable(db)

        // Insert Sample Records
        insertDummyRecords(db)
    }

    // DATABASE UPGRADE
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        // Upgrade from versions below 3
        if (oldVersion < 3) {
            createGoalsTable(db)
        }

        // Upgrade to version 4
        if (oldVersion < 4) {

            db.execSQL(
                """
                ALTER TABLE $TABLE_NAME
                ADD COLUMN $COL_HEART_RATE INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )

            db.execSQL(
                """
                ALTER TABLE $TABLE_NAME
                ADD COLUMN $COL_SLEEP_HOURS REAL NOT NULL DEFAULT 0
                """.trimIndent()
            )

            db.execSQL(
                """
                ALTER TABLE $TABLE_NAME
                ADD COLUMN $COL_WEIGHT_KG REAL NOT NULL DEFAULT 0
                """.trimIndent()
            )
        }

        // Upgrade to version 5
        if (oldVersion < 5) {

            val columns = mutableSetOf<String>()

            val cursor = db.rawQuery(
                "PRAGMA table_info($GOALS_TABLE)",
                null
            )

            cursor.use {
                while (it.moveToNext()) {
                    columns.add(it.getString(1))
                }
            }

            if (COL_SLEEP_GOAL_HOURS !in columns) {

                db.execSQL(
                    """
                    ALTER TABLE $GOALS_TABLE
                    ADD COLUMN $COL_SLEEP_GOAL_HOURS REAL NOT NULL DEFAULT 8
                    """.trimIndent()
                )
            }

            if (COL_WEIGHT_GOAL_KG !in columns) {

                db.execSQL(
                    """
                    ALTER TABLE $GOALS_TABLE
                    ADD COLUMN $COL_WEIGHT_GOAL_KG REAL NOT NULL DEFAULT 0
                    """.trimIndent()
                )
            }
        }
    }

    // CREATE DAILY GOALS TABLE
    private fun createGoalsTable(db: SQLiteDatabase) {

        val createTable = """
            CREATE TABLE IF NOT EXISTS $GOALS_TABLE (
                $COL_GOAL_ID INTEGER PRIMARY KEY,
                $COL_STEPS_GOAL INTEGER NOT NULL,
                $COL_CALORIES_GOAL INTEGER NOT NULL,
                $COL_WATER_GOAL INTEGER NOT NULL,
                $COL_SLEEP_GOAL_HOURS REAL NOT NULL DEFAULT 8,
                $COL_WEIGHT_GOAL_KG REAL NOT NULL DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createTable)

        // Insert Default Goals
        val values = ContentValues().apply {
            put(COL_GOAL_ID, 1)
            put(COL_STEPS_GOAL, 10000)
            put(COL_CALORIES_GOAL, 500)
            put(COL_WATER_GOAL, 2000)
            put(COL_SLEEP_GOAL_HOURS, 8f)
            put(COL_WEIGHT_GOAL_KG, 0f)
        }

        db.insertWithOnConflict(
            GOALS_TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    // GET DAILY GOALS
    fun getDailyGoals(): DailyGoals {

        val db = readableDatabase

        val cursor = db.query(
            GOALS_TABLE,
            null,
            "$COL_GOAL_ID = ?",
            arrayOf("1"),
            null,
            null,
            null
        )

        cursor.use {

            if (it.moveToFirst()) {

                return DailyGoals(

                    stepsGoal = it.getInt(
                        it.getColumnIndexOrThrow(COL_STEPS_GOAL)
                    ),

                    caloriesGoal = it.getInt(
                        it.getColumnIndexOrThrow(COL_CALORIES_GOAL)
                    ),

                    waterGoal = it.getInt(
                        it.getColumnIndexOrThrow(COL_WATER_GOAL)
                    ),

                    sleepGoalHours = it.getFloat(
                        it.getColumnIndexOrThrow(COL_SLEEP_GOAL_HOURS)
                    ),

                    weightGoalKg = it.getFloat(
                        it.getColumnIndexOrThrow(COL_WEIGHT_GOAL_KG)
                    )
                )
            }
        }

        return DailyGoals()
    }

    // UPDATE DAILY GOALS
    fun updateDailyGoals(goals: DailyGoals): Int {

        val db = writableDatabase

        val values = ContentValues().apply {

            put(COL_STEPS_GOAL, goals.stepsGoal)
            put(COL_CALORIES_GOAL, goals.caloriesGoal)
            put(COL_WATER_GOAL, goals.waterGoal)

            put(COL_SLEEP_GOAL_HOURS, goals.sleepGoalHours)
            put(COL_WEIGHT_GOAL_KG, goals.weightGoalKg)
        }

        return db.update(
            GOALS_TABLE,
            values,
            "$COL_GOAL_ID = ?",
            arrayOf("1")
        )
    }

    // INSERT HEALTH RECORD
    fun insertRecord(record: HealthRecord): Long {

        val db = writableDatabase

        val values = ContentValues().apply {

            put(COL_DATE, record.date)
            put(COL_STEPS, record.steps)
            put(COL_CALORIES, record.calories)
            put(COL_WATER, record.water)

            put(COL_HEART_RATE, record.heartRate)
            put(COL_SLEEP_HOURS, record.sleepHours)
            put(COL_WEIGHT_KG, record.weightKg)
        }

        return db.insert(TABLE_NAME, null, values)
    }

    // GET ALL HEALTH RECORDS
    fun getAllRecords(): List<HealthRecord> {

        val records = mutableListOf<HealthRecord>()

        val db = readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COL_DATE DESC, $COL_ID DESC"
        )

        cursor.use {

            while (it.moveToNext()) {
                records.add(cursorToRecord(it))
            }
        }

        return records
    }

    // GET RECORD BY ID
    fun getRecordById(id: Int): HealthRecord? {

        val db = readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use {

            if (it.moveToFirst()) {
                return cursorToRecord(it)
            }
        }

        return null
    }

    // UPDATE HEALTH RECORD
    fun updateRecord(record: HealthRecord): Int {

        val db = writableDatabase

        val values = ContentValues().apply {

            put(COL_DATE, record.date)
            put(COL_STEPS, record.steps)
            put(COL_CALORIES, record.calories)
            put(COL_WATER, record.water)

            put(COL_HEART_RATE, record.heartRate)
            put(COL_SLEEP_HOURS, record.sleepHours)
            put(COL_WEIGHT_KG, record.weightKg)
        }

        return db.update(
            TABLE_NAME,
            values,
            "$COL_ID = ?",
            arrayOf(record.id.toString())
        )
    }

    // DELETE HEALTH RECORD
    fun deleteRecord(id: Int): Int {

        val db = writableDatabase

        return db.delete(
            TABLE_NAME,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    // SEARCH RECORDS BY DATE
    fun searchRecordsByDate(date: String): List<HealthRecord> {

        val records = mutableListOf<HealthRecord>()

        val db = readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_DATE = ?",
            arrayOf(date),
            null,
            null,
            "$COL_ID DESC"
        )

        cursor.use {

            while (it.moveToNext()) {
                records.add(cursorToRecord(it))
            }
        }

        return records
    }

    // CONVERT CURSOR TO HEALTH RECORD
    private fun cursorToRecord(cursor: Cursor): HealthRecord {

        return HealthRecord(

            id = cursor.getInt(
                cursor.getColumnIndexOrThrow(COL_ID)
            ),

            date = cursor.getString(
                cursor.getColumnIndexOrThrow(COL_DATE)
            ),

            steps = cursor.getInt(
                cursor.getColumnIndexOrThrow(COL_STEPS)
            ),

            calories = cursor.getInt(
                cursor.getColumnIndexOrThrow(COL_CALORIES)
            ),

            water = cursor.getInt(
                cursor.getColumnIndexOrThrow(COL_WATER)
            ),

            heartRate = cursor.getInt(
                cursor.getColumnIndexOrThrow(COL_HEART_RATE)
            ),

            sleepHours = cursor.getFloat(
                cursor.getColumnIndexOrThrow(COL_SLEEP_HOURS)
            ),

            weightKg = cursor.getFloat(
                cursor.getColumnIndexOrThrow(COL_WEIGHT_KG)
            )
        )
    }

    // INSERT DUMMY HEALTH RECORDS
    private fun insertDummyRecords(db: SQLiteDatabase) {

        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        for (day in 0..2) {

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -day)

            val date = dateFormat.format(calendar.time)

            val values = ContentValues().apply {

                put(COL_DATE, date)
                put(COL_STEPS, 5000 + (day * 1200))
                put(COL_CALORIES, 250 + (day * 50))
                put(COL_WATER, 1500 + (day * 250))

                put(COL_HEART_RATE, 0)
                put(COL_SLEEP_HOURS, 0f)
                put(COL_WEIGHT_KG, 0f)
            }

            db.insert(TABLE_NAME, null, values)
        }
    }
}
