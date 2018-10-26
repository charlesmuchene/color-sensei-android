package com.charlesmuchene.colorsensei.data.models

/**
 * Game duration
 *
 * @param plays Total number of plays
 * @param duration Duration of the game
 */
enum class GameDuration(val plays: Int, val duration: Long) {

    SHORT(10, 10_000),
    MEDIUM(50, 30_000),
    LONG(100, 60_000)

}