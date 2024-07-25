package com.rahman.storyapp.view.ui.stories.adapterview

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PaddingDecoration(private val context: Context, private val padding: Int) : RecyclerView.ItemDecoration() {
    private fun dpToPx(): Int {
        return (padding * context.resources.displayMetrics.density).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) < state.itemCount - 1) {
            outRect.bottom = dpToPx()
        }
    }
}