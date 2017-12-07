package de.taop.swipeaction.actions

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.annotation.*
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.SwipeDismissBehavior
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewTreeObserver
import de.taop.swipeaction.R
import de.taop.swipeaction.addEndListener


/**
 * Pending removal code was inspired by https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete
 *
 * An example implementation of an pending delete action.
 *
 * @constructor creates a new Delete Action
 * @param recyclerView the recyclerView to use
 * @param snackBarParentID the parent of the Snackbar to show if undo is enabled
 * @param allowUndo whether or not undo is allowed
 * @param iconID the resource id of the action icon (has default value)
 * @param colorID the color id of the actions background color (has default value)
 * @author Adrian Bernhart
 */
class DeleteAction(recyclerView: RecyclerView, @IdRes val snackBarParentID: Int, val allowUndo: Boolean = true,
                   @DrawableRes val iconID: Int = R.drawable.ic_delete_sweep_black_24dp,
                   @ColorRes val colorID: Int = R.color.colorPendingDelete)
    : SwipeAction(recyclerView, "pendingDelete", actionIconID = iconID, actionBGColorID = colorID) {

    @StringRes
    private var deleteUndoID: Int = R.string.deleteUndo

    @PluralsRes
    private var deleteSnackID: Int = R.plurals.deletedItems

    private var snackBar: Snackbar
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var PENDING_TIMEOUT_MS = 2500L

    private val pendingRemovalItems = ArrayList<RecyclerView.ViewHolder>()
    private val originalItemHeights = HashMap<RecyclerView.ViewHolder, Int>()

    /**
     * Called with the indices of removed items.
     */
    var itemsDeletedCallback: (IntArray) -> Unit = {}

    private lateinit var pendingRunnable: Runnable

    init { // create the snackbar and pending runnable
        adapter as SwipeActionAdapter
        snackBar = Snackbar.make((context as Activity).findViewById(snackBarParentID), "", Snackbar.LENGTH_INDEFINITE)

        updateSnackBar()

        disableSnackBarSwipeToDismiss(snackBar.view as Snackbar.SnackbarLayout)

        pendingRunnable = object : Runnable {
            override fun run() {

                deletePendingItems()

                snackBar.dismiss()

                //stop timer
                handler.removeCallbacks(this)

            }
        }
    }

    private fun disableSnackBarSwipeToDismiss(layout: Snackbar.SnackbarLayout) {
        layout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val lp = layout.layoutParams
                if (lp is CoordinatorLayout.LayoutParams) {
                    lp.behavior = object : SwipeDismissBehavior<Snackbar.SnackbarLayout>() {
                        override fun canSwipeDismissView(view: View): Boolean {
                            return false
                        }
                    }
                    layout.layoutParams = lp
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {

                    layout.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        })
    }

    override fun performAction(swipedHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
        if (allowUndo) {
            pendingRemoval(swipedHolder)
        } else {
            shrinkItemUntilGone(swipedHolder.adapterPosition) {
                finalRemoval(swipedHolder.adapterPosition)
                growItemToOriginalHeight(swipedHolder.adapterPosition)
            }
        }
    }

    /**
     * Removes an item from the adapter. The item is shrinked and
     * then marked as pending. An undo SnackBar is shown.
     *
     * @param holder the ViewHolder of the item to remove
     */
    private fun pendingRemoval(holder: RecyclerView.ViewHolder) {
        adapter as SwipeActionAdapter
        //get swiped item
        val item = adapter.getItemAt(holder.adapterPosition)
        if (!pendingRemovalItems.contains(item)) { //sparse array does not contain item
            pendingRemovalItems.add(holder)
            //adapter.removeItemAt(holder)
            shrinkItemUntilGone(holder.adapterPosition)

            //stop current timer
            handler.removeCallbacks(pendingRunnable)
            //and restart it
            handler.postDelayed(pendingRunnable, PENDING_TIMEOUT_MS)

            if (!snackBar.isShown) {
                snackBar.show()
                disableSnackBarSwipeToDismiss(snackBar.view as Snackbar.SnackbarLayout)
            }

            updateSnackBar()

        }
    }

    /**
     * Shrinks an item until it's gone with an animation.
     *
     * @param position the position to shrink
     * @param callback function to call on animation end
     */
    private fun shrinkItemUntilGone(position: Int, callback: () -> (Unit) = {}) {
        val child = recyclerView.findViewHolderForAdapterPosition(position)
        originalItemHeights.put(child, child.itemView.layoutParams.height)

        val heightAnimator = ValueAnimator.ofInt(child.itemView.height, 0)
        heightAnimator.repeatCount = 0
        heightAnimator.addUpdateListener({
            child.itemView.layoutParams.height = it.animatedValue as Int
            child.itemView.requestLayout()
        })

        heightAnimator.addEndListener(callback)

        heightAnimator.start()
    }

    /**
     * Grows an shrinked item to its original size with an animation.
     *
     * @param the position to restore
     */
    private fun growItemToOriginalHeight(position: Int) {
        val child = recyclerView.findViewHolderForAdapterPosition(position)
        val heightAnimator = originalItemHeights[child]?.let { ValueAnimator.ofInt(0, it) }
        heightAnimator?.repeatCount = 0
        heightAnimator?.addUpdateListener({
            child.itemView.layoutParams.height = it.animatedValue as Int
            child.itemView.requestLayout()
        })
        heightAnimator?.start()
    }

    /**
     * Undos all Pending items.
     */
    private fun undoPendingItems() {
        for (i in pendingRemovalItems) {
            growItemToOriginalHeight(i.adapterPosition)
            adapter.notifyItemChanged(i.adapterPosition)
        }

        pendingRemovalItems.clear()
    }

    /**
     * Permanently deletes the element at the given position.
     *
     * @param position the position to remove
     */
    private fun finalRemoval(position: Int) {
        adapter as SwipeActionAdapter
        val item = adapter.getItemAt(position)
        if (pendingRemovalItems.contains(item)) {
            pendingRemovalItems.removeAt(position)
        }

        if (adapter.containsItem(item)) {
            adapter.removeItemAt(position)
        }
    }

    /**
     * Deletes all pending items.
     *
     * @return true if items were deleted
     */
    fun deletePendingItems(): Boolean {
        var removed: Boolean = false
        pendingRemovalItems.forEach({
            //if the view was sized down we first need to restore its original height
            //otherwise the height will stay at 0 even after recycling
            it.itemView?.layoutParams?.height = originalItemHeights[it]
            finalRemoval(it.adapterPosition)
            removed = true
        })

        pendingRemovalItems.clear()

        if (removed) {
            itemsDeletedCallback.invoke(
                    pendingRemovalItems.map { it.adapterPosition }.toIntArray()
            )
        }

        return removed
    }

    /**
     * Updates the snackbar text.
     */
    private fun updateSnackBar() {
        snackBar.setText(context.resources.getQuantityString(R.plurals.deletedItems, pendingRemovalItems.size, pendingRemovalItems.size))
        snackBar.setAction(context.resources.getText(deleteUndoID)) {
            undoPendingItems()
            handler.removeCallbacks(pendingRunnable)
        }
    }

    /**
     * Sets the text of the snackbar. If you don't provide your own resource ids
     * the displayed text will be english. The Text of the Snackbar needs to be a quantity String!
     *
     * Example:
     * <plurals name="deletedItems">
     * <item quantity="one">deleted %1$d item</item>
     * <item quantity="other">deleted %1$d items</item>
     * </plurals>
     *
     * @param deleteUndo The Text of the Undo Button
     * @param deletedItems The text of the snackbar. Needs to be a Quantity String!
     * @see https://developer.android.com/guide/topics/resources/string-resource.html#Plurals
     */
    fun setSnackBarText(@StringRes deleteUndo: Int, @PluralsRes deletedItems: Int) {
        deleteUndoID = deleteUndo
        deleteSnackID = deletedItems
    }
}
