package com.example.myschedule.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
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
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class DeleteActivity : AppCompatActivity() {
    private lateinit var binding : DeleteLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
        val calendar = Calendar.getInstance()
        calendar.set(
            binding.etYear.text.toString().toInt(),
            binding.etMonth.text.toString().toInt()-1,
            binding.etDay.text.toString().toInt(),
        )
        val date = sdf.format(calendar.time) // binding.etYear.text.toString()+"-"+binding.etMonth.text.toString()+"-"+binding.etDay.text.toString()
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
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("날짜 선택")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.addOnPositiveButtonClickListener { dateInMillis ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateInMillis
                binding.etYear.setText(calendar.get(Calendar.YEAR).toString())
                binding.etMonth.setText((calendar.get(Calendar.MONTH) + 1).toString())
                binding.etDay.setText(calendar.get(Calendar.DAY_OF_MONTH).toString())
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER")
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
