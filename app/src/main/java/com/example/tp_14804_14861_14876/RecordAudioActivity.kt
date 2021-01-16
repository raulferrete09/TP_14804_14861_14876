package com.example.tp_14804_14861_14876

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*

class RecordAudioActivity : AppCompatActivity(),ConnectionReceiver.ConnectionReceiverListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_audio)

        //Internet Connection
        ReceiverConnection.instance.setConnectionListener(this)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder,this)
        }
    }
}