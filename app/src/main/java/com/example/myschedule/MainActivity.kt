package com.example.myschedule

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myschedule.databinding.ActivityMainBinding
import com.example.myschedule.db.MyDAO
import com.example.myschedule.db.MyDatabase
import com.example.myschedule.fragment.DayFragment
import com.example.myschedule.fragment.MonthFragment
import com.example.myschedule.fragment.WeekFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var myDao: MyDAO
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var fragmentDay: DayFragment
    private lateinit var fragmentWeek: WeekFragment
    private lateinit var fragmentMonth: MonthFragment
    private lateinit var tabLayout: TabLayout
    private lateinit var inputBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        myDao = MyDatabase.getDatabase(this).getMyDao()
        tabLayout = binding.tabLayout
        fragmentDay = DayFragment()
        fragmentMonth = MonthFragment()
        fragmentWeek = WeekFragment()
        supportFragmentManager.beginTransaction().replace(binding.tabContent.id, fragmentDay).commit()
        inputBtn = binding.editBtn
        inputBtn.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
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