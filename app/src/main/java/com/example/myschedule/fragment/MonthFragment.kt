package com.example.myschedule.fragment

import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.adapter.MonthYearPickerDialog
import com.example.myschedule.adapter.MyScheduleAdapterMonth
import com.example.myschedule.databinding.MonthLayoutBinding
import com.example.myschedule.databinding.ScheduleItemMonthBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MonthFragment :Fragment(){
    private lateinit var binding : MonthLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    private lateinit var schedules : LiveData<List<Schedule>>
    private var scheduleObserver: Observer<List<Schedule>>? = null
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= MonthLayoutBinding.inflate(inflater)
        val calendar = Calendar.getInstance()
        binding.calendarTxt.text="${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH)+1}월"
        binding.datePicker.setOnClickListener {
            showMonthYearPicker()
        }
        binding.calendarLeftBtn.setOnClickListener{
            val items = binding.calendarTxt.text.split(" ")
            val currentYear = items[0].replace("[^0-9]".toRegex(), "").toInt()
            val currentMonth = items[1].replace("[^0-9]".toRegex(), "").toInt()-1
            if(currentMonth!=0){
                binding.calendarTxt.text="${currentYear}년 ${currentMonth}월"
            }
            else{
                binding.calendarTxt.text="${currentYear-1}년 ${12}월"
            }
            setObserver()
        }
        binding.calendarRightBtn.setOnClickListener{
            val items = binding.calendarTxt.text.split(" ")
            val currentYear = items[0].replace("[^0-9]".toRegex(), "").toInt()
            val currentMonth = items[1].replace("[^0-9]".toRegex(), "").toInt()-1
            if(currentMonth!=11){
                binding.calendarTxt.text="${currentYear}년 ${currentMonth+2}월"
            }
            else{
                binding.calendarTxt.text="${currentYear+1}년 ${1}월"
            }
            setObserver()
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val sDate = sdf.format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val eDate = sdf.format(calendar.time)
        schedules = myViewModel.getMonthSchedules(sDate,eDate)
        scheduleObserver?.let { schedules.removeObserver(it) }
        scheduleObserver = Observer {
            setCalendar()
        }
        schedules.observe(viewLifecycleOwner, scheduleObserver!!)
        return binding.root
    }

    private fun setCalendar() {
        val sortedSchedule : MutableList<MutableList<Schedule>> = sortingSchedules(schedules.value!!)
        val adapter = MyScheduleAdapterMonth(sortedSchedule)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),7)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                if(child!=null){
                    val position = rv.getChildAdapterPosition(child)
                    Log.d("Onclick",position.toString())
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }
        })
    }

    private fun sortingSchedules(schedules: List<Schedule>): MutableList<MutableList<Schedule>> {
        val calendar = Calendar.getInstance()
        val items = binding.calendarTxt.text.split(" ")
        val currentYear = items[0].replace("[^0-9]".toRegex(), "").toInt()
        val currentMonth = items[1].replace("[^0-9]".toRegex(), "").toInt()-1
        calendar.set(
            currentYear,
            currentMonth,
            1
        )
        val sDate = sdf.format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val idx = calendar.get(Calendar.DAY_OF_MONTH)
        val sortedSchedule : MutableList<MutableList<Schedule>> = MutableList(idx){mutableListOf()}
        for(schedule in schedules){
            if(schedule.type==1) {
                val i = schedule.startDate.split("-")[2].toInt()
                sortedSchedule[i - 1].add(schedule)
            }
            else{
                val sDay = if(sDate >= schedule.startDate) sDate else schedule.startDate
                val eDay = if(sdf.format(calendar.time) <= schedule.endDate) sdf.format(calendar.time) else schedule.endDate
                val calendar1 = Calendar.getInstance()
                val calendar2 = Calendar.getInstance()
                calendar1.set(
                    sDay.split("-")[0].toInt(),
                    sDay.split("-")[1].toInt()-1,
                    sDay.split("-")[2].toInt()
                )
                calendar2.set(
                    eDay.split("-")[0].toInt(),
                    eDay.split("-")[1].toInt()-1,
                    eDay.split("-")[2].toInt(),
                )
                while(calendar1.time<=calendar2.time){
                    val i = calendar1.get(Calendar.DAY_OF_MONTH)
                    sortedSchedule[i - 1].add(schedule)
                    calendar1.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        }

        return sortedSchedule
    }

    private fun showMonthYearPicker() {
        val dialog = MonthYearPickerDialog(requireContext(),binding) { year, month ->
            binding.calendarTxt.text="${year}년 ${month+1}월"
            setObserver()
        }
        dialog.show()
    }

    private fun setObserver() {
        val calendar = Calendar.getInstance()
        val items = binding.calendarTxt.text.split(" ")
        val currentYear = items[0].replace("[^0-9]".toRegex(), "").toInt()
        val currentMonth = items[1].replace("[^0-9]".toRegex(), "").toInt()-1
        calendar.set(
            currentYear,
            currentMonth,
            1
        )
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val sDate = sdf.format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val eDate = sdf.format(calendar.time)
        schedules = myViewModel.getMonthSchedules(sDate,eDate)
        scheduleObserver?.let { schedules.removeObserver(it) }
        scheduleObserver = Observer {
            setCalendar()
        }
        schedules.observe(viewLifecycleOwner, scheduleObserver!!)
    }

    fun refresh(){
        Log.d("Month","month called")
    }
}
