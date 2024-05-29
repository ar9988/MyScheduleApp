package com.example.myschedule.fragment

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myschedule.R
import com.example.myschedule.application.extractMonth
import com.example.myschedule.application.extractYear
import com.example.myschedule.recyclerview.MyScheduleAdapterFragment
import com.example.myschedule.customView.MonthYearPickerDialog
import com.example.myschedule.recyclerview.MyScheduleAdapterMonth
import com.example.myschedule.databinding.MonthLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.recyclerview.GridItemDecoration
import com.example.myschedule.viewModel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MonthFragment :Fragment(){
    private lateinit var binding : MonthLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    private lateinit var schedules : LiveData<List<Schedule>>
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var itemTouchListener: RecyclerView.OnItemTouchListener? = null
    private var myAdapter : MyScheduleAdapterMonth? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= MonthLayoutBinding.inflate(inflater)
        val calendar = Calendar.getInstance()
        binding.calendarTxt.text=getString(
            R.string.calendar_text,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1
        )
        binding.datePicker.setOnClickListener {
            showMonthYearPicker()
        }
        binding.calendarLeftBtn.setOnClickListener{
            val items = binding.calendarTxt.text.split(" ")
            val currentYear = items[0].extractYear()
            val currentMonth = items[1].extractMonth()
            if(currentMonth!=0){
                binding.calendarTxt.text=getString(
                    R.string.calendar_text,
                    currentYear,
                    currentMonth
                )
            }
            else{
                binding.calendarTxt.text=getString(
                    R.string.calendar_text,
                    currentYear-1,
                    12
                )
            }
            setObserver()
        }
        binding.calendarRightBtn.setOnClickListener{
            val items = binding.calendarTxt.text.split(" ")
            val currentYear = items[0].extractYear()
            val currentMonth = items[1].extractMonth()
            if(currentMonth!=11){
                binding.calendarTxt.text=getString(
                    R.string.calendar_text,
                    currentYear,
                    currentMonth+2
                )
            }
            else{
                binding.calendarTxt.text=getString(
                    R.string.calendar_text,
                    currentYear+1,
                    1
                )
            }
            setObserver()
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val sDate = sdf.format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val eDate = sdf.format(calendar.time)
        schedules = myViewModel.getMonthSchedules(sDate,eDate)
        schedules.observe(viewLifecycleOwner){
            setCalendar()
        }
        return binding.root
    }

    private fun setCalendar() {
        val sortedSchedule : MutableList<MutableList<Schedule>> = sortingSchedules(schedules.value!!)
        val calendar = Calendar.getInstance()
        val items = binding.calendarTxt.text.split(" ")
        val currentYear = items[0].extractYear()
        val currentMonth = items[1].extractMonth()
        calendar.set(
            currentYear,
            currentMonth,
            1
        )
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if(dayOfWeek!=1) sortedSchedule.add(0, mutableListOf())
        val adapter = MyScheduleAdapterMonth(sortedSchedule,dayOfWeek)
        myAdapter = adapter
        val recyclerView = binding.recyclerView
        if (itemTouchListener != null) {
            recyclerView.removeOnItemTouchListener(itemTouchListener!!)
        }
        val layoutManager = GridLayoutManager(requireContext(),7)
        if(dayOfWeek!=1) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) {
                        dayOfWeek - 1
                    } else {
                        1
                    }
                }
            }
        }
        recyclerView.layoutManager = layoutManager
        if(recyclerView.itemDecorationCount>0) recyclerView.removeItemDecorationAt(0)
        binding.recyclerView.addItemDecoration(GridItemDecoration(1,7-(dayOfWeek-1)))
        recyclerView.adapter = adapter
        adapter.setParentRecyclerView(recyclerView)
        itemTouchListener = object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    val child = rv.findChildViewUnder(e.x, e.y)
                    if (child != null) {
                        val position = rv.getChildAdapterPosition(child)
                        val list = adapter.getItem(position)
                        if(list.size!=0){
                            val sheetAdapter = MyScheduleAdapterFragment(list)
                            val bottomSheet = MyBottomSheetFragment(sheetAdapter)
                            bottomSheet.show(requireActivity().supportFragmentManager, MyBottomSheetFragment.TAG)
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }
        }
        itemTouchListener?.let { recyclerView.addOnItemTouchListener(it) }
    }

    private fun sortingSchedules(schedules: List<Schedule>): MutableList<MutableList<Schedule>> {
        val calendar = Calendar.getInstance()
        val items = binding.calendarTxt.text.split(" ")
        val currentYear = items[0].extractYear()
        val currentMonth = items[1].extractMonth()
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
                val sDay = (if(sDate >= schedule.startDate) sDate else schedule.startDate).split("-").map { it.toInt() }
                val eDay = (if(sdf.format(calendar.time) <= schedule.endDate) sdf.format(calendar.time) else schedule.endDate).split("-").map { it.toInt() }
                val calendar1 = Calendar.getInstance()
                val calendar2 = Calendar.getInstance()
                calendar1.set(
                    sDay[0],
                    sDay[1]-1,
                    sDay[2]
                )
                calendar2.set(
                    eDay[0],
                    eDay[1]-1,
                    eDay[2],
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
            binding.calendarTxt.text=getString(
                R.string.calendar_text,
                year,
                month+1
            )
            setObserver()
        }
        dialog.show()
    }

    private fun setObserver() {
        val calendar = Calendar.getInstance()
        val items = binding.calendarTxt.text.split(" ")
        val currentYear = items[0].extractYear()
        val currentMonth = items[1].extractMonth()
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
        schedules.observe(viewLifecycleOwner){
            setCalendar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        itemTouchListener?.let { binding.recyclerView.removeOnItemTouchListener(it) }
        itemTouchListener = null
    }
    fun refresh(){
        myAdapter?.shiftItems()
    }
}
