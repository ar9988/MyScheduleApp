package com.example.myschedule.activity

import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myschedule.R
import com.example.myschedule.application.calculateTime
import com.example.myschedule.databinding.InputLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

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
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var frames:Array<ConstraintLayout>
    private val callback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            if (state > 0) {
                state--
                activateState(state)
            } else {
                finish()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = InputLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.onBackPressedDispatcher.addCallback(this, callback)
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
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("날짜 선택")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
                datePicker.addOnPositiveButtonClickListener { dateInMillis ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = dateInMillis
                    binding.etYear.setText(calendar.get(Calendar.YEAR).toString())
                    binding.etMonth.setText(getString(
                        R.string.int_placeholder,
                        calendar.get(Calendar.MONTH) + 1
                    ))
                    binding.etDay.setText(calendar.get(Calendar.DAY_OF_MONTH).toString())
                }
                datePicker.show(supportFragmentManager, "DATE_PICKER")
            }
            datePicker2.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("날짜 선택")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                datePicker.addOnPositiveButtonClickListener { dateInMillis ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = dateInMillis
                    binding.etYear2.setText(calendar.get(Calendar.YEAR).toString())
                    binding.etMonth2.setText(getString(
                        R.string.int_placeholder,
                        calendar.get(Calendar.MONTH) + 1
                    ))
                    binding.etDay2.setText(calendar.get(Calendar.DAY_OF_MONTH).toString())
                }
                datePicker.show(supportFragmentManager, "DATE_PICKER")
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
                            if (type!=0&&calculateTime(hour,minute)>calculateTime(hour2,minute2)) {
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
                callback.handleOnBackPressed()
            }
        }
    }

    private suspend fun conflictCheck(type:Int,startTime:String,endTime:String): Pair<Boolean, String?> {
        var item:String?
        var flag: Boolean
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
    private fun deactivateCalendar(){
        with(binding){
            spinner.visibility = View.INVISIBLE
            datePicker.visibility = View.INVISIBLE
            etYear.visibility = View.INVISIBLE
            etMonth.visibility = View.INVISIBLE
            etDay.visibility = View.INVISIBLE
            datePicker2.visibility = View.INVISIBLE
            etYear2.visibility = View.INVISIBLE
            etMonth2.visibility = View.INVISIBLE
            etDay2.visibility = View.INVISIBLE
            textView.visibility = View.INVISIBLE
            textView2.visibility = View.INVISIBLE
            textView3.visibility = View.INVISIBLE
            textView10.visibility = View.INVISIBLE
            textView11.visibility = View.INVISIBLE
            textView12.visibility = View.INVISIBLE
        }
    }
    private fun activateCalendar(i: Int){
        with(binding){
            when (i) {
                0 -> {
                    spinner.visibility = View.VISIBLE
                }
                2 -> {
                    datePicker.visibility = View.VISIBLE
                    etYear.visibility = View.VISIBLE
                    etMonth.visibility = View.VISIBLE
                    etDay.visibility = View.VISIBLE
                    datePicker2.visibility = View.VISIBLE
                    etYear2.visibility = View.VISIBLE
                    etMonth2.visibility = View.VISIBLE
                    etDay2.visibility = View.VISIBLE
                    textView.visibility = View.VISIBLE
                    textView2.visibility = View.VISIBLE
                    textView3.visibility = View.VISIBLE
                    textView10.visibility = View.VISIBLE
                    textView11.visibility = View.VISIBLE
                    textView12.visibility = View.VISIBLE
                }
                else -> {
                    datePicker.visibility = View.VISIBLE
                    etYear.visibility = View.VISIBLE
                    etMonth.visibility = View.VISIBLE
                    etDay.visibility = View.VISIBLE
                    textView.visibility = View.VISIBLE
                    textView2.visibility = View.VISIBLE
                    textView3.visibility = View.VISIBLE
                }
            }
        }
    }
}