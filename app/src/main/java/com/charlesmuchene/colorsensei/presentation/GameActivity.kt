package com.charlesmuchene.colorsensei.presentation

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.charlesmuchene.colorsensei.R
import com.charlesmuchene.colorsensei.data.Game
import com.charlesmuchene.colorsensei.data.models.GameState
import com.charlesmuchene.colorsensei.data.models.Play
import kotlinx.android.synthetic.main.activity_game.*

/**
 * Game screen
 */
class GameActivity : AppCompatActivity() {

    private var scoreToast: Toast? = null
    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initializeView()
        setListeners()

        Game.shuffle()
        next() // TODO remove
    }

    private fun initializeView() {
        val noFabTint = ContextCompat.getColor(this, R.color.no_fab_tint)
        noFab.backgroundTintList= ColorStateList.valueOf(noFabTint)

        val yesFabTint = ContextCompat.getColor(this, R.color.yes_fab_tint)
        yesFab.backgroundTintList = ColorStateList.valueOf(yesFabTint)

        playDrawable = ContextCompat.getDrawable(this, R.drawable.ic_play)
        pauseDrawable = ContextCompat.getDrawable(this, R.drawable.ic_pause)

    }

    private fun showToast(message: String) {

        scoreToast?.cancel()

        val containerView =
            LayoutInflater.from(this).inflate(R.layout.game_toast_layout, findViewById(R.id.toastViewGroup))

        (containerView.findViewById<TextView>(R.id.toastTextView)).text = message

        scoreToast = Toast(this).apply {
            view = containerView
            duration = Toast.LENGTH_SHORT
        }
        scoreToast?.show()
    }

    private fun setListeners() {
        yesFab.setOnClickListener { respond(true) }
        noFab.setOnClickListener { respond(false) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.playItem) {

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun next() {
        val (play, state) = Game.getNextPlay()
        when (state) {
            GameState.DONE -> gameDone()
            GameState.TIME_OUT -> timedOut()
            else -> play?.let(::nextPlay)
        }
    }

    /**
     * Advance to next play
     *
     * @param play [Play] instance
     */
    private fun nextPlay(play: Play) {
        val (name, color) = play.ask()
        colorNameTextView.text = name
        colorView.setBackgroundColor(color)
    }

    private fun gameDone() {

    }

    private fun timedOut() {

    }

    private fun respond(same: Boolean) {
        val result = Game.respond(same)
        if (result) {
            showToast("Correct")
            pointsTextView.text = Game.getTally()
        } else {
            showToast("Incorrect")
        }
    }
}
