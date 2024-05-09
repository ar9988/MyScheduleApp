package com.example.myschedule.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myschedule.recyclerview.MyScheduleAdapterDelete
import com.example.myschedule.databinding.DeleteLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import java.util.Calendar

@AndroidEntryPoint
class DeleteActivity : AppCompatActivity() {
    private lateinit var binding : DeleteLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    private lateinit var schedules : Array<List<Schedule>>
    private lateinit var adapter : MyScheduleAdapterDelete
    private lateinit var livedata1 :LiveData<List<Schedule>>
    private lateinit var livedata2 :LiveData<List<Schedule>>
    private lateinit var livedata3 :LiveData<List<Schedule>>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = DeleteLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        schedules = Array(3) { emptyList() }
        livedata1 = myViewModel.getSchedulesByType(0)
        livedata1.observe(this) {
            schedules[0] = it
        }
        livedata2 = myViewModel.getSchedulesByType(1)
        livedata2.observe(this) {
            schedules[1] = it
        }
        livedata3 = myViewModel.getSchedulesByType(2)
        livedata3.observe(this) {
            schedules[2] = it
        }
        setupListeners()
    }

    private fun setupListeners() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyScheduleAdapterDelete(emptyList())
        binding.recyclerView.adapter = adapter
        binding.DatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this@DeleteActivity, 16973935, { _, selectedYear, selectedMonth, selectedDay ->
                run {
                    binding.etYear.setText("$selectedYear")
                    binding.etMonth.setText("${selectedMonth + 1}")
                    binding.etDay.setText("$selectedDay")
                }
            }, currentYear, currentMonth, currentDay).show()
        }
        binding.SearchBtn.setOnClickListener{
            search()
        }
        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            for(i:Int in 0 until adapter.itemCount) {
                val viewHolder = binding.recyclerView.findViewHolderForAdapterPosition(i)
                if (viewHolder != null && viewHolder is MyScheduleAdapterDelete.ScheduleViewHolder) {
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
        val searchedSchedules: MutableList<Schedule> = mutableListOf()
        etArray.add( binding.etYear.text.toString())
        etArray.add( binding.etMonth.text.toString())
        etArray.add( binding.etDay.text.toString())
        etArray.add( binding.etTitle.text.toString())
        etArray.add( binding.etContent.text.toString())
        when (pos){
            0 ->{
                for(schedule in schedules[pos]){
                    val contents = listOf(schedule.name,schedule.content)
                    var flag = true
                    for((i,text) in contents.withIndex()){
                        if(etArray[i+3].isNotEmpty()){
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
                    val dates = schedule.startDate.split("-").map { it.toIntOrNull()?.toString() ?: it }+schedule.endDate.split("-").map { it.toIntOrNull()?.toString() ?: it }
                    val contents = listOf(schedule.name,schedule.content)
                    val data = dates + contents
                    var flag = true
                    for((i,text) in etArray.withIndex()){
                        if(i<3 && text.isNotEmpty()){
                            if(data[i]!=text){
                                flag=false
                                break
                            }
                        }else if(text.isNotEmpty()){
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
                    var flag = true
                    if(etArray[0].isEmpty()&&etArray[1].isEmpty()&&etArray[2].isEmpty()){
                        searchedSchedules.add(schedule)
                    }
                    else if(etArray[0].isEmpty()||etArray[1].isEmpty()||etArray[2].isEmpty()){
                        Toast.makeText(this@DeleteActivity, "년, 월, 일을 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val calendar1 = Calendar.getInstance()
                        val calendar2 = Calendar.getInstance()
                        val inputDate = Calendar.getInstance()
                        val dates = schedule.startDate.split("-").map { it.toIntOrNull()?.toString() ?: it }+schedule.endDate.split("-").map { it.toIntOrNull()?.toString() ?: it }
                        val contents = listOf(schedule.name,schedule.content)
                        calendar1.set(
                            dates[0].toInt(),
                            dates[1].toInt(),
                            dates[2].toInt()
                        )
                        calendar2.set(
                            dates[3].toInt(),
                            dates[4].toInt(),
                            dates[5].toInt()
                        )
                        inputDate.set(
                            etArray[0].toInt(),
                            etArray[1].toInt(),
                            etArray[2].toInt(),
                        )
                        val isInputDateBetweenCalendars = (inputDate.timeInMillis >= calendar1.timeInMillis &&
                                inputDate.timeInMillis <= calendar2.timeInMillis)
                        if(isInputDateBetweenCalendars){
                            for((i,text) in contents.withIndex()){
                                if(etArray[i+3].isNotEmpty()){
                                    if (!text.contains(etArray[i+3])) {
                                        flag = false
                                        break
                                    }
                                }
                            }
                            if(flag) searchedSchedules.add(schedule)
                        }
                    }
                }
            }
        }
        try {
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = MyScheduleAdapterDelete(searchedSchedules)
            binding.recyclerView.adapter = adapter
        }catch (e : NullPointerException){
            Toast.makeText(this@DeleteActivity, "adapter error", Toast.LENGTH_SHORT).show()
        }
    }
    private fun delete(){
        val confirmationDialog = AlertDialog.Builder(this)
            .setTitle("일정 삭제")
            .setMessage("선택한 일정을 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                for (schedule in adapter.getCheckedItems()) {
                    myViewModel.viewModelScope.launch {
                        myViewModel.deleteSchedule(schedule)
                    }
                }
                binding.recyclerView.adapter = MyScheduleAdapterDelete(emptyList())
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
