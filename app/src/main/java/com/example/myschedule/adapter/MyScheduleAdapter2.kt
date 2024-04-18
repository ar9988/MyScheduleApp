package com.example.myschedule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.databinding.ScheduleItem2Binding
import com.example.myschedule.db.Schedule

class MyScheduleAdapter2(private val itemList: MutableList<Schedule>): RecyclerView.Adapter<MyScheduleAdapter2.Holder>() {
    inner class Holder(private val binding: ScheduleItem2Binding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.text1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScheduleItem2Binding.inflate( LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.title.text = itemList[position].name
    }
}