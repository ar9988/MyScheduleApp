package com.example.myschedule.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myschedule.R
import com.example.myschedule.activity.MainActivity
import com.example.myschedule.customView.TimePiece
import com.example.myschedule.databinding.DayLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.atan2

@AndroidEntryPoint
class DayFragment : Fragment(){
    private lateinit var binding : DayLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    private val timePieceLists: MutableList<MutableList<TimePiece>> = MutableList(3) { mutableListOf() }
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private var calendar = Calendar.getInstance()
    private var prevX = 0f
    private var prevY = 0f
    private var dragFlag = false
    private var rotationAngle = 0f
    private var clockTimeIds = arrayOf(
        R.id.clockTime_0, R.id.clockTime_1, R.id.clockTime_2, R.id.clockTime_3,
        R.id.clockTime_4, R.id.clockTime_5, R.id.clockTime_6, R.id.clockTime_7,
        R.id.clockTime_8, R.id.clockTime_9, R.id.clockTime_10, R.id.clockTime_11,
        R.id.clockTime_12, R.id.clockTime_13, R.id.clockTime_14, R.id.clockTime_15,
        R.id.clockTime_16, R.id.clockTime_17, R.id.clockTime_18, R.id.clockTime_19,
        R.id.clockTime_20, R.id.clockTime_21, R.id.clockTime_22, R.id.clockTime_23
    )
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
        val activity = requireActivity()
        binding= DayLayoutBinding.inflate(inflater)
        //myViewModel = (activity as MainActivity).getMyViewModel()
        val frame: FrameLayout = binding.watchCenter
        val touchScreen: FrameLayout = binding.touchScreen
        val date = sdf.format(calendar.time)
        val dailyScheduleLiveData = myViewModel.getSchedulesByType(0)
        val daySchedule = myViewModel.getScheduleByDateAndType(date,1)
        val periodScheduleLiveData = myViewModel.getSchedulesByType(2)
        var colorIndex = 0
        val clock: ConstraintLayout = binding.clockLayout
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
        touchScreen.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.x
                    prevY = event.y
                    dragFlag = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val centerX = touchScreen.width / 2f
                    val centerY = touchScreen.height / 2f
                    val touchX = event.x
                    val touchY = event.y

                    val startAngle = Math.toDegrees(atan2(prevY - centerY, prevX - centerX).toDouble()).toFloat()
                    val endAngle = Math.toDegrees(atan2(touchY - centerY, touchX - centerX).toDouble()).toFloat()

                    val deltaAngle = endAngle - startAngle
                    val currentRotation = frame.rotation

                    if (abs(deltaAngle) >= 1) {
                        dragFlag = true
                    }
                    if (dragFlag) {
                        var rotation = deltaAngle
                        if (abs(deltaAngle) > 180) {
                            rotation = if (deltaAngle > 0) {
                                deltaAngle - 360
                            } else {
                                deltaAngle + 360
                            }
                        }
                        frame.rotation = currentRotation + rotation
                        clock.rotation = currentRotation + rotation
                        rotationAngle += rotation
                        if(rotationAngle>360F){
                            rotationAngle-=360F
                        }else if(rotationAngle<-360F){
                            rotationAngle+=360F
                        }
                        for(item in clockTimeIds){
                            val textView: TextView? = view?.findViewById(item)
                            textView!!.rotation-=rotation
                        }
                    }
                    prevX = touchX
                    prevY = touchY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!dragFlag) {
                        clickEvent(binding, event)
                    }
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
                val startAngle = normalizeAngle(angles.first+rotationAngle)
                val endAngle = normalizeAngle(angles.second+rotationAngle)
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
                    myViewModel.viewModelScope.launch {
                        myViewModel.deleteSchedule(selectedItem.first.schedule)
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
                    myViewModel.viewModelScope.launch {
                        myViewModel.deleteSchedule(selectedItem.first.schedule)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun normalizeAngle(fl: Float): Float {
        if(fl>360F){
            return fl-360F
        }else if(fl<0F){
            return fl+360F
        }
        return fl
    }

    private fun findNearestAngle(item: TimePiece, item2: TimePiece,angle:Float):Boolean{
        val a = abs(item.getCentralAngle() - angle)
        val b = abs(item2.getCentralAngle() - angle)
        return a>b
    }
    override fun onResume() {
        super.onResume()
        rotationAngle = 0F
    }
}

