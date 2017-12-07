package de.taop.swipeaction

import android.content.Context
import android.graphics.Rect
import android.support.annotation.ColorRes
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Sets alternating Background Colors.
 *
 * @param context the given Context
 * @param evenColorID the color ID of even cells
 * @param oddColorID the color ID of odd cells
 * @Author Adrian Bernhart
 */
class AlternatingBackgroundItemDecoration(private val context: Context, @ColorRes private val evenColorID: Int, @ColorRes private val oddColorID: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        view.setBackgroundColor(context.universalGetColor(if (position.isEven()) evenColorID else oddColorID))
    }
}