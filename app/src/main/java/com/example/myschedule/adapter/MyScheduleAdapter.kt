package com.example.myschedule.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.databinding.ScheduleItemBinding
import com.example.myschedule.db.Schedule

class MyScheduleAdapter(private val scheduleList: List<Schedule>) : RecyclerView.Adapter<MyScheduleAdapter.ScheduleViewHolder>(){
    private val checkedItems = HashSet<Schedule>()
    inner class ScheduleViewHolder(val binding: ScheduleItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(schedule: Schedule) {
            binding.title.text = schedule.name
            binding.content.text = schedule.content
            binding.date.text = schedule.date
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    toggleItemChecked(schedule)
                } else {
                    toggleItemChecked(schedule)
                }
            }
        }
        fun toggle(isChecked:Boolean){
            this.binding.checkbox.isChecked = isChecked
        }

    }
    fun toggleItemChecked(schedule: Schedule) {
        if (checkedItems.contains(schedule)) {
            checkedItems.remove(schedule)
        } else {
            checkedItems.add(schedule)
        }
    }

    fun getCheckedItems(): Set<Schedule> {
        return checkedItems
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
