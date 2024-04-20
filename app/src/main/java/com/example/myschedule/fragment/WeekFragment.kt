package com.example.myschedule.fragment

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myschedule.R
import com.example.myschedule.customView.rectangleFormSchedule
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
    private val scheduleLists: MutableList<MutableList<Pair<rectangleFormSchedule,Int>>> = MutableList(3) { mutableListOf() }
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
        val dailyScheduleLiveData = myViewModel.getWeekSchedulesByDateAndType(sDate,eDate,0)
        val daySchedule = myViewModel.getWeekSchedulesByDateAndType(sDate,eDate,1)
        val periodScheduleLiveData = myViewModel.getWeekSchedulesByDateAndType(sDate,eDate,2)
        dailyScheduleLiveData.observe(viewLifecycleOwner){dailySchedules->
            removeSchedules(0)
            addSchedules(0,dailySchedules)
        }
        daySchedule.observe(viewLifecycleOwner){daySchedules->
            removeSchedules(1)
            addSchedules(1,daySchedules)
        }
        periodScheduleLiveData.observe(viewLifecycleOwner){periodSchedules->
            removeSchedules(2)
            addSchedules(2,periodSchedules)
        }

        return binding.root
    }

    private fun addSchedules(i:Int,schedules: List<Schedule>) {
        for (schedule in schedules) {
            val colorResourceId = rainbowColors[colorIndex % rainbowColors.size]
            val color = ContextCompat.getColor(requireContext(), colorResourceId)
            colorIndex++
            when (i) {
                0 -> {
                    for((j,frame) in frames.withIndex()){
                        val v : rectangleFormSchedule? = context?.let { rectangleFormSchedule(it,attrs = null,schedule,color,frames[j]) }
                        frame.addView(v)
                        v?.let { scheduleLists[i].add(Pair(it,j)) }
                    }
                }
                1 -> {
                    val calendar1 = Calendar.getInstance()
                    calendar1.set(
                        schedule.startDate.split("-")[0].toInt(),
                        schedule.startDate.split("-")[1].toInt()-1,
                        schedule.startDate.split("-")[2].toInt()
                    )
                    var frameIdx = calendar1.get(Calendar.DAY_OF_WEEK) - 2
                    if(frameIdx < 0) {
                        frameIdx += 7
                    }
                    val v : rectangleFormSchedule? = context?.let { rectangleFormSchedule(it,attrs = null,schedule,color,frames[frameIdx]) }
                    frames[frameIdx].addView(v)
                    v?.let { scheduleLists[i].add(Pair(it,frameIdx)) }
                }
                2 -> {
                    val sDay = if(sDate >= schedule.startDate) sDate else schedule.startDate
                    val eDay = if(eDate <= schedule.endDate) eDate else schedule.endDate
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
                    var frameIdx = calendar1.get(Calendar.DAY_OF_WEEK) - 2
                    if(frameIdx < 0) {
                        frameIdx += 7
                    }
                    while(calendar1.time<=calendar2.time){
                        val v : rectangleFormSchedule? = context?.let { rectangleFormSchedule(it,attrs = null,schedule,color,frames[frameIdx]) }
                        frames[frameIdx].addView(v)
                        v?.let { scheduleLists[i].add(Pair(it,frameIdx)) }
                        calendar1.add(Calendar.DAY_OF_MONTH, 1)
                        frameIdx++
                    }
                }
            }
        }
    }
    private fun removeSchedules(i: Int) {
        for (timePiece in scheduleLists[i]) {
            frames[timePiece.second].removeView(timePiece.first)
        }
        scheduleLists[i].clear()
    }
    fun refresh(){
        refreshIdx %= 3
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