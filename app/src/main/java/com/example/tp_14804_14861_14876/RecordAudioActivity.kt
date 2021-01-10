package com.example.tp_14804_14861_14876

import android.Manifest
import android.app.AlertDialog
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.Chronometer
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*

class RecordAudioActivity : AppCompatActivity(),ConnectionReceiver.ConnectionReceiverListener {

    lateinit var btn_start: Button
    lateinit var btn_play: Button
    lateinit var timer_tv_counter: Chronometer

    lateinit var mr: MediaRecorder
    var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_audio)

        //Internet Connection
        ReceiverConnection.instance.setConnectionListener(this)

        mr = MediaRecorder()
        btn_start = findViewById<Button>(R.id.btn_start)
        btn_play = findViewById<Button>(R.id.btn_play)
        timer_tv_counter = findViewById<Chronometer>(R.id.timer_chromo_counter)

        btn_play.isEnabled = false
        timer_tv_counter.text = ""

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 111
            )
        btn_start.isEnabled = true

        //Start Recording
        btn_start.setOnClickListener {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val path = getExternalFilesDir("").toString() + "/" + timeStamp + ".mp3"
            mr.setAudioSource(MediaRecorder.AudioSource.MIC)
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mr.setMaxDuration(10000)
            mr.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            mr.setOutputFile(path)
            mr.prepare()
            mr.start()
            btn_play.isEnabled = false
            val timer = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timer_tv_counter.text = counter.toString()
                    counter++
                }

                override fun onFinish() {
                    timer_tv_counter.text = "Finished"
                    mr.stop()
                    btn_play.isEnabled = true
                    counter = 0

                }
            }
            timer.start()
        }

        //Play Recording
        btn_play.setOnClickListener {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val path = getExternalFilesDir("").toString() + "/" + timeStamp + ".mp3"
            var mp = MediaPlayer()
            mp.setDataSource(path)
            mp.prepare()
            mp.start()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        btn_start = findViewById<Button>(R.id.btn_start)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            btn_start.isEnabled = true

    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder,this)
        }
    }
}