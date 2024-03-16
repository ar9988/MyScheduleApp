package com.example.myschedule.fragment

import MyPeriodScheduleViewModel
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myschedule.R
import com.example.myschedule.customView.TimePiece
import com.example.myschedule.databinding.DayLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyViewModel
import com.example.myschedule.viewModel.MyDailyViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.log

class DayFragment : Fragment(){
    private lateinit var binding : DayLayoutBinding
    private lateinit var myDailyViewModel: MyDailyViewModel
    private lateinit var myViewModel: MyViewModel
    private lateinit var myPeriodScheduleViewModel: MyPeriodScheduleViewModel
    private val timePieceLists: MutableList<MutableList<TimePiece>> = MutableList(3) { mutableListOf() }
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private var calendar = Calendar.getInstance()
    private val rotationSensitivity = 0.03f
    private var prevX = 0f
    private var prevY = 0f
    private var dragFlag = false
    private val rainbowColors = intArrayOf(
        R.color.rainbow1,
        R.color.rainbow2,
        R.color.rainbow3,
        R.color.rainbow4,
        R.color.rainbow5,
        R.color.rainbow6,
        R.color.rainbow7
    )
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DayLayoutBinding.inflate(inflater)
        myDailyViewModel = ViewModelProvider(this)[MyDailyViewModel::class.java]
        myPeriodScheduleViewModel = ViewModelProvider(this)[MyPeriodScheduleViewModel::class.java]
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        val frame: FrameLayout = binding.watchCenter
        val touchScreen: FrameLayout = binding.touchScreen
        val dailyScheduleLiveData = myDailyViewModel.getAllSchedules()
        val date = sdf.format(calendar.time)
        val daySchedule = myViewModel.getScheduleByDate(date)
        val periodScheduleLiveData = myPeriodScheduleViewModel.getAllSchedules()
        var colorIndex = 0
        dailyScheduleLiveData.observe(viewLifecycleOwner) { dailySchedules ->
            removeTimePieces(0,frame)
            for (schedule in dailySchedules) {
                val colorResourceId = rainbowColors[colorIndex % rainbowColors.size]
                val color = ContextCompat.getColor(requireContext(), colorResourceId)
                colorIndex++
                val v : TimePiece? = context?.let { TimePiece(it,attrs = null,schedule,color,binding) }
                frame.addView(v)
                v?.let { timePieceLists[0].add(it) }
            }
        }
        daySchedule.observe(viewLifecycleOwner){schedules ->
            removeTimePieces(1,frame)
            for(schedule in schedules){
                val colorResourceId = rainbowColors[colorIndex % rainbowColors.size]
                val color = ContextCompat.getColor(requireContext(), colorResourceId)
                colorIndex++
                val v : TimePiece? = context?.let { TimePiece(it,attrs = null,schedule,color,binding) }
                frame.addView(v)
                v?.let { timePieceLists[1].add(it) }
            }
        }
        periodScheduleLiveData.observe(viewLifecycleOwner){schedules ->
            val searchedSchedule = search(schedules)
            removeTimePieces(2,frame)
            for(schedule in searchedSchedule){
                val colorResourceId = rainbowColors[colorIndex % rainbowColors.size]
                val color = ContextCompat.getColor(requireContext(), colorResourceId)
                colorIndex++
                val v : TimePiece? = context?.let { TimePiece(it,attrs = null,schedule,color,binding) }
                frame.addView(v)
                v?.let { timePieceLists[2].add(it) }
            }
        }
        touchScreen.setOnTouchListener{_, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.x
                    prevY = event.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.x - prevX
                    val deltaY = event.y - prevY
                    Log.d("move","$deltaX - $deltaY")
                    if(deltaX<1&&deltaY<1){
                        dragFlag=false
                    }
                    else{
                        val rotation = Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat() * rotationSensitivity
                        frame.rotation += rotation
                        dragFlag=true
                    }
                    prevX = event.x
                    prevY = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if(!dragFlag){clickEvent(binding,event)}
                    true
                }
                else -> false
            }
        }
        return binding.root
    }

    private fun search(schedules: List<Schedule>?): MutableList<Schedule> {
        val searchedSchedules: MutableList<Schedule> = mutableListOf()
        if (schedules != null) {
            for(schedule in schedules) {
                val calendar1 = Calendar.getInstance()
                val calendar2 = Calendar.getInstance()
                val date = Calendar.getInstance()
                val dates = schedule.date.split("-")
                calendar1.set(
                    dates[0].toInt(),
                    dates[1].toInt()-1,
                    dates[2].toInt()
                )
                calendar2.set(
                    dates[3].toInt(),
                    dates[4].toInt()-1,
                    dates[5].toInt()
                )
                val isInputDateBetweenCalendars = (date.timeInMillis >= calendar1.timeInMillis &&
                        date.timeInMillis <= calendar2.timeInMillis)
                if(isInputDateBetweenCalendars){
                    searchedSchedules.add(schedule)
                }
            }
        }
        return searchedSchedules
    }

    private fun removeTimePieces(i: Int, frame: FrameLayout) {
        for (timePiece in timePieceLists[i]) {
            frame.removeView(timePiece)
        }
        timePieceLists[i].clear()
    }
    private fun clickEvent(binding: DayLayoutBinding,event: MotionEvent){
        val touchScreen = binding.touchScreen
        val frame = binding.watchCenter
        val centerX = touchScreen.width / 2f
        val centerY = touchScreen.height / 2f
        val clickX = event.x
        val clickY = event.y
        val dx = clickX - centerX
        val dy = clickY - centerY
        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        if (angle < 0) {
            angle += 360f
        }
        val selectedItems: MutableList<Pair<TimePiece,Int>> = mutableListOf()
        for((index, list) in timePieceLists.withIndex()){
            for(item in list){
                val angles = item.getAngles()
                val startAngle = angles.first
                val endAngle = angles.second
                if(endAngle<startAngle){
                    if(angle>startAngle){
                        selectedItems.add(Pair(item, index))
                    }
                    else if(angle<startAngle&&angle<endAngle){
                        selectedItems.add(Pair(item, index))
                    }
                }
                else if (angle in startAngle..endAngle) {
                    selectedItems.add(Pair(item, index))
                }
            }
        }
        if(selectedItems.size>1){
            var index = 0
            for((i,item) in selectedItems.withIndex()){
                if(findNearestAngle(item.first,selectedItems[index].first,angle)){
                    index = i
                }
            }
            val selectedItem = selectedItems[index]
            val itemName = selectedItem.first.schedule.name
            val itemContent = selectedItem.first.schedule.content
            val times = selectedItem.first.schedule.times.split("-")
            val startTime = times[0]+"시"+times[1]+"분"
            val endTime = times[2]+"시"+times[3]+"분"
            AlertDialog.Builder(requireContext())
                .setTitle(itemName)
                .setMessage("$itemContent\n$startTime - $endTime\nDo you want to delete?")
                .setPositiveButton("Yes") { dialog, _ ->
                    frame.removeView(selectedItem.first)
                    when (selectedItem.second){
                        0->{
                            myDailyViewModel.viewModelScope.launch{
                                myDailyViewModel.deleteSchedule(selectedItem.first.schedule)
                            }
                        }
                        1->{
                            myViewModel.viewModelScope.launch {
                                myViewModel.deleteSchedule(selectedItem.first.schedule)
                            }
                        }
                        2->{
                            myPeriodScheduleViewModel.viewModelScope.launch {
                                myPeriodScheduleViewModel.deleteSchedule(selectedItem.first.schedule)
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        else if(selectedItems.size==1){
            val selectedItem = selectedItems[0]
            val itemName = selectedItem.first.schedule.name
            val itemContent = selectedItem.first.schedule.content
            val times = selectedItem.first.schedule.times.split("-")
            val startTime = times[0]+"시"+times[1]+"분"
            val endTime = times[2]+"시"+times[3]+"분"
            AlertDialog.Builder(requireContext())
                .setTitle(itemName)
                .setMessage("$itemContent\n$startTime - $endTime\nDo you want to delete?")
                .setPositiveButton("Yes") { dialog, _ ->
                    frame.removeView(selectedItem.first)
                    when (selectedItem.second){
                        0->{
                            myDailyViewModel.viewModelScope.launch{
                                myDailyViewModel.deleteSchedule(selectedItem.first.schedule)
                            }
                        }
                        1->{
                            myViewModel.viewModelScope.launch {
                                myViewModel.deleteSchedule(selectedItem.first.schedule)
                            }
                        }
                        2->{
                            myPeriodScheduleViewModel.viewModelScope.launch {
                                myPeriodScheduleViewModel.deleteSchedule(selectedItem.first.schedule)
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
    private fun findNearestAngle(item: TimePiece, item2: TimePiece,angle:Float):Boolean{
        val a = abs(item.getCentralAngle() - angle)
        val b = abs(item2.getCentralAngle() - angle)
        return a>b
    }
}

