package com.charlesmuchene.colorsensei.utils

import android.os.CountDownTimer
import com.charlesmuchene.colorsensei.data.models.RemainingTime
import com.charlesmuchene.colorsensei.data.models.TimerDone
import com.charlesmuchene.colorsensei.data.models.TimerMessage
import org.greenrobot.eventbus.EventBus

/**
 * Game timer.
 *
 * The timer defaults to 1 second intervals.
 *
 * @param futureMillis Duration of the timer
 */
class GameTimer(futureMillis: Long) : CountDownTimer(futureMillis, 1_000) {

    var remainingMillis: Long = futureMillis
        private set

    override fun onFinish() {
        EventBus.getDefault().post(TimerDone)
    }

    override fun onTick(millisUntilFinished: Long) {
        remainingMillis = millisUntilFinished
        EventBus.getDefault().post(RemainingTime(millisUntilFinished))
    }

    companion object {
        const val futureMillis = "future_millis"
    }
}