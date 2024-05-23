package com.example.myschedule.fragment

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
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myschedule.R
import com.example.myschedule.customView.circularSectorFormSchedule
import com.example.myschedule.databinding.DayLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.atan2

@AndroidEntryPoint
class DayFragment : Fragment(){
    private lateinit var binding : DayLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    private val scheduleLists: MutableList<MutableList<circularSectorFormSchedule>> = MutableList(3) { mutableListOf() }
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private var calendar = Calendar.getInstance()
    private var prevX = 0f
    private var prevY = 0f
    private var dragFlag = false
    private var rotationAngle = 0f
    private var refreshIdx = 0
    private var colorIndex = 0
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
        binding= DayLayoutBinding.inflate(inflater)
        rotationAngle = savedInstanceState?.getFloat("rotationAngle") ?: 0f
        val frame: FrameLayout = binding.watchCenter
        val touchScreen: FrameLayout = binding.touchScreen
        val date = sdf.format(calendar.time)
        val dailyScheduleLiveData = myViewModel.getSchedulesByDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
        val daySchedule = myViewModel.getScheduleByDateAndType(date,1)
        val periodScheduleLiveData = myViewModel.getScheduleByDateAndType(date,2)
        val clock: ConstraintLayout = binding.clockLayout
        dailyScheduleLiveData.observe(viewLifecycleOwner) { dailySchedules ->
            removeSchedules(0,frame)
            addSchedules(0,frame,dailySchedules)
        }
        daySchedule.observe(viewLifecycleOwner){schedules ->
            removeSchedules(1,frame)
            addSchedules(1,frame,schedules)
        }
        periodScheduleLiveData.observe(viewLifecycleOwner){schedules ->
            removeSchedules(2,frame)
            addSchedules(2,frame,schedules)
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

    private fun addSchedules(i: Int, frame: FrameLayout, schedules: List<Schedule>) {
        for (schedule in schedules) {
            val colorResourceId = rainbowColors[colorIndex % rainbowColors.size]
            val color = ContextCompat.getColor(requireContext(), colorResourceId)
            colorIndex++
            val v : circularSectorFormSchedule? = context?.let { circularSectorFormSchedule(it,attrs = null,schedule,color,binding) }
            frame.addView(v)
            v?.let { scheduleLists[i].add(it) }
        }
    }

    private fun removeSchedules(i: Int, frame: FrameLayout) {
        for (timePiece in scheduleLists[i]) {
            frame.removeView(timePiece)
        }
        scheduleLists[i].clear()
    }
    private fun clickEvent(binding: DayLayoutBinding,event: MotionEvent){
        val touchScreen = binding.touchScreen
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
        val selectedItems: MutableList<Pair<circularSectorFormSchedule,Int>> = mutableListOf()
        for((index, list) in scheduleLists.withIndex()){
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
            val times = selectedItem.first.schedule.startTime.split("-")+selectedItem.first.schedule.endTime.split("-")
            val startTime = times[0]+"시"+times[1]+"분"
            val endTime = times[2]+"시"+times[3]+"분"
            AlertDialog.Builder(requireContext())
                .setTitle(itemName)
                .setMessage("$itemContent\n$startTime - $endTime\n")
                .setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        else if(selectedItems.size==1){
            val selectedItem = selectedItems[0]
            val itemName = selectedItem.first.schedule.name
            val itemContent = selectedItem.first.schedule.content
            val times = selectedItem.first.schedule.startTime.split("-")+selectedItem.first.schedule.endTime.split("-")
            val startTime = times[0]+"시"+times[1]+"분"
            val endTime = times[2]+"시"+times[3]+"분"
            AlertDialog.Builder(requireContext())
                .setTitle(itemName)
                .setMessage("$itemContent\n$startTime - $endTime\n")
                .setPositiveButton("확인") { dialog, _ ->
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

    private fun findNearestAngle(item: circularSectorFormSchedule, item2: circularSectorFormSchedule, angle:Float):Boolean{
        val a = abs(item.getCentralAngle() - angle)
        val b = abs(item2.getCentralAngle() - angle)
        return a>b
    }
    override fun onResume() {
        super.onResume()
        colorIndex = 0
        refreshIdx = 0
    }

    fun refresh(){
        refreshIdx %= 3
        for (timePiece in scheduleLists[refreshIdx]) {
            binding.watchCenter.removeView(timePiece)
            binding.watchCenter.addView(timePiece)
        }
        refreshIdx++
    }

}

