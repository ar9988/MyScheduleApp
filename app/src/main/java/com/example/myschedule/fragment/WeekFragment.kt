package com.example.myschedule.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myschedule.databinding.WeekLayoutBinding

class WeekFragment : Fragment(){
    private lateinit var binding : WeekLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= WeekLayoutBinding.inflate(inflater)
        return binding.root
    }
}