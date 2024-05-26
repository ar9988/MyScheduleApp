package com.example.myschedule.viewModel

import androidx.lifecycle.LiveData
import com.example.myschedule.db.MyDAO
import com.example.myschedule.db.Schedule

class MyRepository(private val myDao: MyDAO) {
    fun getSchedulesByType(type: Int): LiveData<List<Schedule>>{
        return myDao.getSchedulesByType(type)
    }
    fun getScheduleByDateAndType(date: String, type: Int): LiveData<List<Schedule>>{
        return when(type){
            0 -> myDao.getSchedulesByType(0)
            1 -> myDao.getScheduleByDate(date)
            2 -> myDao.getSchedulesBetweenDate(date)
            else -> myDao.getScheduleByDate(date)
        }
    }
    suspend fun deleteSchedule(schedule: Schedule){
        myDao.deleteSchedule(schedule)
    }
    suspend fun insertSchedule(schedule: Schedule) {
        myDao.insertSchedule(schedule)
    }

    fun getMonthSchedules(sDate: String, eDate: String): LiveData<List<Schedule>> {
        return myDao.getMonthSchedules(sDate, eDate)
    }

    fun conflictCheck(type:Int,startTime:String,endTime:String): String? {
        return myDao.conflictCheck(type,startTime,endTime)
    }

    fun conflictCheck2(dayOfWeek: Int, startTime: String, endTime: String): String? {
        if(dayOfWeek==0){
            return myDao.conflictCheck(0,startTime,endTime)
        }
        return myDao.conflictCheck2(dayOfWeek,startTime,endTime)
    }

    fun getSchedulesByTypeAndTitle(type: Int, title: String): LiveData<List<Schedule>> {
        return myDao.getSchedulesByTypeAndTitle(type,title)
    }

    fun getSchedulesByTypeAndContents(type: Int, content: String): LiveData<List<Schedule>> {
        return myDao.getSchedulesByTypeAndContents(type,content)
    }

    fun getSchedulesByTypeAndDateAndTitle(type: Int, date: String, title: String): LiveData<List<Schedule>> {
        return myDao.getSchedulesByTypeAndDateAndTitle(type,date,title)
    }

    fun getSchedulesByTypeAndDateAndContents(type: Int, date: String, contents: String): LiveData<List<Schedule>> {
        return myDao.getSchedulesByTypeAndDateAndContents(type,date,contents)
    }

    fun getSchedulesByTypeAndTitleAndContents(type: Int, title: String, contents: String): LiveData<List<Schedule>> {
        return myDao.getSchedulesByTypeAndTitleAndContents(type,title,contents)
    }

    fun getSchedulesByTypeAndDateAndTitleAndContents(type: Int, date: String, title: String, contents: String): LiveData<List<Schedule>> {
        return myDao.getSchedulesByTypeAndDateAndTitleAndContents(type,date,title,contents)
    }

    fun getDayFragmentData(date: String,dayOfWeek: Int): LiveData<List<Schedule>> {
        return myDao.getDayFragmentData(date,dayOfWeek)
    }

    fun getWeekSchedule(sDate: String, eDate: String): LiveData<List<Schedule>> {
        return myDao.getWeekSchedule(sDate,eDate)
    }


}