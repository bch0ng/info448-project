package game

import android.hardware.SensorEventListener
import java.io.Serializable

interface Game: Serializable, SensorEventListener {
    var gameFragment: GameFragment?         //The game's fragment
    fun onStart(name:String)                //Called by the fragment or application
    fun onRegisterMotionListener()          //when motion listeners are registered
    fun onEnd():Int                         //Make sure to have this called by the fragment or application's onEnd function
    fun onFragmentStart()                   //supposed to be called by the fragment
    fun onPause()                           //Supposed to be called by the fragment
}