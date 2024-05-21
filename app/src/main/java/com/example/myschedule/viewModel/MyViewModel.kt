package com.example.myschedule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myschedule.db.Schedule
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    private val allSchedules: LiveData<List<Schedule>> = repository.getAllSchedules()
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

    fun getSchedulesByType(type: Int): LiveData<List<Schedule>>{
        return repository.getSchedulesByType(type)
    }
    fun getScheduleByDateAndType(date: String, type: Int): LiveData<List<Schedule>>{
        return repository.getScheduleByDateAndType(date,type)
    }

    fun getWeekSchedulesByDateAndType(startDate: String,endDate:String, type: Int): LiveData<List<Schedule>> {
        return repository.getWeekScheduleByDateAndType(startDate,endDate,type)
    }

    fun getMonthSchedules(sDate:String, eDate:String): LiveData<List<Schedule>> {
        return repository.getMonthSchedules(sDate,eDate)
    }

    fun conflictCheck(type:Int,startTime:String,endTime:String): String? {
        return repository.conflictCheck(type,startTime,endTime)
    }

    fun getSchedulesByDayOfWeek(dayOfWeek: Int): LiveData<List<Schedule>> {
        return repository.getSchedulesByDayOfWeek(dayOfWeek);
    }

    fun conflictCheck2(dayOfWeek: Int, startTime: String, endTime: String): String? {
        return repository.conflictCheck2(dayOfWeek,startTime,endTime)
    }
}
