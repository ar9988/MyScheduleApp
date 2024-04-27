package com.example.myschedule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.databinding.ScheduleItemMonth2Binding
import com.example.myschedule.db.Schedule

class MyScheduleAdapterMonth2(private val itemList: MutableList<Schedule>): RecyclerView.Adapter<MyScheduleAdapterMonth2.Holder>() {
    inner class Holder(private val binding: ScheduleItemMonth2Binding) : RecyclerView.ViewHolder(binding.root) {
        val scheduleTitle = binding.title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScheduleItemMonth2Binding.inflate( LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.scheduleTitle.text = itemList[position].name
    }
}