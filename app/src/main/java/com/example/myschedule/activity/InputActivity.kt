package com.example.myschedule.activity

import MyPeriodScheduleViewModel
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.example.myschedule.databinding.InputLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyDailyViewModel
import com.example.myschedule.viewModel.MyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class InputActivity: AppCompatActivity()  {
    private lateinit var binding : InputLayoutBinding
    private lateinit var myDailyViewModel: MyDailyViewModel
    private lateinit var myViewModel: MyViewModel
    private lateinit var myPeriodScheduleViewModel: MyPeriodScheduleViewModel
    private var startDay: Calendar = Calendar.getInstance()
    private var endDay: Calendar = Calendar.getInstance()
    private lateinit var title:String
    private lateinit var content:String
    private lateinit var startTime:String
    private lateinit var endTime:String
    private var state = 0
    private var id=0
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private lateinit var frames:Array<ConstraintLayout>
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        myDailyViewModel = ViewModelProvider(this)[MyDailyViewModel::class.java]
        myPeriodScheduleViewModel = ViewModelProvider(this)[MyPeriodScheduleViewModel::class.java]
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        binding = InputLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        frames = arrayOf(binding.frame0, binding.frame1, binding.frame2)
        activateState(0)
        setupListeners()
        deactivateCalendar()
    }

    private fun setupListeners() {
        binding.apply {
            datePicker.setOnClickListener { datePicker.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                DatePickerDialog(this@InputActivity,16973935, { _, year, month, day ->
                    run {
                        binding.etYear.setText("$year")
                        binding.etMonth.setText("${month+1}")
                        binding.etDay.setText("$day")
                    }
                }, year, month, day).show()
            } }
            datePicker2.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                DatePickerDialog(this@InputActivity,16973935, { _, year, month, day ->
                    run {
                        binding.etYear2.setText("$year")
                        binding.etMonth2.setText("${month+1}")
                        binding.etDay2.setText("$day")
                    }
                }, year, month, day).show()
            }
            timePicker1.setOnClickListener {
                val timeSetListener = TimePickerDialog.OnTimeSetListener{_,hourOfDay,minute ->
                    binding.etHour2.setText("$hourOfDay")
                    binding.etMinute2.setText("$minute")
                }
                TimePickerDialog(this@InputActivity,16973935,timeSetListener,0,0,true).show()
            }
            timePicker0.setOnClickListener {
                val timeSetListener = TimePickerDialog.OnTimeSetListener{_,hourOfDay,minute ->
                    binding.etHour1.setText("$hourOfDay")
                    binding.etMinute1.setText("$minute")
                }
                TimePickerDialog(this@InputActivity,16973935,timeSetListener,0,0,true).show()
            }
            rgBtn1.setOnClickListener {
                id=0
                deactivateCalendar()
            }
            rgBtn2.setOnClickListener {
                id=1
                deactivateCalendar()
                activateCalendar(1)
            }
            rgBtn3.setOnClickListener {
                id=2
                deactivateCalendar()
                activateCalendar(2)
            }
            btnInput.setOnClickListener {
                when(state){
                    0->{
                        val yearText = binding.etYear.text.toString()
                        val monthText = binding.etMonth.text.toString()
                        val dayText = binding.etDay.text.toString()
                        val yearText2 = binding.etYear2.text.toString()
                        val monthText2 = binding.etMonth2.text.toString()
                        val dayText2 = binding.etDay2.text.toString()
                        when(id){
                            0->{
                                state++
                                activateState(state)
                            }
                            1 ->{
                                if (yearText.isEmpty() || monthText.isEmpty() || dayText.isEmpty()) {
                                    Toast.makeText(this@InputActivity, "년, 월, 일을 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    startDay.set(
                                        yearText.toInt(),
                                        monthText.toInt()-1,
                                        dayText.toInt()
                                    )
                                    state++
                                    activateState(state)
                                }
                            }
                            2->{
                                if (yearText.isEmpty() || monthText.isEmpty() || dayText.isEmpty() ||yearText2.isEmpty() || monthText2.isEmpty() || dayText2.isEmpty()) {
                                    Toast.makeText(this@InputActivity, "년, 월, 일을 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    startDay.set(
                                        yearText.toInt(),
                                        monthText.toInt()-1,
                                        dayText.toInt()
                                    )
                                    endDay.set(
                                        yearText2.toInt(),
                                        monthText2.toInt()-1,
                                        dayText2.toInt()
                                    )
                                    if(endDay.after(startDay)){
                                        state++
                                        activateState(state)
                                    }
                                    else{
                                        Toast.makeText(this@InputActivity, "옳바른 기간을 입력하세요.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                    }
                    1->{
                        val hour = binding.etHour1.text.toString()
                        val minute = binding.etMinute1.text.toString()
                        val hour2 = binding.etHour2.text.toString()
                        val minute2 = binding.etMinute2.text.toString()
                        if(hour.isEmpty()||minute.isEmpty()||hour2.isEmpty()||minute2.isEmpty()){
                            Toast.makeText(this@InputActivity, "시간을 입력하세요", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            //시간처리
                            startTime="${hour}-${minute}"
                            endTime="${hour2}-${minute2}"
                            state++
                            activateState(state)
                        }
                    }
                    2-> {
                        title = binding.etTitle.text.toString()
                        content = binding.etContent.text.toString()
                        if (id == 0) {
                            if(title.isEmpty()||content.isEmpty()){
                                Toast.makeText(this@InputActivity, "올바른 일정을 입력하세요", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                CoroutineScope(Dispatchers.IO).launch {
                                    with(myDailyViewModel) {
                                        insertSchedule(
                                            Schedule(
                                                0L,
                                                title,
                                                content,
                                                "",
                                                "${startTime}-${endTime}"
                                            )
                                        )
                                    }
                                }
                                finish()
                            }
                        } else if (id == 1) {
                            if(title.isEmpty()||content.isEmpty()){
                                Toast.makeText(this@InputActivity, "올바른 일정을 입력하세요", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                CoroutineScope(Dispatchers.IO).launch {
                                    with(myViewModel) {
                                        insertSchedule(
                                            Schedule(
                                                0L,
                                                title,
                                                content,
                                                "${sdf.format(startDay.time)}",
                                                "${startTime}-${endTime}"
                                            )
                                        )
                                    }
                                }
                                finish()
                            }
                        } else {
                            if(title.isEmpty()||content.isEmpty()){
                                Toast.makeText(this@InputActivity, "올바른 일정을 입력하세요", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                CoroutineScope(Dispatchers.IO).launch {
                                    with(myPeriodScheduleViewModel) {
                                        insertSchedule(
                                            Schedule(
                                                0L,
                                                title,
                                                content,
                                                "${sdf.format(startDay.time)}-${sdf.format(endDay.time)}",
                                                "${startTime}-${endTime}"
                                            )
                                        )
                                    }
                                }
                                finish()
                            }
                        }
                    }
                }
            }
            btnCancel.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun activateState(s: Int) {
        deactivateAllFrames()
        frames.getOrNull(s)?.visibility = View.VISIBLE
    }

    private fun deactivateAllFrames() {
        frames.forEach { it.visibility = View.INVISIBLE }
    }
    override fun onBackPressed() {
        if (state > 0) {
            state--
            activateState(state)
        } else {
            super.onBackPressed()
        }
    }
    private fun deactivateCalendar(){
        binding.datePicker.isEnabled=false
        binding.etYear.isEnabled=false
        binding.etMonth.isEnabled=false
        binding.etDay.isEnabled=false
        binding.datePicker2.isEnabled=false
        binding.etYear2.isEnabled=false
        binding.etMonth2.isEnabled=false
        binding.etDay2.isEnabled=false
        binding.textView.isEnabled=false
        binding.textView2.isEnabled=false
        binding.textView3.isEnabled=false
        binding.textView10.isEnabled=false
        binding.textView11.isEnabled=false
        binding.textView12.isEnabled=false
    }
    private fun activateCalendar(i: Int){
        if(i==2){
            binding.datePicker.isEnabled=true
            binding.etYear.isEnabled=true
            binding.etMonth.isEnabled=true
            binding.etDay.isEnabled=true
            binding.datePicker2.isEnabled=true
            binding.etYear2.isEnabled=true
            binding.etMonth2.isEnabled=true
            binding.etDay2.isEnabled=true
            binding.textView.isEnabled=true
            binding.textView2.isEnabled=true
            binding.textView3.isEnabled=true
            binding.textView10.isEnabled=true
            binding.textView11.isEnabled=true
            binding.textView12.isEnabled=true
        }
        else{
            binding.datePicker.isEnabled=true
            binding.etYear.isEnabled=true
            binding.etMonth.isEnabled=true
            binding.etDay.isEnabled=true
            binding.textView.isEnabled=true
            binding.textView2.isEnabled=true
            binding.textView3.isEnabled=true
        }
    }
}