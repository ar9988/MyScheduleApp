package com.example.myschedule.activity

import MyPeriodScheduleViewModel
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myschedule.R
import com.example.myschedule.adapter.MyScheduleAdapter
import com.example.myschedule.databinding.DeleteLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyDailyViewModel
import com.example.myschedule.viewModel.MyViewModel
import java.lang.NullPointerException
import java.util.Calendar

class DeleteActivity : AppCompatActivity() {
    private lateinit var binding : DeleteLayoutBinding
    private lateinit var myDailyViewModel: MyDailyViewModel
    private lateinit var myViewModel: MyViewModel
    private lateinit var myPeriodScheduleViewModel: MyPeriodScheduleViewModel
    private var startDay: Calendar = Calendar.getInstance()
    private var endDay: Calendar = Calendar.getInstance()
    private lateinit var title:String
    private lateinit var content:String
    private lateinit var startTime:String
    private lateinit var endTime:String
    private lateinit var scheduleData : LiveData<List<Schedule>>
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = DeleteLayoutBinding.inflate(layoutInflater)
        myDailyViewModel = ViewModelProvider(this)[MyDailyViewModel::class.java]
        myPeriodScheduleViewModel = ViewModelProvider(this)[MyPeriodScheduleViewModel::class.java]
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        setContentView(binding.root)
        scheduleData = myDailyViewModel.getAllSchedules()
        val scheduleList = listOf(
            Schedule(1, "Meeting", "Discuss project progress", "2024-03-03", "10:00-11:00"),
            Schedule(2, "Lunch", "Meet with colleagues for lunch", "2024-03-03", "12:00-13:00"),
            Schedule(3, "Presentation", "Prepare slides for presentation", "2024-03-04", "14:00-15:00"),
            // 여기에 더 많은 더미 스케줄 데이터 추가 가능
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = MyScheduleAdapter(scheduleList)
        setupListeners()
    }

    private fun setupListeners() {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> scheduleData = myDailyViewModel.getAllSchedules()
                    1 -> scheduleData = myViewModel.getAllSchedules()
                    2 -> scheduleData = myPeriodScheduleViewModel.getAllSchedules()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.DatePicker.setOnClickListener { binding.DatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this@DeleteActivity,16973935, { _, year, month, day ->
                run {
                    binding.etYear.setText("$year")
                    binding.etMonth.setText("${month+1}")
                    binding.etDay.setText("$day")
                }
            }, year, month, day).show()
        } }
        binding.SearchBtn.setOnClickListener{
            if(scheduleData==null){
                Toast.makeText(this@DeleteActivity, "유형을 선택하세요", Toast.LENGTH_SHORT).show()
            }
            else{
//                val calendar = Calendar.getInstance()
//                val year:String? = binding.etYear.text?.toString()
//                val month:String? = binding.etMonth.text?.toString()
//                val day:String? = binding.etDay.text?.toString()
                try {
                    Log.d("test", scheduleData.value.toString())
                    binding.recyclerView.adapter = MyScheduleAdapter(scheduleData.value!!)
                }catch (e : NullPointerException){
                    Toast.makeText(this@DeleteActivity, "데이터 없음", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.deleteBtn.setOnClickListener{
            //schedule ID로 삭제
            binding.checkbox
        }
    }
}