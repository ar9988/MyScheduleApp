package com.example.myschedule.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.myschedule.R
import com.example.myschedule.databinding.InputLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.util.Calendar

@AndroidEntryPoint
class InputActivity: AppCompatActivity()  {
    private lateinit var binding : InputLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    private var startDay: Calendar = Calendar.getInstance()
    private var endDay: Calendar = Calendar.getInstance()
    private lateinit var title:String
    private lateinit var content:String
    private lateinit var startTime:String
    private lateinit var endTime:String
    private var state = 0
    private var dayOfWeek = 0
    private var type=0
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private lateinit var frames:Array<ConstraintLayout>
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = InputLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        frames = arrayOf(binding.frame0, binding.frame1, binding.frame2)
        activateState(0)
        setupListeners()
        deactivateCalendar()
        activateCalendar(0)
        ArrayAdapter.createFromResource(
            this,
            R.array.week_list,
            android.R.layout.simple_spinner_dropdown_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = arrayAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            datePicker.setOnClickListener {
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
            }
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
                type=0
                deactivateCalendar()
                activateCalendar(type)
            }
            rgBtn2.setOnClickListener {
                type=1
                deactivateCalendar()
                activateCalendar(type)
            }
            rgBtn3.setOnClickListener {
                type=2
                deactivateCalendar()
                activateCalendar(type)
            }
            btnInput.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    when (state) {
                        0 -> {
                            val yearText = binding.etYear.text.toString()
                            val monthText = binding.etMonth.text.toString()
                            val dayText = binding.etDay.text.toString()
                            val yearText2 = binding.etYear2.text.toString()
                            val monthText2 = binding.etMonth2.text.toString()
                            val dayText2 = binding.etDay2.text.toString()
                            when (type) {
                                0 -> {
                                    state++
                                    activateState(state)
                                    dayOfWeek = spinner.selectedItemPosition
                                }
                                1 -> {
                                    if (yearText.isEmpty() || monthText.isEmpty() || dayText.isEmpty()) {
                                        Toast.makeText(this@InputActivity, "년, 월, 일을 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        startDay.set(
                                            yearText.toInt(),
                                            monthText.toInt() - 1,
                                            dayText.toInt()
                                        )
                                        endDay.set(
                                            0,
                                            0,
                                            0
                                        )
                                        state++
                                        activateState(state)
                                    }
                                }
                                2 -> {
                                    if (yearText.isEmpty() || monthText.isEmpty() || dayText.isEmpty() || yearText2.isEmpty() || monthText2.isEmpty() || dayText2.isEmpty()) {
                                        Toast.makeText(this@InputActivity, "년, 월, 일을 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        startDay.set(
                                            yearText.toInt(),
                                            monthText.toInt() - 1,
                                            dayText.toInt()
                                        )
                                        endDay.set(
                                            yearText2.toInt(),
                                            monthText2.toInt() - 1,
                                            dayText2.toInt()
                                        )
                                        if (state == 2 && endDay.before(startDay)) {
                                            Toast.makeText(this@InputActivity, "옳바른 기간을 입력하세요.", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }
                                        state++
                                        activateState(state)
                                    }
                                }
                            }
                        }
                        1 -> {
                            val hour = binding.etHour1.text.toString()
                            val minute = binding.etMinute1.text.toString()
                            val hour2 = binding.etHour2.text.toString()
                            val minute2 = binding.etMinute2.text.toString()
                            if (hour.isEmpty() || minute.isEmpty() || hour2.isEmpty() || minute2.isEmpty()) {
                                Toast.makeText(this@InputActivity, "시간을 입력하세요", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            startTime = "$hour-$minute"
                            endTime = "$hour2-$minute2"
                            if (startTime>endTime) {
                                Toast.makeText(this@InputActivity, "옳바른 시간을 입력하세요", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            state++
                            activateState(state)
                        }
                        2 -> {
                            title = binding.etTitle.text.toString()
                            content = binding.etContent.text.toString()
                            if (title.isEmpty() || content.isEmpty()) {
                                Toast.makeText(this@InputActivity, "올바른 일정을 입력하세요", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            val ans = conflictCheck(type, startTime, endTime)
                            if (ans.first) {
                                Toast.makeText(this@InputActivity, "해당 시간대에 ${ans.second} 일정이 있습니다", Toast.LENGTH_SHORT).show()
                            } else {
                                if(type == 0){
                                    Log.d("input",dayOfWeek.toString());
                                    val tmp = Schedule(
                                        0L,
                                        0,
                                        title,
                                        content,
                                        dayOfWeek.toString(),
                                        "",
                                        startTime,
                                        endTime
                                    )
                                    withContext(Dispatchers.IO) {
                                        myViewModel.insertSchedule(tmp)
                                    }
                                    finish()
                                }
                                else{
                                    val tmp = Schedule(
                                        0L,
                                        type,
                                        title,
                                        content,
                                        sdf.format(startDay.time),
                                        sdf.format(endDay.time),
                                        startTime,
                                        endTime
                                    )
                                    withContext(Dispatchers.IO) {
                                        myViewModel.insertSchedule(tmp)
                                    }
                                    finish()
                                }
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

    private suspend fun conflictCheck(type:Int,startTime:String,endTime:String): Pair<Boolean, String?> {
        var item:String? = null
        var flag:Boolean = false
        val deferred = CoroutineScope(Dispatchers.IO).async {
            if(type==0){
                item = myViewModel.conflictCheck2(dayOfWeek, startTime, endTime)
                flag = item != null
                Pair(flag, item)
            }
            else{
                item = myViewModel.conflictCheck(type, startTime, endTime)
                flag = item != null
                Pair(flag, item)
            }
        }

        val (resultFlag, resultItem) = deferred.await()


        return Pair(resultFlag, resultItem)
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
        binding.spinner.visibility = View.INVISIBLE
        binding.datePicker.visibility = View.INVISIBLE
        binding.etYear.visibility = View.INVISIBLE
        binding.etMonth.visibility = View.INVISIBLE
        binding.etDay.visibility = View.INVISIBLE
        binding.datePicker2.visibility = View.INVISIBLE
        binding.etYear2.visibility = View.INVISIBLE
        binding.etMonth2.visibility = View.INVISIBLE
        binding.etDay2.visibility = View.INVISIBLE
        binding.textView.visibility = View.INVISIBLE
        binding.textView2.visibility = View.INVISIBLE
        binding.textView3.visibility = View.INVISIBLE
        binding.textView10.visibility = View.INVISIBLE
        binding.textView11.visibility = View.INVISIBLE
        binding.textView12.visibility = View.INVISIBLE
    }
    private fun activateCalendar(i: Int){
        if(i==0){
            binding.spinner.visibility = View.VISIBLE
        }
        else if(i==2){
            binding.datePicker.visibility = View.VISIBLE
            binding.etYear.visibility = View.VISIBLE
            binding.etMonth.visibility = View.VISIBLE
            binding.etDay.visibility = View.VISIBLE
            binding.datePicker2.visibility = View.VISIBLE
            binding.etYear2.visibility = View.VISIBLE
            binding.etMonth2.visibility = View.VISIBLE
            binding.etDay2.visibility = View.VISIBLE
            binding.textView.visibility = View.VISIBLE
            binding.textView2.visibility = View.VISIBLE
            binding.textView3.visibility = View.VISIBLE
            binding.textView10.visibility = View.VISIBLE
            binding.textView11.visibility = View.VISIBLE
            binding.textView12.visibility = View.VISIBLE
        }
        else{
            binding.datePicker.visibility = View.VISIBLE
            binding.etYear.visibility = View.VISIBLE
            binding.etMonth.visibility = View.VISIBLE
            binding.etDay.visibility = View.VISIBLE
            binding.textView.visibility = View.VISIBLE
            binding.textView2.visibility = View.VISIBLE
            binding.textView3.visibility = View.VISIBLE
        }
    }
}