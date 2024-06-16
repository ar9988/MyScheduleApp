package com.example.myschedule.recyclerview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(
    private val spacing: Int,
    private val spanCount: Int
) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontalDividers(canvas, parent)
        drawVerticalDividers(canvas, parent)

    }

    private fun drawHorizontalDividers(canvas: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val left = child.left
            val right = child.right
            val bottom = child.bottom
            canvas.drawLine(left.toFloat(), bottom.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            if(spanCount!=7){
                if(i<spanCount+1){
                    canvas.drawLine(left.toFloat(), child.top.toFloat(), right.toFloat(), child.top.toFloat(), paint)
                }
            }else{
                if(i<7){
                    canvas.drawLine(left.toFloat(), child.top.toFloat(), right.toFloat(), child.top.toFloat(), paint)
                }
            }
        }
    }

    private fun drawVerticalDividers(canvas: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        val idx :MutableList<Int> = mutableListOf()
        if(spanCount!=7){
            idx.add(0)
            idx.add(spanCount+1)
            var tmp = spanCount+8
            while(tmp<childCount){
                idx.add(tmp)
                tmp +=7
            }
        }
        else{
            idx.add(0)
            var tmp = 7
            while(tmp<childCount){
                idx.add(tmp)
                tmp +=7
            }
        }
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val top = child.top
            val bottom = child.bottom
            val right = child.right
            canvas.drawLine(right.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
        for (j in idx){
            val child = parent.getChildAt(j)
            val top = child.top
            val bottom = child.bottom
            val left = child.left
            canvas.drawLine(left.toFloat(), top.toFloat(), left.toFloat(), bottom.toFloat(), paint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(spacing, spacing, spacing, spacing)
    }
}
