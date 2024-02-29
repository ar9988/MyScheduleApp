package com.example.myschedule.fragment

import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
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

    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private var calendar = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DayLayoutBinding.inflate(inflater)
        myDailyViewModel = ViewModelProvider(this)[MyDailyViewModel::class.java]
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        val frame: FrameLayout = binding.watchCenter
        val dailyScheduleLiveData = myDailyViewModel.getAllSchedules()
        dailyScheduleLiveData.observe(viewLifecycleOwner) { dailySchedules ->
            for (schedule in dailySchedules) {
                val v : TimePiece? = context?.let { TimePiece(it,attrs = null,schedule,R.color.rainbow7,binding) }
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
        val date = sdf.format(calendar.time)
        var daySchedule = myViewModel.getScheduleByDate(date)
        daySchedule.observe(viewLifecycleOwner){schedules ->
            //frameLayout 청소과정이필요할듯. 업데이트때마다 붙이면 무한으로 늘어난다. 어디 배열에넣어서 관리하다 한번에지우고 다시붙히기.
            for(schedule in schedules){
                val v : TimePiece? = context?.let { TimePiece(it,attrs = null,schedule,R.color.rainbow7,binding) }
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