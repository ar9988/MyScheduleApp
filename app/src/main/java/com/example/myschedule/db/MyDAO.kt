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
    suspend fun getScheduleByDate(date: String): List<Schedule>

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

}