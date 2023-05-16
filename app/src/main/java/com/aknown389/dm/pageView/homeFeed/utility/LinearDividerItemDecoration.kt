package com.aknown389.dm.pageView.homeFeed.utility

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearDividerItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = spacing
    }
}