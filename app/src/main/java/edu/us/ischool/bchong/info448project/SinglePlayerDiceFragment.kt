package edu.us.ischool.bchong.info448project

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Network
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.dice.*
import kotlinx.android.synthetic.main.flip.*
import kotlinx.android.synthetic.main.postgame_buttons.*
import android.os.Build
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.content.res.AppCompatResources
import android.text.Layout
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import game.Game
import game.GameApp
import game.GameFragment
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt


class SinglePlayerDiceFragment : Fragment(), GameFragment {
    override fun setNetworkPlayers(thisPlayers: ArrayList<Pair<String, String>>) {
      //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNetworkListener(networkListener: NetworkListener) {
        this.localHost=networkListener as RollTheDiceHost
    }

    override fun newInstance(game: Game): GameFragment {
        gameObj = game as Game
        return this
    }


    private lateinit var nearby: NearbyConnection
    private var listener: OnFragmentInteractionListener? = null
    lateinit var gameObj: Game
    lateinit var gyroscope: Sensor
    lateinit var accelerometer: Sensor
    //lateinit var linearAccelerometer:SensorManager
    lateinit var motionSensorController: SensorManager
    //lateinit var accelerometer:Sensor

    private lateinit var randomCharSet:Array<String>

    val PLAYER_KEY = "player"
    val PLAYERS_KEY = "players"
    val ID_SUFFIX = "Dice"
    lateinit var player: Pair<String, String>
    private lateinit var playerDiceVisual: ImageView

    //Animation variables
    var baseHeight = 100
    var baseWidth = 100
    lateinit var dimensionAnimators: HashMap<String, ValueAnimator>
    lateinit var playerDiceDimAnimator: ValueAnimator
    private var localHost:RollTheDiceHost?=null


    //Shows the name of the player who won
    fun showWinner(winner: Pair<String, Int>) {
        val scoreString = "${winner.first} won with ${winner.second}"
        //turn_text_view.text=scoreString
        //turn_text_view.visibility=View.VISIBLE
        Log.v("dice", scoreString)
    }


    //Returns the id string used in view id's
    private fun getIdString(id: String): String {
        return "$id$ID_SUFFIX"
    }


