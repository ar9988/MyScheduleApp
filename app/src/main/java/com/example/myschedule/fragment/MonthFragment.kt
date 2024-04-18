package com.example.myschedule.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myschedule.databinding.MonthLayoutBinding
import com.example.myschedule.viewModel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonthFragment :Fragment(){
    private lateinit var binding : MonthLayoutBinding
    private val myViewModel: MyViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= MonthLayoutBinding.inflate(inflater)
        return binding.root
    }

    fun refresh(){
        Log.d("Month","month called")
    }
}