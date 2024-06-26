package com.example.myschedule.fragment

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myschedule.R
import com.example.myschedule.customView.RectangleFormSchedule
import com.example.myschedule.databinding.WeekLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class WeekFragment : Fragment(){
    private lateinit var binding : WeekLayoutBinding
    private lateinit var sDate : String
    private lateinit var eDate : String
    private val scheduleLists: MutableList<MutableList<Pair<RectangleFormSchedule,Int>>> = MutableList(4) { mutableListOf() }
    private val myViewModel: MyViewModel by viewModels()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var colorIndex = 0
    private var refreshIdx = 0
    private lateinit var frames: Array<FrameLayout>
    private val rainbowColors = intArrayOf(
        R.color.rainbow1,
        R.color.rainbow2,
        R.color.rainbow3,
        R.color.rainbow4,
        R.color.rainbow5,
        R.color.rainbow6,
        R.color.rainbow7
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= WeekLayoutBinding.inflate(inflater)
        frames = arrayOf(binding.frame1,binding.frame2,binding.frame3,binding.frame4,binding.frame5,binding.frame6,binding.frame7)
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DAY_OF_YEAR, Calendar.MONDAY - dayOfWeek)
        sDate = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, 6)
        eDate = sdf.format(calendar.time)
        val scheduleData = myViewModel.getWeekSchedule(sDate,eDate)
        scheduleData.observe(viewLifecycleOwner){
            removeSchedules()
            addSchedules(it)
        }

        return binding.root
    }

    private fun addSchedules(schedules: List<Schedule>) {
        for (schedule in schedules) {
            val colorResourceId = rainbowColors[colorIndex % rainbowColors.size]
            val color = ContextCompat.getColor(requireContext(), colorResourceId)
            val i = schedule.type
            colorIndex++
            when (i) {
                0 -> {
                    if(schedule.startDate == "0"){
                        for((j,frame) in frames.withIndex()){
                            val v : RectangleFormSchedule? = context?.let { RectangleFormSchedule(it,attrs = null,schedule,color) }
                            frame.addView(v)
                            v?.let { scheduleLists[i].add(Pair(it,j)) }
                        }
                    }
                    else{
                        var frameIdx = schedule.startDate.toInt()-2
                        if(frameIdx < 0) {
                            frameIdx += 7
                        }
                        val v : RectangleFormSchedule? = context?.let { RectangleFormSchedule(it,attrs = null,schedule,color) }
                        frames[frameIdx].addView(v)
                        v?.let { scheduleLists[3].add(Pair(it,frameIdx)) }
                    }
                }
                1 -> {
                    val calendar1 = Calendar.getInstance()
                    val startDate = schedule.startDate.split("-").map { it.toInt() }
                    calendar1.set(
                        startDate[0],
                        startDate[1]-1,
                        startDate[2]
                    )
                    var frameIdx = calendar1.get(Calendar.DAY_OF_WEEK) - 2
                    if(frameIdx < 0) {
                        frameIdx += 7
                    }
                    val v : RectangleFormSchedule? = context?.let { RectangleFormSchedule(it,attrs = null,schedule,color) }
                    frames[frameIdx].addView(v)
                    v?.let { scheduleLists[i].add(Pair(it,frameIdx)) }
                }
                2 -> {
                    val sDay = (if(sDate >= schedule.startDate) sDate else schedule.startDate).split("-").map { it.toInt() }
                    val eDay = (if(eDate <= schedule.endDate) eDate else schedule.endDate).split("-").map { it.toInt() }
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
                    var frameIdx = calendar1.get(Calendar.DAY_OF_WEEK) - 2
                    if(frameIdx < 0) {
                        frameIdx += 7
                    }
                    while(calendar1.time<=calendar2.time){
                        val v : RectangleFormSchedule? = context?.let { RectangleFormSchedule(it,attrs = null,schedule,color) }
                        frames[frameIdx].addView(v)
                        v?.let { scheduleLists[i].add(Pair(it,frameIdx)) }
                        calendar1.add(Calendar.DAY_OF_MONTH, 1)
                        frameIdx++
                    }
                }
            }
        }
    }
    private fun removeSchedules() {
        for(i in 0 until 4){
            for (timePiece in scheduleLists[i]) {
                frames[timePiece.second].removeView(timePiece.first)
            }
            scheduleLists[i].clear()
        }
    }
    fun refresh(){
        refreshIdx %= 4
        for (timePiece in scheduleLists[refreshIdx]) {
            frames[timePiece.second].removeView(timePiece.first)
            frames[timePiece.second].addView(timePiece.first)
        }
        refreshIdx++
    }

    override fun onResume() {
        super.onResume()
        colorIndex = 0
        refreshIdx = 0
    }
}