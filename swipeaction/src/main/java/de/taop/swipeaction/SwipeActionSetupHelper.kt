package de.taop.swipeaction

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import de.taop.swipeaction.actions.SwipeAction

/**
 * Most of the Code was taken from here: https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete
 * and modified to allow a more generic implementation.
 *
 * @author Adrian Bernhart
 */

class SwipeActionSetupHelper {
    companion object { //static context

        fun setUpRecyclerView(context: Context, recyclerView: RecyclerView, action: SwipeAction) {
            setUpRecyclerView(context, recyclerView, action, action)
        }

        fun setUpRecyclerView(context: Context, recyclerView: RecyclerView, leftAction: SwipeAction, rightAction: SwipeAction) {
            // we won't recycle the viewHolder because otherwise it could
            // interfere with other actions since we change the size of the
            // item.

            val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                // we want to cache these and not allocate anything repeatedly in the onChildDraw method
                internal lateinit var leftBackground: Drawable
                internal lateinit var rightBackground: Drawable
                lateinit var background: Drawable
                internal lateinit var leftIcon: Drawable
                internal lateinit var rightIcon: Drawable
                lateinit var icon: Drawable
                internal var iconMargin: Int = 0
                internal var initiated: Boolean = false

                private fun init() {
                    leftBackground = ColorDrawable(context.universalGetColor(leftAction.actionBGColorID))
                    rightBackground = ColorDrawable(context.universalGetColor(rightAction.actionBGColorID))
                    leftIcon = ContextCompat.getDrawable(context, leftAction.actionIconID)
                    leftIcon.universalSetTint(context, leftAction.actionIconColorID)

                    rightIcon = ContextCompat.getDrawable(context, rightAction.actionIconID)
                    rightIcon.universalSetTint(context, rightAction.actionIconColorID)

                    iconMargin = context.resources.getDimension(R.dimen.swipe_icon_margin).toInt()
                    initiated = true
                }

                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val swipedPosition = viewHolder.adapterPosition
                    when (swipeDir) {
                        ItemTouchHelper.LEFT -> {
                            leftAction.performAction(viewHolder, ItemTouchHelper.LEFT)
                        }
                        ItemTouchHelper.RIGHT -> {
                            rightAction.performAction(viewHolder, ItemTouchHelper.RIGHT)
                        }
                    }
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    val itemView = viewHolder.itemView
                    //called for viewHolders which were swiped away
                    //not needed
                    if (viewHolder.adapterPosition == -1) {
                        return
                    }
                    if (!initiated) {
                        init()
                    }
                    //set up according to swipe direction
                    val itemHeight: Int
                    val intrinsicWidth: Int
                    val intrinsicHeight: Int

                    itemHeight = itemView.bottom - itemView.top
                    intrinsicWidth = itemHeight / 3
                    intrinsicHeight = itemHeight / 3

                    //get bounds of icon and color of background according to swipe direction
                    val iconLeft: Int
                    val iconRight: Int
                    val iconTop: Int
                    val iconBottom: Int

                    if (dX < 0.0f) { //swipe left
                        iconLeft = itemView.right - iconMargin - intrinsicWidth
                        iconRight = itemView.right - iconMargin
                        iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                        iconBottom = iconTop + intrinsicHeight
                    } else { //swipe right
                        iconLeft = itemView.left + iconMargin
                        iconRight = itemView.left + iconMargin + intrinsicWidth
                        iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                        iconBottom = iconTop + intrinsicHeight
                    }

                    background = if (dX < 0.0f) leftBackground else rightBackground
                    icon = if (dX < 0.0f) leftIcon else rightIcon

                    //set Background bounds to fill the view
                    background.setBounds(0, itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)

                    // draw left or right icon icon
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    icon.draw(c)

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }


            }
            val itemTouchHelper = ItemTouchHelper(callback)

            itemTouchHelper.attachToRecyclerView(recyclerView)

            //recyclerView.addItemDecoration(SwipeActionItemDecoration())

        }
    }

}