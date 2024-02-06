package com.example.myschedule

import androidx.room.*

@Entity
data class Schedule (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val content: String,
    val date: String
)
