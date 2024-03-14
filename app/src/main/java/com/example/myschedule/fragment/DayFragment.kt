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
import com.example.myschedule.viewModel.MyViewModel
import com.example.myschedule.viewModel.MyDailyViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.atan2

class DayFragment : Fragment(){
    private lateinit var binding : DayLayoutBinding
    private lateinit var myDailyViewModel: MyDailyViewModel
    private lateinit var myViewModel: MyViewModel
    private lateinit var myPeriodScheduleViewModel: MyPeriodScheduleViewModel
    private val timePieceLists: MutableList<MutableList<TimePiece>> = MutableList(3) { mutableListOf() }
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private var calendar = Calendar.getInstance()
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
        val dailyScheduleLiveData = myDailyViewModel.getAllSchedules()
        val date = sdf.format(calendar.time)
        val daySchedule = myViewModel.getScheduleByDate(date)
        val periodScheduleLiveData = myPeriodScheduleViewModel.getScheduleByDate(date)
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
            removeTimePieces(2,frame)
            for(schedule in schedules){
                val colorResourceId = rainbowColors[colorIndex % rainbowColors.size]
                val color = ContextCompat.getColor(requireContext(), colorResourceId)
                colorIndex++
                val v : TimePiece? = context?.let { TimePiece(it,attrs = null,schedule,color,binding) }
                frame.addView(v)
                v?.let { timePieceLists[2].add(it) }
            }
        }
        frame.setOnTouchListener{ _ , event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val centerX = frame.width / 2f
                val centerY = frame.height / 2f
                val clickX = event.x
                val clickY = event.y

                val dx = clickX - centerX
                val dy = clickY - centerY

                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                if (angle < 0) {
                    angle += 360f
                }
                Log.d("TouchListener", "Angle: $angle degrees")
                true
            }else{
                false
            }
        }
        binding.watchCenter.setOnTouchListener{_, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val centerX = frame.width / 2f
                    val centerY = frame.height / 2f
                    val clickX = event.x
                    val clickY = event.y
                    val dx = clickX - centerX
                    val dy = clickY - centerY
                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    if (angle < 0) {
                        angle += 360f
                    }
                    Log.d("TouchListener", "Angle: $angle degrees")
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
                    if(selectedItems.size!=1){
                        //하나 이상일때 처리코드
                    }
                    else{
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
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    true
                }
                MotionEvent.ACTION_UP -> {
                    true
                }
                else -> false
            }
        }
        return binding.root
    }
    private fun removeTimePieces(i: Int, frame: FrameLayout) {
        for (timePiece in timePieceLists[i]) {
            frame.removeView(timePiece)
        }
        timePieceLists[i].clear()
    }

}

