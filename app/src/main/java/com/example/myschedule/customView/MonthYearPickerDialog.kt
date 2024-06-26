package com.example.myschedule.customView

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.example.myschedule.R
import com.example.myschedule.application.extractMonth
import com.example.myschedule.application.extractYear
import com.example.myschedule.databinding.MonthLayoutBinding

class MonthYearPickerDialog(
    context: Context,
    binding: MonthLayoutBinding,
    private val listener: (Int, Int) -> Unit
) : AlertDialog(context) {

    private var years: List<Int>
    private val months = listOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")

    init {
        val rootView = FrameLayout(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_month_year_picker, rootView)
        setView(view)

        val monthSpinner = view.findViewById<Spinner>(R.id.monthSpinner)
        val monthAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter
        val items = binding.calendarTxt.text.split(" ")
        val currentYear = items[0].extractYear()
        val currentMonth = items[1].extractMonth()
        val startYear = currentYear - 10
        val endYear = currentYear + 10
        years = (startYear..endYear).toList()
        val yearSpinner = view.findViewById<Spinner>(R.id.yearSpinner)
        val yearAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        yearSpinner.setSelection(years.indexOf(currentYear))
        monthSpinner.setSelection(currentMonth)

        val confirmButton = view.findViewById<Button>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            val selectedYear = years[yearSpinner.selectedItemPosition]
            val selectedMonth = monthSpinner.selectedItemPosition
            listener.invoke(selectedYear, selectedMonth)
            dismiss()
        }

        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}

