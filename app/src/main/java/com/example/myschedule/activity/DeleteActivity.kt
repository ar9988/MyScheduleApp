package com.example.myschedule.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myschedule.recyclerview.MyScheduleAdapterDelete
import com.example.myschedule.databinding.DeleteLayoutBinding
import com.example.myschedule.db.Schedule
import com.example.myschedule.viewModel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class DeleteActivity : AppCompatActivity() {
    private lateinit var binding : DeleteLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    private lateinit var adapter : MyScheduleAdapterDelete
    private var schedules: LiveData<List<Schedule>>? = null
    private var scheduleObserver: Observer<List<Schedule>>? = null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = DeleteLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }
    private fun search() {
        val pos = binding.spinner.selectedItemPosition
        val etArray = mutableListOf<String>()
        val date = binding.etYear.text.toString()+"-"+binding.etMonth.text.toString()+"-"+binding.etDay.text.toString()
        etArray.add(date)
        etArray.add(binding.etTitle.text.toString())
        etArray.add(binding.etContent.text.toString())
        schedules = myViewModel.advancedSearch(pos,etArray)
        scheduleObserver?.let { schedules?.removeObserver(it) }
        scheduleObserver = Observer {
            setData()
        }
        schedules?.observe(this, scheduleObserver!!)

    }
    private fun setData() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.updateData(schedules?.value!!)
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
            }
            .setNegativeButton("취소", null)
            .create()
        confirmationDialog.show()
    }
    override fun onDestroy() {
        super.onDestroy()
        scheduleObserver?.let { schedules?.removeObserver(it) }
    }

}
