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

    @Query("SELECT * FROM Schedule WHERE type == :type AND (endDate != '0000-00-00' AND startDate BETWEEN :sDate AND :eDate) OR (endDate BETWEEN :sDate AND :eDate) OR (:sDate BETWEEN startDate AND endDate) OR (:eDate BETWEEN startDate AND endDate)")
    fun getWeekSchedulesBetweenDateAndType(sDate: String, eDate: String, type: Int): LiveData<List<Schedule>>

}