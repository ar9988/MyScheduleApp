package com.example.myschedule.db

import androidx.room.*

@Entity
data class Schedule (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val content: String,
    val date: String,
    val times: String
)
