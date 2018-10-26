package com.charlesmuchene.colorsensei.extensions

import android.view.View

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

val View.isVisible
    get() = this.visibility == View.VISIBLE