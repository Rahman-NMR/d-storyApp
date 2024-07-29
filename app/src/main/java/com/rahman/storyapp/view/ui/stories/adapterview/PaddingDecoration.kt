package com.rahman.storyapp.view.ui.stories.adapterview

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PaddingDecoration(context: Context, private val padding: Int) : RecyclerView.ItemDecoration() {
    private val displayMetrics = context.resources.displayMetrics

    private fun dpToPx(dp: Int): Int {
        return (dp * displayMetrics.density).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val screenWidthPx = displayMetrics.widthPixels

        val horizontalPadding = if (screenWidthPx > dpToPx(500)) {
            (screenWidthPx - dpToPx(500)) / 2
        } else 0

        outRect.left = horizontalPadding
        outRect.right = horizontalPadding

        if (parent.getChildAdapterPosition(view) < state.itemCount - 1) {
            outRect.bottom = dpToPx(padding)
        }
    }
}