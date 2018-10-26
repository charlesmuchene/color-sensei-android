package com.charlesmuchene.colorsensei.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.charlesmuchene.colorsensei.R
import com.charlesmuchene.colorsensei.data.Game
import com.charlesmuchene.colorsensei.data.models.Failure
import com.charlesmuchene.colorsensei.data.models.Result
import com.charlesmuchene.colorsensei.data.models.Success
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Start activity
 */
class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        if (Game.isInitialized) resumeGame()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().register(this)
    }

    /**
     * On load data result
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadDataResult(result: Result) {
        when (result) {
            is Success -> resumeGame()
            is Failure -> showErrorLoadingGameData()
        }
    }

    /**
     * Resume game
     */
    private fun resumeGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Show error loading game data
     */
    private fun showErrorLoadingGameData() {

    }
}