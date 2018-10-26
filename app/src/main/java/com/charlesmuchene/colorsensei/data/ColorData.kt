package com.charlesmuchene.colorsensei.data

import androidx.annotation.WorkerThread
import com.charlesmuchene.colorsensei.data.models.SColor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.*

/**
 * Color data class
 */
object ColorData {

    private val colorList = mutableListOf<SColor>()
    private var loaded = false

    val isLoaded: Boolean
        get() = loaded

    /**
     * Load colors to list
     *
     * @param inputStream [InputStream] of the color data
     * @return `true` if the colors were loaded successfully,
     * `false` otherwise
     */
    @WorkerThread
    fun load(inputStream: InputStream): Boolean {
        if (loaded) return true

        val colors = loadColors(inputStream)
        colorList.addAll(colors)

        loaded = true
        return loaded
    }

    /**
     * Load colors from the given stream
     *
     * @param stream [InputStream] to read colors from
     * @return [List] of [SColor]s
     */
    fun loadColors(stream: InputStream): List<SColor> {
        val bytes = BufferedInputStream(stream).readBytes()
        val string = String(bytes)
        val sColorType = object : TypeToken<List<SColor>>() {}.type
        return Gson().fromJson(string, sColorType)
    }

    /**
     * Get the given number of colors
     *
     * @param count Color count
     * @return [List] of [SColor]
     */
    fun getColors(count: Int): List<SColor> {
        val checkedCount = if (count >= colorList.size) colorList.size - 1 else count
        return getIndices(checkedCount, colorList.size).map { index ->
            colorList[index]
        }
    }

    /**
     * Generate a list of the given number of indices
     *
     * @return [List] of color indices
     */
    fun getIndices(count: Int, max: Int): List<Int> {
        val randomizer = Random(System.nanoTime())
        val list = mutableSetOf<Int>()

        while (list.size != count) list.add(randomizer.nextInt(max))

        return list.toList().shuffled()
    }

}