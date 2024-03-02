package com.example.myschedule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.databinding.ScheduleItemBinding
import com.example.myschedule.db.Schedule
private lateinit var binding : ScheduleItemBinding

class MyScheduleAdapter(private val scheduleList: List<Schedule>) : RecyclerView.Adapter<MyScheduleAdapter.ScheduleViewHolder>(){
    inner class ScheduleViewHolder(private val binding: ScheduleItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(schedule: Schedule) {
            binding.title.text = schedule.name
            binding.content.text = schedule.content
            binding.date.text = schedule.date
        }
    }

    override fun getItemCount(): Int = scheduleList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ScheduleItemBinding.inflate(layoutInflater, parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }
}
