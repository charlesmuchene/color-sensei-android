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
class Timer(futureMillis: Long) : CountDownTimer(futureMillis, 1_000) {

    override fun onFinish() {
        EventBus.getDefault().post(TimerDone)
    }

    override fun onTick(millisUntilFinished: Long) {
        EventBus.getDefault().post(RemainingTime(millisUntilFinished))
    }
}