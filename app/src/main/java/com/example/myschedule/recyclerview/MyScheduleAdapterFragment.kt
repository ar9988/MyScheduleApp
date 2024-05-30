package com.example.myschedule.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.databinding.ScheduleItemSheetBinding
import com.example.myschedule.db.Schedule

class MyScheduleAdapterFragment(
    private val scheduleList: List<Schedule>,
    private val context: Context
) : RecyclerView.Adapter<MyScheduleAdapterFragment.ScheduleViewHolder>(){
    inner class ScheduleViewHolder(val binding: ScheduleItemSheetBinding) : RecyclerView.ViewHolder(binding.root){
        val title = binding.title
        val content = binding.content
        val times = binding.times
    }

    override fun getItemCount(): Int = scheduleList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ScheduleItemSheetBinding.inflate(layoutInflater, parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.title.text = scheduleList[position].name
        holder.content.text = scheduleList[position].content
        val times = scheduleList[position].startTime.split("-")+scheduleList[position].endTime.split("-")
        holder.times.text = context.getString(
            R.string.adapter_text1,
            times[0],
            times[1],
            times[2],
            times[3]
        )
    }

}
