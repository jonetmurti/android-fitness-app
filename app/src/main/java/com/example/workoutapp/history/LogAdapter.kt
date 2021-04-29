package com.example.workoutapp.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.R

class LogAdapter(
        private val logs: List<TrainingLog>
) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    class LogViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.trainingTypeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.log_item, parent, false)

        return LogViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val item = logs[position]
        holder.textView.text = "Cycling"
    }

}