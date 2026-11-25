package com.example.healthmate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.R
import com.example.healthmate.model.HealthRecord

class HealthAdapter(
    private var records: List<HealthRecord>,
    private val onEditClick: (HealthRecord) -> Unit,
    private val onDeleteClick: (HealthRecord) -> Unit
) : RecyclerView.Adapter<HealthAdapter.RecordViewHolder>() {

    class RecordViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val date: TextView = itemView.findViewById(R.id.tvRecordDate)
        val steps: TextView = itemView.findViewById(R.id.tvRecordSteps)
        val calories: TextView = itemView.findViewById(R.id.tvRecordCalories)
        val water: TextView = itemView.findViewById(R.id.tvRecordWater)

        val heartRate: TextView =
            itemView.findViewById(R.id.tvRecordHeartRate)

        val sleep: TextView =
            itemView.findViewById(R.id.tvRecordSleep)

        val weight: TextView =
            itemView.findViewById(R.id.tvRecordWeight)

        val editButton: Button =
            itemView.findViewById(R.id.btnEditRecord)

        val deleteButton: Button =
            itemView.findViewById(R.id.btnDeleteRecord)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_health_record, parent, false)

        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecordViewHolder,
        position: Int
    ) {

        val record = records[position]

        holder.date.text = "Date: ${record.date}"
        holder.steps.text = "Steps: ${record.steps}"
        holder.calories.text = "Calories: ${record.calories} kcal"
        holder.water.text = "Water: ${record.water} ml"

        holder.heartRate.text =
            if (record.heartRate > 0) {
                "Heart Rate: ${record.heartRate} BPM"
            } else {
                "Heart Rate: Not recorded"
            }

        holder.sleep.text =
            if (record.sleepHours > 0f) {
                "Sleep: ${record.sleepHours} hours"
            } else {
                "Sleep: Not recorded"
            }

        holder.weight.text =
            if (record.weightKg > 0f) {
                "Weight: ${record.weightKg} kg"
            } else {
                "Weight: Not recorded"
            }

        holder.editButton.setOnClickListener {
            onEditClick(record)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(record)
        }
    }

    override fun getItemCount(): Int {
        return records.size
    }

    fun updateRecords(newRecords: List<HealthRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }
}
