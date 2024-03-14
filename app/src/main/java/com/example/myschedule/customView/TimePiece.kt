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
import kotlin.properties.Delegates

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
        rect.set(
            0F,
            0F,
            binding.watchCenter.width.toFloat(),
            binding.watchCenter.height.toFloat()
        )
        Log.d("angles","$startAngle, $endAngle")
        canvas.drawArc(rect, ((startTime - 90F).toFloat()), sweepAngle.toFloat(), true, paint)
        canvas.drawArc(rect, ((startTime - 90F).toFloat()), sweepAngle.toFloat(), true, paint2)

        path.reset()
        path.addCircle(
            (binding.watchCenter.width / 2).toFloat(),
            (binding.watchCenter.height / 2).toFloat(),
            (binding.watchCenter.width / 2.5).toFloat(),
            Path.Direction.CW
        )
        canvas.drawPath(path, pathPaint)

        pathMeasure.setPath(path, false)
        val len = pathMeasure.length
        val textLength = textPaint.measureText(schedule.name)
        val hOffset = len * ((startTime + sweepAngle / 2) / 360F) - len / 4 -textLength/2
        canvas.drawTextOnPath(schedule.name, path, hOffset.toFloat(), 0F, textPaint)
    }

    fun getAngles(): Pair<Float, Float> {
        return Pair(startAngle, endAngle)
    }

}
