package com.example.tp_14804_14861_14876

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
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


    lateinit var mr: MediaRecorder
    lateinit var record_btn_list: Button
    lateinit var record_btn_start: Button
    lateinit var timer_chromo_counter: Chronometer
    lateinit var filenametext: TextView



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

        navController = Navigation.findNavController(view)
        record_btn_list = view.findViewById<Button>(R.id.record_btn_list)
        record_btn_start = view.findViewById<Button>(R.id.record_btn_start)
        timer_chromo_counter = view.findViewById<Chronometer>(R.id.timer_chromo_counter)
        filenametext = view.findViewById<TextView>(R.id.info_tv)


        record_btn_list.setOnClickListener(this)
        record_btn_start.setOnClickListener(this)

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

        record_btn_start.isEnabled = true
        when (v.id) {
            R.id.record_btn_list ->
                navController?.navigate(R.id.action_recordFragment_to_audioListFragment)
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

        }

    }

    private fun startRecording() {
        mr = MediaRecorder()
        record_btn_start.isEnabled = false
        record_btn_list.isEnabled = false

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //Get app external directory path
        //name -> path = User + timeStamp + ".mp3"
        val path = requireActivity().getExternalFilesDir("/")!!.toString() + "/" + timeStamp + ".mp3"

        mr.setAudioSource(MediaRecorder.AudioSource.MIC)
        mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mr.setMaxDuration(10000)
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mr.setOutputFile(path)
        mr.prepare()
        mr.start()

        filenametext.text = "\"Recording, File Saved : " + timeStamp + ".mp3";

        timer_chromo_counter.base = SystemClock.elapsedRealtime()
        timer_chromo_counter.start()

        val timer = object : CountDownTimer(11000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                //timer_chromo_counter.text = counter.toString()
                counter++
            }

            override fun onFinish() {
                mr.stop()
                timer_chromo_counter.stop()
                filenametext.text = "Recording Stopped, File Saved : " + timeStamp + ".mp3";
                timer_chromo_counter.text = "Finished"
                record_btn_start.background = resources.getDrawable(
                        R.drawable.record_btn_stopped,
                        null
                )
                record_btn_start.isEnabled = true
                record_btn_list.isEnabled = true
                counter = 0
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