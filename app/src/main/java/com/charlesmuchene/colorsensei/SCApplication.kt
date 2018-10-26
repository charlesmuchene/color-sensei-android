package com.charlesmuchene.colorsensei

import android.app.Application
import com.charlesmuchene.colorsensei.data.ColorData
import com.charlesmuchene.colorsensei.data.models.Failure
import com.charlesmuchene.colorsensei.data.models.Success
import com.charlesmuchene.colorsensei.utils.COLOR_DATA_FILENAME
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.IOException

/**
 * Application class
 */
class SCApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeLogging()
        loadGameData()
    }

    /**
     * Load game data in background thread
     */
    private fun loadGameData() {
        try {
            val inputStream = assets.open(COLOR_DATA_FILENAME)
            GlobalScope.launch(Dispatchers.IO) {
                val result = ColorData.load(inputStream)
                val message = if (result) Success else Failure
                EventBus.getDefault().post(message)
            }
        } catch (e: IOException) {
            EventBus.getDefault().post(Failure)
        }
    }

    /**
     * Initialize logging
     */
    private fun initializeLogging() {
        Timber.plant(Timber.DebugTree())
    }
}