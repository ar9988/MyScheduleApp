package com.example.myschedule.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.databinding.ScheduleItemMonthBinding
import com.example.myschedule.db.Schedule

class MyScheduleAdapterMonth(
    private var itemList: MutableList<MutableList<Schedule>>,
    private val dayOfWeek: Int,
    private val context: Context): RecyclerView.Adapter<MyScheduleAdapterMonth.Holder>() {
    private lateinit var parentRecyclerView: RecyclerView
    inner class Holder(binding: ScheduleItemMonthBinding) : RecyclerView.ViewHolder(binding.root) {
        val date = binding.calendarDate
        val recyclerView = binding.recyclerView
        var childAdapter: MyScheduleAdapterMonth2? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ScheduleItemMonthBinding.inflate( LayoutInflater.from(parent.context),parent,false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if(dayOfWeek!=1){
            if(position!=0) {
                holder.date.text = context.getString(R.string.int_placeholder,position)
                holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
                val adapter = MyScheduleAdapterMonth2(itemList[position])
                holder.childAdapter = adapter
                holder.recyclerView.adapter = adapter
            }
        }
        else{
            holder.date.text = context.getString(R.string.int_placeholder, position+1)
            holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            val adapter = MyScheduleAdapterMonth2(itemList[position])
            holder.recyclerView.adapter = adapter
        }
    }
    fun shiftItems() {
        for(i in 0 until  itemCount){
            val holder = parentRecyclerView.findViewHolderForAdapterPosition(i) as Holder
            holder.childAdapter?.shiftItems()
        }
    }
    fun getItem(position: Int):MutableList<Schedule>{
        return itemList[position]
    }

    fun setParentRecyclerView(recyclerView: RecyclerView) {
        this.parentRecyclerView = recyclerView
    }
}