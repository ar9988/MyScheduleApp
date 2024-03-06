package com.example.myschedule.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.myschedule.db.MyDAO
import com.example.myschedule.db.MyDatabase
import com.example.myschedule.db.Schedule
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MyRepository
    private val allSchedules: LiveData<List<Schedule>>
    init {
        val myDao = MyDatabase.getDatabase(application).getMyDao()
        repository = MyRepository(myDao)
        allSchedules = repository.getAllSchedules()
    }
    fun getAllSchedules():LiveData<List<Schedule>>{
        return allSchedules
    }
    fun getScheduleByDate(date: String): LiveData<List<Schedule>> {
        return repository.getScheduleByDate(date)
    }
    fun insertSchedule(schedule: Schedule) = viewModelScope.launch {
        repository.insertSchedule(schedule)
    }
    suspend fun deleteAllSchedule(){
        repository.deleteAllSchedule()
    }
    suspend fun deleteSchedule(schedule: Schedule){
        repository.deleteSchedule(schedule)
    }
}
