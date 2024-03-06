package com.example.myschedule.fragment

import MyPeriodScheduleViewModel
import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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

class DayFragment : Fragment(){
    private lateinit var binding : DayLayoutBinding
    private lateinit var myDailyViewModel: MyDailyViewModel
    private lateinit var myViewModel: MyViewModel
    private lateinit var myPeriodScheduleViewModel: MyPeriodScheduleViewModel
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
        val periodSchedule = myPeriodScheduleViewModel.getScheduleByDate(date)
        dailyScheduleLiveData.observe(viewLifecycleOwner) { dailySchedules ->
            for ((i,schedule) in dailySchedules.withIndex()) {
                val v : TimePiece? = context?.let { TimePiece(it,attrs = null,schedule,rainbowColors[(i) % rainbowColors.size],binding) }
                v?.setOnClickListener(){
                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle("Delete Schedule")
                        .setMessage("Are you sure you want to delete this schedule?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            myDailyViewModel.viewModelScope.launch {
                                myDailyViewModel.deleteSchedule(schedule)
                            }
                            dialog.dismiss()
                            binding.watchCenter.removeView(v)
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                    alertDialog.show()
                }
                frame.addView(v)
            }
        }
        daySchedule.observe(viewLifecycleOwner){schedules ->
            //frameLayout 청소과정이필요할듯. 업데이트때마다 붙이면 무한으로 늘어난다. 어디 배열에넣어서 관리하다 한번에지우고 다시붙히기.
            for((i,schedule) in schedules.withIndex()){
                val v : TimePiece? = context?.let { TimePiece(it,attrs = null,schedule,rainbowColors[(i) % rainbowColors.size],binding) }
                v?.setOnClickListener(){
                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle("Delete Schedule")
                        .setMessage("Are you sure you want to delete this schedule?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            myViewModel.viewModelScope.launch {
                                myViewModel.deleteSchedule(schedule)
                            }
                            dialog.dismiss()
                            binding.watchCenter.removeView(v)
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                    alertDialog.show()
                }
                frame.addView(v)
            }
        }
        return binding.root
    }


}