package com.charlesmuchene.colorsensei.data.models

import android.graphics.Color

/**
 * SColor data class
 */
data class SColor(val name: String, val hex: String) {

    /**
     * Get a color representation
     */
    fun getColor(): Int = Color.parseColor(hex)

}