package com.example.myschedule.activity

import android.app.AlertDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myschedule.R
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
    private var schedules: LiveData<List<Schedule>> = MutableLiveData()
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
        val yearFlag = binding.etYear.text.toString().isEmpty()
        val monthFlag = binding.etMonth.text.toString().isEmpty()
        val dayFlag = binding.etDay.text.toString().isEmpty()
        if ((yearFlag || monthFlag || dayFlag) && !(yearFlag && monthFlag && dayFlag)) {
            Toast.makeText(this@DeleteActivity, "년, 월, 일을 모두 입력하거나 모두 비워야 합니다.", Toast.LENGTH_SHORT).show()
        } else {
            var date = ""
            if (!yearFlag) {
                calendar.set(
                    binding.etYear.text.toString().toInt(),
                    binding.etMonth.text.toString().toInt() - 1,
                    binding.etDay.text.toString().toInt()
                )
                date = sdf.format(calendar.time)
            }
            etArray.add(date)
            etArray.add(binding.etTitle.text.toString())
            etArray.add(binding.etContent.text.toString())
            schedules = myViewModel.advancedSearch(pos, etArray)
            schedules.observe(this){
                setData()
            }
        }
    }
    private fun setData() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        schedules.value?.let { adapter.updateData(it) }
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
                binding.etMonth.setText(getString(
                    R.string.month_placeholder,
                    calendar.get(Calendar.MONTH) + 1
                ))
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

}
