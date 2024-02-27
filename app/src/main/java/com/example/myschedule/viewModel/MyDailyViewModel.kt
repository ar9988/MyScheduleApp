package com.example.myschedule.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.myschedule.db.MyDailyDatabase
import com.example.myschedule.db.Schedule
import kotlinx.coroutines.launch

class MyDailyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MyRepository
    private val allSchedules: LiveData<List<Schedule>>
    init {
        val myDao = MyDailyDatabase.getDatabase(application).getMyDao()
        repository = MyRepository(myDao)
        allSchedules = repository.getAllSchedules()
    }
    fun getAllSchedules(): LiveData<List<Schedule>> {
        return allSchedules
    }
    suspend fun deleteSchedule(schedule: Schedule){
        repository.deleteSchedule(schedule)
    }
    suspend fun deleteAllSchedule(){
        repository.deleteAllSchedule()
    }
    fun insertSchedule(schedule: Schedule) = viewModelScope.launch {
        repository.insertSchedule(schedule)
    }
}