    //Draws all the players in the scrollview
    private fun drawPlayers(savedInstanceState: Bundle) {
        val playerNameTag: TextView = player_dice.findViewWithTag("player_name") as TextView
        //val inflater=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (savedInstanceState.containsKey(PLAYER_KEY)) {
            player = savedInstanceState.get(PLAYER_KEY) as Pair<String, String>
            playerNameTag.text = player.second
        } else {
            playerNameTag.text = getString(R.string.default_player_name)
        }
        playerDiceVisual = player_dice.findViewWithTag("dice_img") as ImageView
    }

    //Saves the current players
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (player != null) {
            outState.putSerializable("player", player)
        }
    }

    //When the game is started it will call this function.
    override fun onStart() {
        super.onStart()
        if(this.localHost!=null){
            localHost!!.setNearby(nearby)
            localHost!!.onStart()
        }
        this.gameObj!!.onFragmentStart()
        randomCharSet=arrayOf<String>("$","?","@","ß","∫")
        dimensionAnimators = hashMapOf<String, ValueAnimator>()
        val fragMananager = fragmentManager
        val fragTransaction = fragMananager!!.beginTransaction()
        /*if (opponent_dice.childCount > 0) {
            opponent_dice.removeAllViews()
        }*/
        fragTransaction.commit()
    }

    //When the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            drawPlayers(savedInstanceState)
        }
        restart_button.setOnClickListener {
            val ft = fragmentManager!!.beginTransaction()
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false)
            }
            postgame_buttons.visibility = View.GONE
            ft.detach(this).attach(this).commit()
        }
        end_game_button.setOnClickListener {
            postgame_buttons.visibility = View.GONE
            activity!!.finish()
        }

        nearby = NearbyConnection.instance
        if (nearby.isHosting()) {
        } else {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        playerDiceDimAnimator = ValueAnimator()
    }
    fun sendMessage(bundle: Bundle){
        val keyset=bundle.keySet()
        var jsonObj = JSONObject()
        keyset.map {
            try {
                // json.put(key, bundle.get(key)); see edit below
                jsonObj.put(it, JSONObject.wrap(bundle.get(it)));
            } catch (ex:java.lang.Exception) {
                //Handle exception here
            }
        }
      nearby.sendMessageAll("dice:${jsonObj.toString()}")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.dice, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    /*
    //Do not add a onAttach function
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }*/

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    //Starts the player rolling animation with the given force
    //Eat unit of force equals 1 radian of rotation per second
    fun diceRoll(force1: Float, force2: Float, currentRollEnergy: Double, duration: Long) {
        // TODO: test visuals
        if (playerDiceDimAnimator.isRunning) {
            //playerDiceDimAnimator.end()
        }else{
            playerDiceDimAnimator = setDimAnimator(player_dice, duration)
        }
    }

    //Reveals the roll for the opponent dice with the given id
    fun revealRoll(id: String, rollValue: Int) {
        val parentElement = root_dice_layout.findViewWithTag<TextView>("${id}Dice")
        parentElement.findViewWithTag<TextView>("dice_text").text = "$rollValue"
    }

    //Do the visuals for when a opponents dice is rolled
    fun opponentRolled(id: String, strength: Double, duration: Long) {
        // TODO:
        try {
            Log.v("diceSet", "Opponent Rolled Fragment listener started")
                //val elementGroup: ViewGroup = opponent_dice.findViewWithTag(getIdString(id)) as ViewGroup
            //val element: View = elementGroup.findViewWithTag("opponent_dice_box")
            if (dimensionAnimators.containsKey(id)) {
                val dimensionAnimator = dimensionAnimators.get(id)
                if(!dimensionAnimator!!.isRunning){
                    //dimensionAnimators.put(id, setDimAnimator(element, duration))
                }
            } else {
                //val element: View = opponent_dice.findViewWithTag(getIdString(id))
                //dimensionAnimators.put(id, setDimAnimator(element, duration))
            }
        } catch (err: Exception) {
            Log.e("dice", "Error finding opponent roll view")
        }
    }

    //Turns off all timers animating the player and opponents dice
    private fun cancelAllVisualTimers() {
        dimensionAnimators.map {
            it.value.cancel()
        }
        playerDiceDimAnimator.cancel()
    }
    fun displayNewTurn(playerName:String){
        //turn_text_view.visibility=View.VISIBLE
        //turn_text_view.text="$playerName's turn"
    }

    //Converts px values to dp units
    private fun dpToPx(dp: Int): Int {
        val density = context!!.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    //Rotates and changes the size of a view
    fun setDimAnimator(targetView: View, milliDuration: Long): ValueAnimator {
        Log.v("diceSet", "New animator set for $milliDuration")
        var targetTextView=(targetView as ViewGroup).findViewWithTag<TextView>("dice_text")
        var valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, Math.PI.toFloat() * 4f)
        valueAnimator.addUpdateListener {
            val animatedVal = it.animatedValue as Float
            var cosinedDimension = Math.cos(animatedVal.toDouble())
            targetView.rotation=(cosinedDimension*Math.PI*180f).toFloat()
            targetTextView.text=randomCharSet.random()

            Log.v("dice", "value changed to $animatedVal :$cosinedDimension");
            var params = targetView.layoutParams
            //params.height = dpToPx((baseHeight * cosinedDimension).toInt())
            params.width = dpToPx((baseWidth * cosinedDimension).toInt())
            targetView.layoutParams = params
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator) {
                //animation.cancel()
                targetView.rotation=0f
                var targetTextView=(targetView as ViewGroup).findViewWithTag<TextView>("dice_text")
                targetTextView.text="${(Math.random()*6+1).toInt()}"
            }

            override fun onAnimationCancel(animation: Animator) {
                targetView.layoutParams.height=baseHeight
                targetView.layoutParams.width=baseWidth
                targetView.rotation=0f
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = milliDuration
        valueAnimator.start()
        return valueAnimator
    }

    //Displays the restart buttons, needs testing
    fun displayRestart(yourScore: Int, winnerScore: Int, isWin: Boolean) {
        postgame_buttons.visibility = View.VISIBLE
    }

    //Reregisters the motion sensors
    override fun onResume() {
        super.onResume()

        this.motionSensorController =
            context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        motionSensorController.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let {
            this.gyroscope = it
        }
        motionSensorController.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
        }
        /*motionSensorController.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
        }*/
        motionSensorController.registerListener(gameObj, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        motionSensorController.registerListener(gameObj, this.gyroscope, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        cancelAllVisualTimers()
        gameObj!!.onPause()
        this.motionSensorController.unregisterListener(gameObj)
    }


    companion object : GameFragment {
        override fun setNetworkPlayers(thisPlayers: ArrayList<Pair<String, String>>) {

        }

        override fun setNetworkListener(networkListener: NetworkListener) {
            //Doesn't do anything ATM
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FlipFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        override fun newInstance(game: Game): SinglePlayerDiceFragment =
            SinglePlayerDiceFragment().apply {
                gameObj = game as NetworkGame
            }
    }
}
