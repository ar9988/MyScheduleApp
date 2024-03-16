package com.example.myschedule.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.Paint
import android.graphics.PathMeasure
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.myschedule.databinding.DayLayoutBinding
import com.example.myschedule.db.Schedule
import kotlin.math.cos
import kotlin.math.sin

class TimePiece(
    context: Context,
    attrs: AttributeSet?,
    val schedule: Schedule,
    private val colorCode: Int,
    private val binding: DayLayoutBinding
) : View(context, attrs) {
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        color = colorCode
    }
    private val paint2 = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 5f
    }
    private val rect = RectF()
    private val path = Path()
    private val pathPaint = Paint().apply {
        style = Paint.Style.STROKE
        alpha = 0
    }
    private val textPaint = Paint().apply {
        textSize = 50F
        color = Color.BLACK
        style = Paint.Style.FILL
    }
    private val pathMeasure = PathMeasure()
    private var startAngle = 0F
    private var endAngle =0F
    private var centralAngle= 0F
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val times = schedule.times.split("-")
        val startTime = (times[0].toFloat() * 60 + times[1].toFloat()) * 0.25
        val endTime = (times[2].toFloat() * 60 + times[3].toFloat()) * 0.25
        val sweepAngle = if (endTime > startTime) endTime - startTime else (24F * 60 * 0.25 - startTime) + endTime
        startAngle = startTime.toFloat()-90F
        if(startAngle<0F){
            startAngle += 360F
        }
        endAngle = (startAngle+sweepAngle).toFloat()
        if(endAngle>360F){
            endAngle -= 360F
        }
        centralAngle = (startAngle+sweepAngle/2).toFloat()
        if(centralAngle>360F){
            centralAngle -= 360F
        }
        val rectSize = resources.displayMetrics.density * 300
        val rectLeft = (binding.watchCenter.width - rectSize) / 2
        val rectTop = (binding.watchCenter.height - rectSize) / 2
        rect.set(
            rectLeft,
            rectTop,
            rectLeft + rectSize,
            rectTop + rectSize
        )
        canvas.drawArc(rect, ((startTime - 90F).toFloat()), sweepAngle.toFloat(), true, paint)
        canvas.drawArc(rect, ((startTime - 90F).toFloat()), sweepAngle.toFloat(), true, paint2)

        val radius = resources.displayMetrics.density * 300/2
        val startX = (binding.watchCenter.width / 2).toFloat()
        val startY = (binding.watchCenter.height / 2).toFloat()
        val endX = (startX + radius * cos(Math.toRadians(centralAngle.toDouble()))).toFloat()
        val endY = (startY + radius * sin(Math.toRadians(centralAngle.toDouble()))).toFloat()
        path.reset()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)
        pathMeasure.setPath(path, false)
        val len = pathMeasure.length
        val textLength = textPaint.measureText(schedule.name)
        val textHeight = textPaint.fontMetrics.bottom - textPaint.fontMetrics.top
        val vOffset = (textHeight / 2) - (textPaint.fontMetrics.bottom)
        val hOffset = len * 0.6 - textLength*0.5
        canvas.drawPath(path, pathPaint)
        canvas.drawTextOnPath(schedule.name, path, hOffset.toFloat(), vOffset, textPaint)
    }

    fun getAngles(): Pair<Float, Float> {
        return Pair(startAngle, endAngle)
    }
    fun getCentralAngle(): Float{
        return centralAngle
    }

}
