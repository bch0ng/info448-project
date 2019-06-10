package Game

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import edu.us.ischool.bchong.info448project.R

class GameActivity : AppCompatActivity(), GamelistFragment.OnGameInteractionListener,
    ScoreBoardFragment.OnScoreboardInteractionListener {

    private lateinit var game: Game
    private lateinit var identity: String
    private lateinit var mode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        identity = intent.getStringExtra("IDENTITY")

        mode = intent.getStringExtra("GAMEMODE")
        Log.e("game","The mode is"+mode)
        onGameSelect(mode,identity)
    }

    override fun onGameSelect(playmode: String, useridentity: String) {
        Log.e("game","In onGameSelect")
        val gameSelectionFragment =
            GamelistFragment.newInstance(playmode, useridentity)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.framegame, gameSelectionFragment!!, "game_fragment")
            .commit()
    }

    override fun onGameStart(gamechoice: String) {
        Log.i("TEST", "gamechoice: $gamechoice")
        if (gamechoice == "Shake the Soda") {
            game = SodaShake(this)
        }
        var gameFragment = game.gameFragment as Fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.framegame, gameFragment!!, "game_fragment")
            .commit()
        game.OnStart()
    }

    fun onGameResult(username: String, useridentity: String, gamechoice: String, userscore: String, playmode: String) {
        val scoreBoardFragment = ScoreBoardFragment.newInstance(username, useridentity, gamechoice, userscore, playmode)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.framegame, scoreBoardFragment!!, "game_fragment")
            .commit()
    }

    override fun onEndCycle() {
        game.OnEnd()
        onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
