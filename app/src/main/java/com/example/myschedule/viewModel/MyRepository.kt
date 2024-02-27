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
    suspend fun deleteSchedule(schedule: Schedule){
        myDao.deleteSchedule(schedule)
    }
    suspend fun deleteAllSchedule(){
        myDao.deleteAllSchedule()
    }
    suspend fun insertSchedule(schedule: Schedule) {
        myDao.insertSchedule(schedule)
    }
}