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
    fun insertSchedule(schedule: Schedule) = viewModelScope.launch {
        repository.insertSchedule(schedule)
    }
    suspend fun deleteSchedule(schedule: Schedule){
        repository.deleteSchedule(schedule)
    }

    fun getMonthSchedules(sDate:String, eDate:String): LiveData<List<Schedule>> {
        return repository.getMonthSchedules(sDate,eDate)
    }

    fun conflictCheck(type:Int,startTime:String,endTime:String): String? {
        return repository.conflictCheck(type,startTime,endTime)
    }

    fun conflictCheck2(dayOfWeek: Int, startTime: String, endTime: String): String? {
        return repository.conflictCheck2(dayOfWeek,startTime,endTime)
    }

    fun advancedSearch(type:Int,etArray: MutableList<String>):LiveData<List<Schedule>>{
        if(etArray[0].isEmpty()&&etArray[1].isEmpty()&&etArray[2].isEmpty()){
            return repository.getSchedulesByType(type)
        }else if(etArray[1].isEmpty()&&etArray[2].isEmpty()){
            return repository.getScheduleByDateAndType(etArray[0],type)
        }else if(etArray[0].isEmpty()&&etArray[2].isEmpty()){
            return repository.getSchedulesByTypeAndTitle(type,etArray[1])
        }else if(etArray[0].isEmpty()&&etArray[1].isEmpty()){
            return repository.getSchedulesByTypeAndContents(type,etArray[2])
        }else if(etArray[2].isEmpty()){
            return repository.getSchedulesByTypeAndDateAndTitle(type,etArray[0],etArray[1])
        }else if(etArray[1].isEmpty()){
            return repository.getSchedulesByTypeAndDateAndContents(type,etArray[0],etArray[2])
        }else if(etArray[0].isEmpty()){
            return repository.getSchedulesByTypeAndTitleAndContents(type,etArray[1],etArray[2])
        }else{
            return repository.getSchedulesByTypeAndDateAndTitleAndContents(type,etArray[0],etArray[1],etArray[2])
        }
    }

    fun getDayFragmentData(date: String,dayOfWeek: Int): LiveData<List<Schedule>> {
        return repository.getDayFragmentData(date,dayOfWeek)
    }

    fun getWeekSchedule(sDate: String, eDate: String): LiveData<List<Schedule>> {
        return repository.getWeekSchedule(sDate,eDate)
    }
}
