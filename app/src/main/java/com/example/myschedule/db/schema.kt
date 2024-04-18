package com.example.myschedule.db

import androidx.room.*

@Entity
data class Schedule (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: Int,
    val name: String,
    val content: String,
    val startDate: String,
    val endDate:String,
    val times: String
)

