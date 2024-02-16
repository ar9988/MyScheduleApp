package com.example.myschedule.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myschedule.databinding.MonthLayoutBinding

class MonthFragment :Fragment(){
    private lateinit var binding : MonthLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= MonthLayoutBinding.inflate(inflater)
        return binding.root
    }
}