package com.example.mytask

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecorator(
    topSpaceDp: Int,
    bottomSpaceDp: Int,
    betweenSpaceDp: Int
) : RecyclerView.ItemDecoration() {

    private val topSpace = dpToPx(topSpaceDp)
    private val bottomSpace = dpToPx(bottomSpaceDp)
    private val betweenSpace = dpToPx(betweenSpaceDp)

    // настроить конвертацию из px в dp. topSpaceDpToPx. Rect ожидает px - DONE
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position == 0) {
            outRect.top = topSpace
        } else {
            outRect.top = betweenSpace
        }

        if (position == itemCount - 1) {
            outRect.bottom = bottomSpace
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}
