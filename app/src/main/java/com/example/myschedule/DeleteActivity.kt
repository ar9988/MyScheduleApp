package com.example.myschedule

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myschedule.databinding.DeleteLayoutBinding
import com.example.myschedule.viewModel.MyDailyViewModel
import com.example.myschedule.viewModel.MyViewModel

class DeleteActivity : AppCompatActivity() {
    private lateinit var binding : DeleteLayoutBinding
    private val myDailyViewModel: MyDailyViewModel by viewModels()
    private val myViewModel: MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = DeleteLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}