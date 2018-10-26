package com.charlesmuchene.colorsensei.data

import com.charlesmuchene.colorsensei.data.models.GameDuration
import com.charlesmuchene.colorsensei.data.models.GameState
import com.charlesmuchene.colorsensei.data.models.Play
import java.util.*

/**
 * Game representation
 */
object Game {

    private var scores = 0
    private var playCount: Int = 0
    private var state = GameState.NEW
    private var currentPlay: Play? = null
    private var gameData = mutableListOf<Play>()
    private var duration: GameDuration = GameDuration.SHORT

    val isInitialized: Boolean
        get() = ColorData.isLoaded

    /**
     * Shuffle game play
     */
    fun shuffle() {
        reset()
        val plays = duration.plays
        val questions = ColorData.getColors(plays)
        val answers = questions.shuffled(Random(System.nanoTime()))

        repeat(plays) { index ->
            val play = Play(questions[index], answers[index])
            gameData.add(play)
        }

    }

    private fun reset() {
        playCount = 0
        gameData.clear()
        currentPlay = null
        state = GameState.NEW
    }

    /**
     * Get next play
     *
     * @return [Pair] of [Play] and [GameState]
     */
    fun getNextPlay(): Pair<Play?, GameState> {

        currentPlay = gameData.elementAtOrNull(playCount++)

        if (currentPlay == null || playCount == duration.plays) {
            state = GameState.DONE
            return Pair(currentPlay, state)
        }

        state = GameState.PLAY
        return Pair(currentPlay, state)

    }

    fun respond(same: Boolean): Boolean {
        val play = currentPlay ?: throw IllegalStateException("Invalid game state. Current play is null!")
        val response = play.respond(same)
        if (response) scores++
        return response
    }

    fun getTally(): String {
        return "$scores/${duration.plays}"
    }
}