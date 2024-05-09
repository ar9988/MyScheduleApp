package com.example.myschedule.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.databinding.ScheduleItemSheetBinding
import com.example.myschedule.db.Schedule

class MyScheduleAdapterFragment(private val scheduleList: List<Schedule>) : RecyclerView.Adapter<MyScheduleAdapterFragment.ScheduleViewHolder>(){
    inner class ScheduleViewHolder(val binding: ScheduleItemSheetBinding) : RecyclerView.ViewHolder(binding.root){
//        fun bind(schedule: Schedule) {
//            binding.title.text = schedule.name
//            binding.content.text = schedule.content
//            val times = schedule.times.split("-")
//            binding.times.text = "${times[0]}시 ${times[1]}분 ~ ${times[2]}시 ${times[3]}분"
//        }
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
        //holder.bind(scheduleList[position])
        holder.title.text = scheduleList[position].name
        holder.content.text = scheduleList[position].content
        val times = scheduleList[position].times.split("-")
        holder.times.text = "${times[0]}시 ${times[1]}분 ~ ${times[2]}시 ${times[3]}분"
    }

}
