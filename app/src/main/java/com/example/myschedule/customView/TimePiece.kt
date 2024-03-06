package com.example.myschedule.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.myschedule.R
import com.example.myschedule.databinding.DayLayoutBinding
import com.example.myschedule.db.Schedule

class TimePiece(context: Context,attrs: AttributeSet?,private val schedule: Schedule, private val colorCode: Int,private val binding: DayLayoutBinding):View(context, attrs) {
    private val paint = Paint().apply {
        this.style = Paint.Style.FILL
        this.color = colorCode
    }
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val times = schedule.times.split("-")
        val startAngle = (times[0].toFloat() * 60 + times[1].toFloat()) * 0.25
        val sweepAngle = (times[2].toFloat() * 60 + times[3].toFloat()) * 0.25 - startAngle
        // 0F -> 06시 시작
        // 1 minute 0.25F
        rect.set(0F, 0F, binding.watchCenter.width.toFloat(), binding.watchCenter.height.toFloat())
        canvas.drawArc(rect, startAngle.toFloat() - 90F, sweepAngle.toFloat(), true, paint)
    }

}