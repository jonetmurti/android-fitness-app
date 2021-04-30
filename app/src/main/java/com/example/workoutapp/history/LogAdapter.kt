package com.example.workoutapp.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.databinding.LogItemBinding

class LogAdapter(
        private val listener: LogClickListener,
        private val logs: List<TrainingLog>
) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    class LogViewHolder(
        private val binding: LogItemBinding,
        private val listener: LogClickListener
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TrainingLog) {
            binding.trainingTypeText.text = item.type.capitalize()
            // TODO: change time start and time end text

            binding.root.setOnClickListener {
                listener.onClick(item)
            }
        }

        companion object{
            fun from(parent: ViewGroup, listener: LogClickListener): LogViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LogItemBinding.inflate(layoutInflater, parent, false)
                return LogViewHolder(binding, listener)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder.from(parent, listener)
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    interface LogClickListener {
        fun onClick(item: TrainingLog)
    }
}