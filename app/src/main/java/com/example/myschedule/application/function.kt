package com.example.myschedule.application

fun String.extractYear(): Int {
    return this.replace("[^0-9]".toRegex(), "").toInt()
}

fun String.extractMonth(): Int {
    return this.replace("[^0-9]".toRegex(), "").toInt() - 1
}