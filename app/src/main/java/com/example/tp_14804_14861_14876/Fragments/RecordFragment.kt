package com.example.tp_14804_14861_14876.Fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import com.example.tp_14804_14861_14876.Activitys.MainActivity
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordFragment : Fragment(), View.OnClickListener {


    var navController: NavController? = null
    var isRecording = true
    var counter = 0
    var auth : FirebaseAuth? = null


    lateinit var mr: MediaRecorder
    lateinit var record_btn_list: Button
    lateinit var record_btn_start: Button
    lateinit var timer_chromo_counter: Chronometer
    lateinit var filenametext: TextView
    lateinit var back_btn_arrow: Button

    lateinit var intent:Intent

    lateinit var audioListFragment: AudioListFragment
    lateinit var transaction: FragmentTransaction

    lateinit var bytes: ByteArray
    lateinit var base64: String

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                RecordFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //navController = Navigation.findNavController(view)
        record_btn_list = view.findViewById<Button>(R.id.record_btn_list)
        record_btn_start = view.findViewById<Button>(R.id.record_btn_start)
        timer_chromo_counter = view.findViewById<Chronometer>(R.id.timer_chromo_counter)
        filenametext = view.findViewById<TextView>(R.id.info_tv)
        back_btn_arrow = view.findViewById<Button>(R.id.back_btn_arrow)


        record_btn_list.setOnClickListener(this)
        record_btn_start.setOnClickListener(this)
        back_btn_arrow.setOnClickListener(this)

        //Check permission
        if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    111
            )

    }

    override fun onClick(v: View) {
        intent = Intent(activity, MainActivity::class.java)
        record_btn_start.isEnabled = true
        when (v.id) {
            R.id.record_btn_list -> {
                audioListFragment = AudioListFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, audioListFragment)
                transaction.commit()
            }
            R.id.record_btn_start ->
                if (isRecording) {
                    //Start record
                    startRecording()
                    record_btn_start.background = resources.getDrawable(
                        R.drawable.record_btn_recording,
                            null
                    )
                    isRecording = true
                }
            R.id.back_btn_arrow ->
                startActivity(intent)
        }

    }

    private fun startRecording() {

        //Firebase info
        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth?.currentUser
        val name:String? = user?.displayName

        mr = MediaRecorder()
        record_btn_start.isEnabled = false
        record_btn_list.isEnabled = false
        back_btn_arrow.isEnabled = false

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //Get app external directory path
        //name -> path = User + timeStamp + ".mp3"
        val pathname = name + "_" + timeStamp + ".mp3"
        val path = requireActivity().getExternalFilesDir("/")!!.toString() + "/" + pathname

        mr.setAudioSource(MediaRecorder.AudioSource.MIC)
        mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mr.setMaxDuration(10000)
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mr.setOutputFile(path)
        mr.prepare()
        mr.start()

        filenametext.text = "\"Recording, File Saved : " + pathname;

        timer_chromo_counter.base = SystemClock.elapsedRealtime()
        timer_chromo_counter.start()

        val timer = object : CountDownTimer(11000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                //timer_chromo_counter.text = counter.toString()
                counter++
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                mr.stop()
                timer_chromo_counter.stop()
                filenametext.text = "Recording Stopped, File Saved : " + pathname;
                timer_chromo_counter.text = "Finished"
                record_btn_start.background = resources.getDrawable(
                    R.drawable.record_btn_stopped,
                        null
                )
                record_btn_start.isEnabled = true
                record_btn_list.isEnabled = true
                back_btn_arrow.isEnabled = true
                counter = 0

                /*bytes = File(pathname).readBytes()
                base64 = Base64.getEncoder().encodeToString(bytes)
                //println(base64)
                var map = mutableMapOf<String,Any>()
                map["mp3 file"] = base64
                println(map)*/
            }
        }
        timer.start()

    }
    private fun stopRecording() {
        //Stop Timer, very obvious
        //Change text on page to file saved
        //filenametext.text = "Recording Stopped, File Saved : " + path

        //Stop media recorder and set it to null for further use to record new audio
        mr.stop()
        mr.release()
    }

    private fun checkPermissions(): Boolean {
        //Check permission
        if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    111
            )
        return true
    }

}