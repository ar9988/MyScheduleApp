package com.example.myschedule

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction

@Dao
interface MyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Query("SELECT * FROM Schedule")
    fun getAllSchedule(): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedule WHERE date = :date")
    suspend fun getScheduleByDate(date: String): List<Schedule>

    @Delete
    suspend fun deleteStudent(schedule: Schedule)

}