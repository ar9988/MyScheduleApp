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

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM Schedule WHERE type == :type")
    fun getSchedulesByType(type: Int): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE startDate = :date")
    fun getScheduleByDate(date: String): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE :date BETWEEN startDate AND endDate")
    fun getSchedulesBetweenDate(date: String) : LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == 1 AND startDate BETWEEN :sDate AND :eDate) OR (type == 2 AND ((startDate BETWEEN :sDate AND :eDate) OR (endDate BETWEEN :sDate AND :eDate) OR (:sDate BETWEEN startDate AND endDate) OR (:eDate BETWEEN startDate AND endDate)))")
    fun getMonthSchedules(sDate: String, eDate: String): LiveData<List<Schedule>>

    @Query("SELECT name FROM Schedule WHERE (type == :type) AND ((startTime BETWEEN :searchStartTime AND :searchEndTime) OR (endTime BETWEEN :searchStartTime AND :searchEndTime) OR (:searchStartTime BETWEEN startTime AND endTime) OR (:searchEndTime BETWEEN startTime AND endTime))")
    fun conflictCheck(type: Int, searchStartTime: String, searchEndTime: String): String?

    @Query("SELECT name FROM Schedule WHERE (startDate == :dayOfWeek) AND ((startTime BETWEEN :searchStartTime AND :searchEndTime) OR (endTime BETWEEN :searchStartTime AND :searchEndTime) OR (:searchStartTime BETWEEN startTime AND endTime) OR (:searchEndTime BETWEEN startTime AND endTime))")
    fun conflictCheck2(dayOfWeek: Int, searchStartTime: String, searchEndTime: String): String?

    @Query("SELECT * FROM Schedule WHERE (type == :type) AND name LIKE '%' || :title || '%'")
    fun getSchedulesByTypeAndTitle(type: Int, title: String): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == :type) AND content LIKE '%' || :contents || '%'")
    fun getSchedulesByTypeAndContents(type: Int, contents: String): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == :type) AND name LIKE '%' || :title || '%' AND (:date BETWEEN startDate AND endDate)")
    fun getSchedulesByTypeAndDateAndTitle(type: Int, date: String, title: String): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == :type) AND content LIKE '%' || :contents || '%' AND (:date BETWEEN startDate AND endDate)")
    fun getSchedulesByTypeAndDateAndContents(type: Int, date: String, contents: String): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == :type) AND name LIKE '%' || :title || '%' AND content LIKE '%' || :contents || '%'")
    fun getSchedulesByTypeAndTitleAndContents(type: Int, title: String, contents: String): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == :type) AND name LIKE '%' || :title || '%' AND (:date BETWEEN startDate AND endDate) AND content LIKE '%' || :contents || '%'")
    fun getSchedulesByTypeAndDateAndTitleAndContents(
        type: Int,
        date: String,
        title: String,
        contents: String
    ): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == 0 AND (startDate == '0' OR startDate == :dayOfWeek)) OR ((type == 1) AND startDate = :date) OR ((type ==2 ) AND :date BETWEEN startDate AND endDate)")
    fun getDayFragmentData(date: String,dayOfWeek: Int): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE (type == 0) OR (type == 1 AND startDate BETWEEN :sDate AND :eDate) OR (type == 2 AND  (((startDate BETWEEN :sDate AND :eDate) OR (endDate BETWEEN :sDate AND :eDate) OR (:sDate BETWEEN startDate AND endDate) OR (:eDate BETWEEN startDate AND endDate))))")
    fun getWeekSchedule(sDate: String, eDate: String): LiveData<List<Schedule>>


}