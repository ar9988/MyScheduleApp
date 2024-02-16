package com.example.myschedule.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.myschedule.databinding.DayLayoutBinding

class TimePiece(context: Context,attrs: AttributeSet?, private val startAngle:Float, private val sweepAngle:Float, private val colorCode: Int,private val binding: DayLayoutBinding):View(context, attrs) {
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        this.color = colorCode
    }
    private val rect = RectF()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect.set(0F, 0F, binding.watchCenter.width.toFloat(), binding.watchCenter.height.toFloat())
        canvas.drawArc(rect, startAngle, sweepAngle, true, paint)
    }
}