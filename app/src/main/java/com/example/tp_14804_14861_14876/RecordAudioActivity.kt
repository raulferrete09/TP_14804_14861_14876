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
    lateinit var record_btn_start: Button
    lateinit var record_btn_play: Button
    lateinit var timer_chromo_counter: Chronometer
    lateinit var filenametext: TextView

    lateinit var mr: MediaRecorder
    var counter = 0
    var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_audio)

        //Internet Connection
        ReceiverConnection.instance.setConnectionListener(this)

        // firebase info
        auth = FirebaseAuth.getInstance()
        val user:FirebaseUser? = auth?.currentUser
        val name:String? = user?.displayName
        //var photo = user?.photoUrl

        mr = MediaRecorder()
        record_btn_start = findViewById<Button>(R.id.record_btn_start)
        record_btn_play = findViewById<Button>(R.id.record_btn_play)
        timer_chromo_counter = findViewById<Chronometer>(R.id.timer_chromo_counter)
        filenametext = findViewById<TextView>(R.id.info_tv)

        record_btn_start.setBackground(resources.getDrawable(R.drawable.record_btn_stopped, null))
        record_btn_play.isEnabled = false
        timer_chromo_counter.text = ""

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
        record_btn_start.isEnabled = true

        //Start Recording
        record_btn_start.setOnClickListener {
            record_btn_start.setBackground(resources.getDrawable(R.drawable.record_btn_recording, null))
            record_btn_start.isEnabled = false

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            //val pathname = User + timeStamp + ".mp3"
            val path = getExternalFilesDir("").toString() + "/" + name + "_" + timeStamp + ".mp3"
            println(path)
            mr.setAudioSource(MediaRecorder.AudioSource.MIC)
            mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mr.setMaxDuration(10000)
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mr.setOutputFile(path)
            mr.prepare()
            mr.start()

            filenametext.setText("\"Recording, File Saved : " + name + "_" + timeStamp + ".mp3");

            timer_chromo_counter.setBase(SystemClock.elapsedRealtime())
            timer_chromo_counter.start()

            val timer = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    //timer_chromo_counter.text = counter.toString()
                    counter++
                }

                override fun onFinish() {
                    mr.stop()
                    timer_chromo_counter.stop()
                    filenametext.setText("Recording Stopped, File Saved : " + name + "_" + timeStamp + ".mp3");
                    timer_chromo_counter.text = "Finished"
                    record_btn_start.setBackground(resources.getDrawable(R.drawable.record_btn_stopped, null))
                    record_btn_start.isEnabled = true
                    record_btn_play.isEnabled = true
                    counter = 0
                }
            }
            timer.start()
        }

        timer_chromo_counter.text = ""
        //Play Recording
        record_btn_play.setOnClickListener {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val path = getExternalFilesDir("").toString() + "/" + name + "_" + timeStamp + ".mp3"
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
        record_btn_start = findViewById<Button>(R.id.record_btn_start)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            record_btn_start.isEnabled = true

    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder, this)
        }
    }
}