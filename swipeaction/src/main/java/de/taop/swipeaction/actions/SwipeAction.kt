package de.taop.swipeaction.actions

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView

/**
 * Extends this class to define a custom SwipeAction.
 *
 * @constructor creates a new Swipe Action
 * @param recyclerView the recyclerview to shich the action is bound
 * @param name the name of the action (optional)
 * @param actionIconID the drawable id of the displayed icon
 * @param actionIconColorID the color id of the displayed icon (default: white)
 * @param actionBGColorID the backgroundcolor of the action
 * @author Adrian Bernhart
 */
abstract class SwipeAction(val recyclerView: RecyclerView, val name: String = "NONE", @DrawableRes val actionIconID: Int, @ColorRes val actionIconColorID: Int = android.R.color.white, @ColorRes val actionBGColorID: Int) {

    val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> = recyclerView.adapter
    val context: Context = recyclerView.context

    /**
     * Called when a swipe is registered. Needs to be implemented!
     *
     * @param swipedHolder the swiped ViewHolder
     * @param swipeDirection the swiped direction
     */
    abstract fun performAction(swipedHolder: RecyclerView.ViewHolder, swipeDirection: Int)
}
