package com.example.myschedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.myschedule.databinding.InputLayoutBinding
import java.util.Calendar

class InputActivity: AppCompatActivity()  {
    private val binding by lazy{InputLayoutBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.datePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this,16973935, { _, year, month, day ->
                run {
                    binding.etYear.setText("$year")
                    binding.etMonth.setText("${month+1}")
                    binding.etDay.setText("$day")
                }
            }, year, month, day).show()
        }
        binding.timePicker1.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener{_,hourOfDay,minute ->
                binding.etHour1.setText("$hourOfDay")
                binding.etMinute1.setText("$minute")
            }
            TimePickerDialog(this,16973935,timeSetListener,0,0,true).show()
        }
        binding.timePicker2.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener{_,hourOfDay,minute ->
                binding.etHour2.setText("$hourOfDay")
                binding.etMinute2.setText("$minute")
            }
            TimePickerDialog(this,16973935,timeSetListener,0,0,true).show()
        }
        binding.checkBox.setOnCheckedChangeListener{_,isChecked ->
            if(isChecked){
                binding.datePicker.isEnabled=false
                binding.etYear.isEnabled=false
                binding.etMonth.isEnabled=false
                binding.etDay.isEnabled=false
            }
            else{
                binding.datePicker.isEnabled=true
                binding.etYear.isEnabled=true
                binding.etMonth.isEnabled=true
                binding.etDay.isEnabled=true
            }
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}