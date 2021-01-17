package com.example.tp_14804_14861_14876

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity(),ConnectionReceiver.ConnectionReceiverListener {

    lateinit var button2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Internet Connection
        ReceiverConnection.instance.setConnectionListener(this)

        button2 = findViewById<Button>(R.id.button2)


        button2.setOnClickListener {
            startActivity(Intent(this,RecordAudioActivity::class.java))
        }
    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder,this)
        }
    }

}