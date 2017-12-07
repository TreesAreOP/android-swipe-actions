package de.taop.swipeaction.actions


/**
 * The Adapter which uses a SwipeAction needs to implement this Interface.
 *
 * @author Adrian Bernhart
 */

interface SwipeActionAdapter {
    /**
     * Provides functionality to add an item to the data set.
     * [android.support.v7.widget.RecyclerView.Adapter.notifyItemInserted] should be called here!
     */
    fun addItemAt(position: Int, item: Any)

    /**
     * Provides functionality to remove an item to the data set.
     * [android.support.v7.widget.RecyclerView.Adapter.notifyItemRemoved] should be called here!
     */
    fun removeItemAt(position: Int)

    /**
     * Returns the Item at the given position
     */
    fun getItemAt(position: Int): Any

    /**
     * Checks if the adapter contains an item
     */
    fun containsItem(item: Any): Boolean

    fun getIndexOfItem(itemAt: Any): Int
}
