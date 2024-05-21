package com.example.myschedule.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Query("SELECT * FROM Schedule")
    fun getAllSchedule(): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE startDate = :date")
    fun getScheduleByDate(date: String): LiveData<List<Schedule>>

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("DELETE FROM Schedule")
    suspend fun deleteAllSchedule()

    @Query("SELECT * FROM Schedule WHERE type == :type")
    fun getSchedulesByType(type: Int): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE type == :type AND startDate = :date")
    fun getScheduleByDateAndType(date: String, type: Int): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE :date BETWEEN startDate AND endDate")
    fun getSchedulesBetweenDate(date: String) : LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE type == :type AND startDate BETWEEN :sDate AND :eDate")
    fun getWeekScheduleByDateAndType(sDate: String, eDate: String, type: Int): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == :type) AND ((startDate BETWEEN :sDate AND :eDate) OR (endDate BETWEEN :sDate AND :eDate) OR (:sDate BETWEEN startDate AND endDate) OR (:eDate BETWEEN startDate AND endDate))")
    fun getWeekSchedulesBetweenDateAndType(sDate: String, eDate: String, type: Int): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == 1 AND startDate BETWEEN :sDate AND :eDate) OR (type == 2 AND ((startDate BETWEEN :sDate AND :eDate) OR (endDate BETWEEN :sDate AND :eDate) OR (:sDate BETWEEN startDate AND endDate) OR (:eDate BETWEEN startDate AND endDate)))")
    fun getMonthSchedules(sDate: String, eDate: String): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == 0 AND (startDate == '0' OR startDate == :dayOfWeek))")
    fun getSchedulesByDayOfWeek(dayOfWeek: String): LiveData<List<Schedule>>

    @Query("SELECT name FROM Schedule WHERE (type == :type) AND ((startTime BETWEEN :searchStartTime AND :searchEndTime) OR (endTime BETWEEN :searchStartTime AND :searchEndTime) OR (:searchStartTime BETWEEN startTime AND endTime) OR (:searchEndTime BETWEEN startTime AND endTime))")
    fun conflictCheck(type: Int, searchStartTime: String, searchEndTime: String): String?

    @Query("SELECT name FROM Schedule WHERE (startDate == :dayOfWeek) AND ((startTime BETWEEN :searchStartTime AND :searchEndTime) OR (endTime BETWEEN :searchStartTime AND :searchEndTime) OR (:searchStartTime BETWEEN startTime AND endTime) OR (:searchEndTime BETWEEN startTime AND endTime))")
    fun conflictCheck2(dayOfWeek: Int, searchStartTime: String, searchEndTime: String): String?

    @Query("SELECT name FROM Schedule WHERE (type == 0) AND ((startTime BETWEEN :searchStartTime AND :searchEndTime) OR (endTime BETWEEN :searchStartTime AND :searchEndTime) OR (:searchStartTime BETWEEN startTime AND endTime) OR (:searchEndTime BETWEEN startTime AND endTime))")
    fun conflictCheck3(searchStartTime: String, searchEndTime: String): String?


}