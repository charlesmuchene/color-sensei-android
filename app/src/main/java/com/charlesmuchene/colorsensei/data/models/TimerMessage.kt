package com.charlesmuchene.colorsensei.data.models

/**
 * Timer message
 */
sealed class TimerMessage

class RemainingTime(val millis: Long) : TimerMessage()

object TimerDone : TimerMessage()