package com.charlesmuchene.colorsensei.utils

import android.view.View
import com.charlesmuchene.colorsensei.extensions.gone
import com.charlesmuchene.colorsensei.extensions.isVisible
import com.charlesmuchene.colorsensei.extensions.visible


/**
 * Fade in|out the given view
 *
 * @param view [View]
 * @param shouldShow Fade in or out
 * @param duration Animation animation
 * @param block Code block executed when the animation finishes
 */
fun fadeView(view: View, shouldShow: Boolean, duration: Long = 200L, block: (() -> Unit)? = null) {

    if ((view.isVisible && shouldShow) || (!view.isVisible && !shouldShow)) return

    val startAlpha = if (shouldShow) 0f else 1f

    view.alpha = startAlpha
    view.visible()
    view.animate()
        .alpha(1f - startAlpha)
        .setDuration(duration)
        .withEndAction {
            if (!shouldShow) {
                view.gone()
                view.alpha = 1f
            }
            block?.invoke()
        }
        .start()
}