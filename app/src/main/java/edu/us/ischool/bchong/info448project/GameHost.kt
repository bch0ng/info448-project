package edu.us.ischool.bchong.info448project

import android.os.Bundle
import java.io.Serializable

interface GameHost: Serializable {
    var localClient:NetworkGame
    fun newInstance(game:NetworkGame):GameHost
    fun newMessage(message:Bundle)
    fun sendMessage(message: Bundle)
    fun setPlayers(players:ArrayList<Pair<String,String>>)
    fun kickPlayer(id:String)
    fun onStart()
    fun onEnd()
}