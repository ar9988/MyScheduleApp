package com.example.myschedule.viewModel

import androidx.lifecycle.LiveData
import com.example.myschedule.db.MyDAO
import com.example.myschedule.db.Schedule

class MyRepository(private val myDao: MyDAO) {
    fun getAllSchedules(): LiveData<List<Schedule>> {
        return myDao.getAllSchedule()
    }
    fun getScheduleByDate(date:String): LiveData<List<Schedule>> {
        return myDao.getScheduleByDate(date)
    }
    fun getSchedulesByType(type: Int): LiveData<List<Schedule>>{
        return myDao.getSchedulesByType(type)
    }
    fun getScheduleByDateAndType(date: String, type: Int): LiveData<List<Schedule>>{
        when(type){
            0 -> return myDao.getSchedulesByType(0)
            1 -> return myDao.getScheduleByDate(date)
            2 -> return myDao.getSchedulesBetweenDate(date)
            else -> return myDao.getAllSchedule()
        }
    }
    suspend fun deleteSchedule(schedule: Schedule){
        myDao.deleteSchedule(schedule)
    }
    suspend fun deleteAllSchedule(){
        myDao.deleteAllSchedule()
    }
    suspend fun insertSchedule(schedule: Schedule) {
        myDao.insertSchedule(schedule)
    }

    fun getWeekScheduleByDateAndType(startDate: String,endDate:String, type: Int): LiveData<List<Schedule>> {
        when(type){
            0 -> return myDao.getSchedulesByType(0)
            1 -> return myDao.getWeekScheduleByDateAndType(startDate,endDate,type)
            2 -> return myDao.getWeekSchedulesBetweenDateAndType(startDate,endDate,type)
            else -> return myDao.getAllSchedule()
        }
    }
}