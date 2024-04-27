package com.example.myschedule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.databinding.ScheduleItemMonthBinding
import com.example.myschedule.db.Schedule

class MyScheduleAdapterMonth(private val itemList: MutableList<MutableList<Schedule>>): RecyclerView.Adapter<MyScheduleAdapterMonth.Holder>() {
    inner class Holder(private val binding: ScheduleItemMonthBinding) : RecyclerView.ViewHolder(binding.root) {
        val date = binding.calendarDate
        val recyclerView = binding.recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScheduleItemMonthBinding.inflate( LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.date.text = (position+1).toString()
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        val adapter = MyScheduleAdapterMonth2(itemList[position])
        holder.recyclerView.adapter = adapter
    }
}