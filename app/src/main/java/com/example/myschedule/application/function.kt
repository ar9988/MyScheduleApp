package com.example.myschedule.application

fun String.extractYear(): Int {
    return this.replace("[^0-9]".toRegex(), "").toInt()
}

fun String.extractMonth(): Int {
    return this.replace("[^0-9]".toRegex(), "").toInt() - 1
}

fun calculateTime(h:String,m:String): Int{
    return h.toInt()*60+m.toInt()
}

fun calculateTime2(h:String,m:String): Double{
    return (h.toFloat()*60+m.toFloat())*0.25
}