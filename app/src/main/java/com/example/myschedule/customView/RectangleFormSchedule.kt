package com.example.myschedule.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.myschedule.application.calculateTime
import com.example.myschedule.db.Schedule

@SuppressLint("ViewConstructor")
class RectangleFormSchedule(
    context: Context,
    attrs: AttributeSet?,
    val schedule: Schedule,
    private val colorCode: Int,
) : View(context, attrs) {
    private val paint = Paint()
    private val times = schedule.startTime.split("-")+schedule.endTime.split("-")
    private val startTime = calculateTime(times[0],times[1])
    private val endTime = calculateTime(times[2],times[3])
    private val paint2 = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2f
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = colorCode
        val lowerLimit = 420
        val maxLimit = 1440
        if (endTime > startTime) {
            drawRectAndText(canvas, startTime, endTime)
        } else {
            if (endTime > lowerLimit) {
                drawRectAndText(canvas, lowerLimit, endTime)
            }
            if (startTime < maxLimit) {
                drawRectAndText(canvas, startTime, maxLimit)
            }
        }
    }

    private fun drawRectAndText(canvas: Canvas, start: Int, end: Int) {
        var duration = end - start
        var startLocation = start - 420

        if (startLocation < 0) {
            duration += startLocation
            startLocation = 0
        }
        if (startLocation + duration > 1020) {
            duration -= (startLocation + duration - 1020)
        }

        if (duration > 0) {
            val x = width / 2f
            val y = startLocation.dpToPx()
            paint.color = colorCode
            val rect = Rect(0, y, width, (startLocation + duration).dpToPx())
            canvas.drawRect(rect, paint)
            canvas.drawRect(rect, paint2)
            paint.color = Color.BLACK
            paint.textSize = 24f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(schedule.name, x, (y + duration.dpToPx() / 2).toFloat(), paint)
        }
    }

    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale).toInt()
    }
}