package com.example.tp_14804_14861_14876

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer

class NetworkConnectionActivity : AppCompatActivity() {

    lateinit var layoutDisconnected: LinearLayout
    lateinit var layoutConnected: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_connection)

        //layoutDisconnected = findViewById<LinearLayout>(R.id.layoutDisconnected)
        //layoutConnected = findViewById<LinearLayout>(R.id.layoutConnected)

        val networkConnection  = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->

            if(isConnected) {
                //layoutDisconnected.visibility = View.GONE
                //layoutConnected.visibility = View.VISIBLE
            } else {
                //layoutDisconnected.visibility = View.VISIBLE
                //layoutConnected.visibility = View.GONE
            }

        })

    }
}