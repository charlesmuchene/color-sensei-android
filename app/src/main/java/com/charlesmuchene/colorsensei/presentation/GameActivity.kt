package com.charlesmuchene.colorsensei.presentation

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.charlesmuchene.colorsensei.R
import com.charlesmuchene.colorsensei.data.Game
import com.charlesmuchene.colorsensei.data.models.*
import com.charlesmuchene.colorsensei.utils.GameTimer
import com.charlesmuchene.colorsensei.utils.fadeView
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.coroutines.experimental.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

/**
 * Game screen
 */
class GameActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + responseStateJob

    private lateinit var gameTimer: GameTimer
    private val responseStateJob = Job()
    private var playDrawable: Drawable? = null
    private var gameDialog: AlertDialog? = null
    private var pauseDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        initializeActivity(savedInstanceState)
        initializeView()
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        pauseGame()
        EventBus.getDefault().unregister(this)
        gameDialog?.dismiss()
    }

    private fun initializeActivity(savedInstanceState: Bundle?) {
        val duration =
            savedInstanceState?.getLong(GameTimer.futureMillis, Game.gameDuration) ?: GameDuration.SHORT.duration
        gameTimer = GameTimer(duration)
    }

    private fun initializeView() {
        val noFabTint = ContextCompat.getColor(this, R.color.no_fab_tint)
        noFab.backgroundTintList = ColorStateList.valueOf(noFabTint)

        val yesFabTint = ContextCompat.getColor(this, R.color.yes_fab_tint)
        yesFab.backgroundTintList = ColorStateList.valueOf(yesFabTint)

        playDrawable = ContextCompat.getDrawable(this, R.drawable.ic_play)
        pauseDrawable = ContextCompat.getDrawable(this, R.drawable.ic_pause)

        enableControls(false)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putLong(GameTimer.futureMillis, gameTimer.remainingMillis)
    }

    private fun showGameResponseState(correct: Boolean) {
        val drawableRes = if (correct) R.drawable.ic_correct else R.drawable.ic_incorrect
        val drawable = ContextCompat.getDrawable(this, drawableRes)
        responseImageView.setImageDrawable(drawable)

        fadeView(responseImageView, true) {
            launch(responseStateJob) {
                delay(RESPONSE_DELAY)
                withContext(Dispatchers.Main) {
                    fadeView(responseImageView, false)
                }
            }
        }

    }

    private fun setListeners() {
        yesFab.setOnClickListener { respond(true) }
        noFab.setOnClickListener { respond(false) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        val playItem = menu?.findItem(R.id.playItem)
        playItem?.icon = if (Game.isGameOnPlay) pauseDrawable else playDrawable
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.playItem) {
            item.icon = if (Game.isGameOnPlay) playDrawable else pauseDrawable
            playOrResumeGame()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun playOrResumeGame() {
        if (Game.isGameOnPlay) {
            Game.pause()
            pauseGame()
        } else {
            if (Game.gameState.`is`(GameState.PAUSE)) resumeGame()
            else startGame()
            Game.play()
        }
    }

    /**
     * Display next game state
     */
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
        Game.gameDone()
        gameDialog?.dismiss()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(Game.gameDuration)
        val message = getString(R.string.game_scores_message, Game.getTally(), seconds)
        gameDialog = AlertDialog.Builder(this)
            .setTitle(R.string.game_over)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .create()
        gameDialog?.show()

        enableControls(false)
        invalidateOptionsMenu()
    }

    private fun timedOut() {
        gameDialog?.dismiss()
        gameDialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.time_up))
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .create()
        gameDialog?.show()

        updateGameTime(Game.gameDuration)
        invalidateOptionsMenu()
    }

    /**
     * Respond to game question
     *
     * @param same Game answer
     */
    private fun respond(same: Boolean) {
        if (!Game.isGameOnPlay) {
            Toast.makeText(this, getString(R.string.start_game), Toast.LENGTH_SHORT).show()
            return
        }

        val result = Game.respond(same)
        if (result) pointsTextView.text = Game.getTally()
        showGameResponseState(result)
        next()
    }

    private fun startGame() {
        Game.shuffle()
        gameTimer.start()
        enableControls(true)
        next()
    }

    private fun resumeGame() {
        gameTimer.start()
        enableControls(true)
    }

    /**
     * Pause game
     */
    private fun pauseGame() {
        gameTimer.cancel()
        enableControls(false)
    }

    /**
     * Update game time
     *
     * @param millis Game time in millis
     */
    private fun updateGameTime(millis: Long) {
        val time = String.format(Locale.getDefault(), "00:%02d", TimeUnit.MILLISECONDS.toSeconds(millis))
        timeTextView.text = time
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGameTimeChanged(timerMessage: TimerMessage) {
        when (timerMessage) {
            is TimerDone -> gameDone()
            is RemainingTime -> updateGameTime(timerMessage.millis)
        }
    }

    /**
     * Enable/Disable controls
     *
     * @param enable Enabled state
     */
    private fun enableControls(enable: Boolean) {
        yesFab.isEnabled = enable
        noFab.isEnabled = enable
        fadeView(instructionsTextView, !enable)
        fadeView(overlayView, !enable)
    }

    companion object {
        private const val RESPONSE_DELAY = 700L
    }

}
