package com.example.tp_14804_14861_14876
import android.app.Application

class ReceiverConnection: Application() {
    companion object {
        @get:Synchronized
        lateinit var instance:ReceiverConnection
    }

    override fun onCreate(){
        super.onCreate()
        instance = this
    }

    fun setConnectionListener(listener: ConnectionReceiver.ConnectionReceiverListener){
        ConnectionReceiver.connectionReceiverListener = listener
    }
}