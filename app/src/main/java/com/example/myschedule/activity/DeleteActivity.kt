package com.example.myschedule.activity

import MyPeriodScheduleViewModel
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myschedule.adapter.MyScheduleAdapter
import com.example.myschedule.databinding.DeleteLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyDailyViewModel
import com.example.myschedule.viewModel.MyViewModel
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import java.time.Period
import java.util.Calendar

class DeleteActivity : AppCompatActivity() {
    private lateinit var binding : DeleteLayoutBinding
    private lateinit var myDailyViewModel: MyDailyViewModel
    private lateinit var myViewModel: MyViewModel
    private lateinit var myPeriodScheduleViewModel: MyPeriodScheduleViewModel
    private lateinit var schedules : Array<List<Schedule>>
    private lateinit var adapter : MyScheduleAdapter
    private lateinit var livedata1 :LiveData<List<Schedule>>
    private lateinit var livedata2 :LiveData<List<Schedule>>
    private lateinit var livedata3 :LiveData<List<Schedule>>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = DeleteLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myDailyViewModel = ViewModelProvider(this)[MyDailyViewModel::class.java]
        myPeriodScheduleViewModel = ViewModelProvider(this)[MyPeriodScheduleViewModel::class.java]
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        schedules = Array(3) { emptyList() }
        livedata1 = myDailyViewModel.getAllSchedules()
        livedata1.observe(this) {
            schedules[0] = it
        }
        livedata2 = myViewModel.getAllSchedules()
        livedata2.observe(this) {
            schedules[1] = it
        }
        livedata3 = myPeriodScheduleViewModel.getAllSchedules()
        livedata3.observe(this) {
            schedules[2] = it
        }
        setupListeners()
    }

    private fun setupListeners() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyScheduleAdapter(emptyList())
        binding.recyclerView.adapter = adapter
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
            search()
        }
        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            for(i:Int in 0 until adapter.itemCount) {
                val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(i)
                if (viewHolder != null && viewHolder is MyScheduleAdapter.ScheduleViewHolder) {
                    viewHolder.binding.checkbox.isChecked = isChecked
                }
            }
        }
        binding.deleteBtn.setOnClickListener {
            delete()
        }
    }

    private fun search() {
        val pos = binding.spinner.selectedItemPosition
        val etArray  = mutableListOf<String>()
        var searchedSchedules: MutableList<Schedule> = mutableListOf()
        etArray.add( binding.etYear.text.toString())
        etArray.add( binding.etMonth.text.toString())
        etArray.add( binding.etDay.text.toString())
        etArray.add( binding.etTitle.text.toString())
        etArray.add( binding.etContent.text.toString())
        if(etArray[1]!="") etArray[1] = (etArray[1].toInt()-1).toString()
        when (pos){
            0 ->{
                for(schedule in schedules[pos]){
                    val contents = listOf(schedule.name,schedule.content)
                    var flag = true
                    for((i,text) in contents.withIndex()){
                        if(etArray[i+3]!=""){
                            if (!text.contains(etArray[i+3])) {
                                flag = false
                                break
                            }
                        }
                    }
                    if(flag) searchedSchedules.add(schedule)
                }
            }
            1 ->{
                for(schedule in schedules[pos]){
                    val dates = schedule.date.split("-")
                    val contents = listOf(schedule.name,schedule.content)
                    val data = dates + contents
                    var flag = true
                    for((i,text) in etArray.withIndex()){
                        if(i<3 && text != ""){
                            if(data[i]!=text){
                                flag=false
                                break
                            }
                        }else if(text!=""){
                            if(!data[i].contains(text)){
                                flag=false
                                break
                            }
                        }
                    }
                    if(flag) searchedSchedules.add(schedule)
                }
            }
            2 ->{
                for(schedule in schedules[pos]){
                    val dates = schedule.date.split("-")
                    val contents = listOf(schedule.name,schedule.content)
                    var flag = true
                    for((i,text) in etArray.withIndex()){
                        if(i<3 && text != ""){
                            if(text.toInt()<dates[i].toInt()||text.toInt()>dates[i+3].toInt()){
                                flag=false
                                break
                            }
                        }else if(text!=""){
                            if(!contents[i-3].contains(text)){
                                flag=false
                                break
                            }
                        }
                    }
                    if(flag) searchedSchedules.add(schedule)
                }
            }
        }
        try {
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = MyScheduleAdapter(searchedSchedules)
            binding.recyclerView.adapter = adapter
        }catch (e : NullPointerException){
            Toast.makeText(this@DeleteActivity, "adapter error", Toast.LENGTH_SHORT).show()
        }
    }
    private fun delete(){
        val selectedItemPosition = binding.spinner.selectedItemPosition
        val confirmationDialog = AlertDialog.Builder(this)
            .setTitle("일정 삭제")
            .setMessage("선택한 일정을 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                for (schedule in adapter.getCheckedItems()) {
                    when (selectedItemPosition) {
                        0 -> {
                            myDailyViewModel.viewModelScope.launch {
                                myDailyViewModel.deleteSchedule(schedule)
                            }
                        }
                        1 -> {
                            myViewModel.viewModelScope.launch {
                                myViewModel.deleteSchedule(schedule)
                            }
                        }
                        2 -> {
                            myPeriodScheduleViewModel.viewModelScope.launch {
                                myPeriodScheduleViewModel.deleteSchedule(schedule)
                            }
                        }
                    }
                }
                binding.recyclerView.adapter = MyScheduleAdapter(emptyList())
                Handler(Looper.getMainLooper()).postDelayed({
                    search()
                }, 100)
                search()
            }
            .setNegativeButton("취소", null)
            .create()
        confirmationDialog.show()
    }

}
