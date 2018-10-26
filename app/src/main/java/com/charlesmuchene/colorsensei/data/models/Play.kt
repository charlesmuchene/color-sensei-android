package com.charlesmuchene.colorsensei.data.models

/**
 * A play class
 *
 * @param question Play's question
 * @param answer Play's answer
 */
data class Play(val question: SColor, val answer: SColor) {

    /**
     * Respond to play
     *
     * @param same `true` that the question == answer, `false` otherwise
     * @return `true` if question == answer, `false` otherwise
     */
    fun respond(same: Boolean): Boolean = (question == answer) == same

    /**
     * Ask about this play
     */
    fun ask(): Pair<String, Int> {
        return Pair(question.name, answer.getColor())
    }

}