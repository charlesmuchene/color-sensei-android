package com.charlesmuchene.colorsensei.data.models

/**
 * Game State
 */
enum class GameState {
    NEW, PLAY, PAUSE, TIME_OUT, DONE;

    /**
     * Check if this state is the same as the given state
     *
     * @return Boolean
     */
    fun `is`(another: GameState): Boolean = another == this

}