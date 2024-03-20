package com.example.myschedule.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myschedule.databinding.ActivityMainBinding
import com.example.myschedule.fragment.DayFragment
import com.example.myschedule.fragment.MonthFragment
import com.example.myschedule.fragment.WeekFragment
import com.example.myschedule.viewModel.MyViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var fragmentDay: DayFragment
    private lateinit var fragmentWeek: WeekFragment
    private lateinit var fragmentMonth: MonthFragment
    private lateinit var tabLayout: TabLayout
    private lateinit var inputBtn: Button
    private lateinit var deleteBtn: Button
    private val myViewModel: MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        tabLayout = binding.tabLayout
        fragmentDay = DayFragment()
        fragmentMonth = MonthFragment()
        fragmentWeek = WeekFragment()
        supportFragmentManager.beginTransaction().replace(binding.tabContent.id, fragmentDay).commit()
        inputBtn = binding.editBtn
        deleteBtn = binding.editBtn2
        inputBtn.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }
        deleteBtn.setOnClickListener {
            val intent = Intent(this, DeleteActivity::class.java)
            startActivity(intent)
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.position) {
                    0 -> {
                        supportFragmentManager.beginTransaction().replace(binding.tabContent.id, fragmentDay).commit()
                    }
                    1 -> {
                        supportFragmentManager.beginTransaction().replace(binding.tabContent.id, fragmentWeek).commit()
                    }
                    2 -> {
                        supportFragmentManager.beginTransaction().replace(binding.tabContent.id, fragmentMonth).commit()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

}