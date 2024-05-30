package com.example.myschedule.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Schedule::class],
    exportSchema = false, version = 5)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getMyDao() : MyDAO
}
