package com.example.myschedule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.databinding.ScheduleItemMonth2Binding
import com.example.myschedule.db.Schedule

class MyScheduleAdapterMonth2(private val itemList: MutableList<Schedule>): RecyclerView.Adapter<MyScheduleAdapterMonth2.Holder>() {
    private var idx = 0
    private lateinit var context: Context
    private val rainbowColors = intArrayOf(
        R.color.rainbow1,
        R.color.rainbow2,
        R.color.rainbow3,
        R.color.rainbow4,
        R.color.rainbow5,
        R.color.rainbow6,
        R.color.rainbow7
    )
    inner class Holder(private val binding: ScheduleItemMonth2Binding) : RecyclerView.ViewHolder(binding.root) {
        val scheduleTitle = binding.title
        val backGround = binding.backGround
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScheduleItemMonth2Binding.inflate( LayoutInflater.from(parent.context),parent,false)
        context = parent.context
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.scheduleTitle.text = itemList[position].name
        val colorResourceId = rainbowColors[idx % rainbowColors.size]
        idx++
        val color = ContextCompat.getColor(context, colorResourceId)
        holder.backGround.setBackgroundColor(color)
    }
}