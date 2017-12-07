package de.taop.swipeaction

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.graphics.drawable.DrawableCompat

/**
 * Utilities as extension functions.
 *
 * @author Adrian Bernhart
 */

/**
 * Tints a drawable according to current android Version.
 *
 * @param context the given Context
 * @param colorId the tint color ID
 */
internal fun Drawable.universalSetTint(context: Context, @ColorRes colorId: Int) {
    val color: Int = context.universalGetColor(colorId)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.setTint(color)
    } else {
        val wrap = DrawableCompat.wrap(this)
        @Suppress("DEPRECATION")
        DrawableCompat.setTint(wrap, color)
    }
}

/**
 * Retrieves a color according to current android Version.
 *
 * @param colorId the ID of the color to retrieve
 * @return retrieved Color Int
 */
@ColorInt
internal fun Context.universalGetColor(@ColorRes colorId: Int): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return resources.getColor(colorId, theme)
    } else {
        @Suppress("DEPRECATION")
        return resources.getColor(colorId)
    }
}


/**
 * Checks if an Integer is even.
 *
 * @return whether or not the Int is even
 */
internal fun Int.isEven(): Boolean {
    return this % 2 == 0
}

/**
 * Adds an onAnimationEnd callback to a given ValueAnimator.
 *
 * @param endCallback function to call on animation end
 */
internal fun ValueAnimator.addEndListener(endCallback: () -> Unit) {
    addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            endCallback.invoke()
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
        }

    })
}