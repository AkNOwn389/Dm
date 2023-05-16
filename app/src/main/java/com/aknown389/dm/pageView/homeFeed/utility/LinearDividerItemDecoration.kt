package com.aknown389.dm.pageView.homeFeed.utility

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aknown389.dm.R

class LinearDividerItemDecoration( private val spaceSize: Int, private val context:Context) : RecyclerView.ItemDecoration() {
    /*
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(offset, offset, offset, offset)
        outRect.bottom = offset
        view.setBackgroundColor(context.getColor(R.color.white))
        super.getItemOffsets(outRect, view, parent, state)
    }

     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        // Apply space for each item except the first one
        if (parent.getChildAdapterPosition(view) != 0) {
            if (parent.layoutManager is LinearLayoutManager) {
                if ((parent.layoutManager as LinearLayoutManager).orientation == RecyclerView.VERTICAL) {
                    outRect.top = spaceSize
                } else {
                    outRect.left = spaceSize
                }
            } else {
                outRect.left = spaceSize
            }
        }
    }
}