
package com.example.healthmate.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.R
import com.example.healthmate.adapter.HealthAdapter
import com.example.healthmate.model.HealthRecord
import com.example.healthmate.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class RecordsActivity : ComponentActivity() {

    private lateinit var viewModel: HealthViewModel
    private lateinit var adapter: HealthAdapter

    private lateinit var tvRecordCount: TextView
    private lateinit var tvEmptyRecords: TextView
    private lateinit var etSearchDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_records)

        viewModel = ViewModelProvider(this)[HealthViewModel::class.java]

        val recyclerView =
            findViewById<RecyclerView>(R.id.rvHealthRecords)

        tvRecordCount = findViewById(R.id.tvRecordCount)
        tvEmptyRecords = findViewById(R.id.tvEmptyRecords)
        etSearchDate = findViewById(R.id.etSearchDate)

        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnShowAll = findViewById<Button>(R.id.btnShowAll)
        val btnAddRecord = findViewById<Button>(R.id.btnAddRecord)

        adapter = HealthAdapter(
            emptyList(),
            onEditClick = { record ->
                val intent = Intent(this, EditRecordActivity::class.java)
                intent.putExtra("RECORD_ID", record.id)
                startActivity(intent)
            },
            onDeleteClick = { record ->
                confirmDelete(record)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observe health records
        viewModel.records.observe(this) { records ->
            displayRecords(records)
        }

        // Add new record
        btnAddRecord.setOnClickListener {
            startActivity(
                Intent(this, AddRecordActivity::class.java)
            )
        }

        // Search records by date
        btnSearch.setOnClickListener {

            val date = etSearchDate.text.toString().trim()

            if (date.isEmpty()) {
                etSearchDate.error = "Enter a date"
                return@setOnClickListener
            }

            if (!isValidDate(date)) {
                etSearchDate.error = "Enter a valid date (YYYY-MM-DD)"
                return@setOnClickListener
            }

            viewModel.searchByDate(date)
        }

        // Show all records
        btnShowAll.setOnClickListener {
            etSearchDate.text.clear()
            viewModel.loadRecords()
        }
    }

    // Display records and update record count
    private fun displayRecords(records: List<HealthRecord>) {

        adapter.updateRecords(records)

        tvRecordCount.text = getString(
            R.string.total_records,
            records.size.toString()
        )

        tvEmptyRecords.visibility =
            if (records.isEmpty()) View.VISIBLE else View.GONE
    }

    // Validate date format and actual calendar date
    private fun isValidDate(date: String): Boolean {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        dateFormat.isLenient = false

        return try {
            val parsedDate = dateFormat.parse(date)
            parsedDate != null &&
                    dateFormat.format(parsedDate) == date
        } catch (e: Exception) {
            false
        }
    }

    // Delete confirmation
    private fun confirmDelete(record: HealthRecord) {

        AlertDialog.Builder(this)
            .setTitle("Delete Record")
            .setMessage("Are you sure you want to delete this record?")
            .setPositiveButton("Delete") { _, _ ->

                val success = viewModel.deleteRecord(record.id)

                if (success) {
                    Toast.makeText(
                        this,
                        "Record deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Unable to delete record",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()

        if (::viewModel.isInitialized) {
            viewModel.refreshData()
        }
    }
}
