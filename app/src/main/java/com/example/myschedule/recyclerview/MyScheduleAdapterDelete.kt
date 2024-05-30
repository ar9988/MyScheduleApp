package com.example.myschedule.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.databinding.ScheduleItemDeleteBinding
import com.example.myschedule.db.Schedule

class MyScheduleAdapterDelete(
    private var scheduleList: List<Schedule>,
    private val context:Context
) : RecyclerView.Adapter<MyScheduleAdapterDelete.ScheduleViewHolder>(){
    private val checkedItems = HashSet<Schedule>()
    inner class ScheduleViewHolder(val binding: ScheduleItemDeleteBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(schedule: Schedule) {
            binding.title.text = schedule.name
            binding.content.text = schedule.content
            val times = schedule.startTime.split("-")+schedule.endTime.split("-")
            when(schedule.type){
                0->{
                    binding.date.text = context.getString(
                        R.string.adapter_text1,
                        times[0],
                        times[1],
                        times[2],
                        times[3]
                    )
                }
                1->{
                    binding.date.text = context.getString(
                        R.string.adapter_text2,
                        schedule.startDate,
                        times[0],
                        times[1],
                        times[2],
                        times[3]
                    )
                    //binding.date.text = schedule.startDate+"\n"+ "${times[0]}시 ${times[1]}분 ~ ${times[2]}시 ${times[3]}분"
                }
                2->{
                    binding.date.text = context.getString(
                        R.string.adapter_text3,
                        schedule.startDate,
                        schedule.endDate,
                        times[0],
                        times[1],
                        times[2],
                        times[3]
                    )
                    //binding.date.text = schedule.startDate+"~"+schedule.endDate+"\n"+ "${times[0]}시 ${times[1]}분 ~ ${times[2]}시 ${times[3]}분"
                }
            }
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    toggleItemChecked(schedule)
                } else {
                    toggleItemChecked(schedule)
                }
            }
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
        val binding = ScheduleItemDeleteBinding.inflate(layoutInflater, parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }

    fun updateData(newDataset : List<Schedule>){
        scheduleList = newDataset
        notifyDataSetChanged()
    }
}
