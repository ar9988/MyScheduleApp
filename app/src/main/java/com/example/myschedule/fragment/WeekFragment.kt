package com.example.myschedule.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myschedule.databinding.WeekLayoutBinding
import com.example.myschedule.viewModel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeekFragment : Fragment(){
    private lateinit var binding : WeekLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= WeekLayoutBinding.inflate(inflater)
        return binding.root
    }
}