package com.aknown389.dm.pageView.homeFeed.utility

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

open class SpeedLinearLayoutManager(context: Context): LinearLayoutManager(context) {
    private val MILLISECONDS_PER_INCH = 90f

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@SpeedLinearLayoutManager.computeScrollVectorForPosition(targetPosition)
            }override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }
        Log.d("Exception", position.toString())
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    override fun isSmoothScrolling(): Boolean {
        return super.isSmoothScrolling()
    }
}