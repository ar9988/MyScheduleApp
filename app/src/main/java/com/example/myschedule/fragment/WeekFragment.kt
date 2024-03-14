package com.example.myschedule.fragment

import MyPeriodScheduleViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myschedule.databinding.WeekLayoutBinding
import com.example.myschedule.viewModel.MyDailyViewModel
import com.example.myschedule.viewModel.MyViewModel

class WeekFragment : Fragment(){
    private lateinit var binding : WeekLayoutBinding
    private lateinit var myDailyViewModel: MyDailyViewModel
    private lateinit var myViewModel: MyViewModel
    private lateinit var myPeriodScheduleViewModel: MyPeriodScheduleViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= WeekLayoutBinding.inflate(inflater)
        return binding.root
    }
}