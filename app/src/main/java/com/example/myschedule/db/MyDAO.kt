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

    @Query("SELECT * FROM Schedule WHERE date = :date")
    fun getScheduleByDate(date: String): LiveData<List<Schedule>>

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("DELETE FROM Schedule")
    suspend fun deleteAllSchedule()

    @Query("SELECT * FROM Schedule WHERE type = :type")
    fun getSchedulesByType(type: Int): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE date = :date AND type = :type")
    fun getScheduleByDateAndType(date: String, type: Int): LiveData<List<Schedule>>

}