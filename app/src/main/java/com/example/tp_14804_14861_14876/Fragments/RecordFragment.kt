package com.example.tp_14804_14861_14876.Fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import com.example.tp_14804_14861_14876.Activitys.MainActivity
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
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
class RecordFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    // Inicialization of the Project variables

    var isRecording = true
    var counter = 0
    var progresscounter = 0
    var auth : FirebaseAuth? = null
    lateinit var audioBase64: String
    var databaseReference: DatabaseReference? = null
    lateinit var machines: ArrayList<String>

    lateinit var mr: MediaRecorder
    lateinit var record_btn_list: Button
    lateinit var record_btn_start: Button
    lateinit var timer_chromo_counter: Chronometer
    lateinit var filenametext: TextView
    lateinit var progress_bar: ProgressBar
    lateinit var record_spinner_machine: Spinner

    lateinit var intent:Intent
    lateinit var audioListFragment: AudioListFragment
    lateinit var transaction: FragmentTransaction

    lateinit var bytes: ByteArray
    lateinit var base64: String
    lateinit var text_spinner_machine: String

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var fileref: StorageReference
    private var storageReference: StorageReference? = null

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

        record_btn_list = view.findViewById<Button>(R.id.record_btn_list)
        record_btn_start = view.findViewById<Button>(R.id.record_btn_start)
        record_spinner_machine = view.findViewById<Spinner>(R.id.record_spinner_machine)
        timer_chromo_counter = view.findViewById<Chronometer>(R.id.timer_chromo_counter)
        filenametext = view.findViewById<TextView>(R.id.info_tv)
        progress_bar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progress_bar.max = 10

        auth= FirebaseAuth.getInstance()
        var uid = auth?.currentUser?.uid
        storageReference = FirebaseStorage.getInstance().reference.child("Sound").child("$uid")


        record_btn_list.setOnClickListener(this)
        record_btn_start.setOnClickListener(this)

        progress_bar.visibility = View.INVISIBLE

        machines = arrayListOf<String>("")
        getMachines()
        record_spinner_machine.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1,machines)
        record_spinner_machine.onItemSelectedListener = this

        showAlert()

    }

    override fun onClick(v: View) {

        intent = Intent(activity, MainActivity::class.java)
        record_btn_start.isEnabled = true
        when (v.id) {
            R.id.record_btn_list -> {
                if (checkPermissions()) {
                    val folder =
                        File(
                            Environment.getExternalStorageDirectory()
                                .toString() + File.separator + "HVAC" + File.separator + "Audios"
                        )
                    if (!folder.exists()) {
                        folder.mkdirs()
                        audioListFragment = AudioListFragment()
                        transaction = fragmentManager?.beginTransaction()!!
                        transaction.replace(R.id.drawable_frameLayout, audioListFragment)
                        transaction.commit()
                    } else {
                        audioListFragment = AudioListFragment()
                        transaction = fragmentManager?.beginTransaction()!!
                        transaction.replace(R.id.drawable_frameLayout, audioListFragment)
                        transaction.commit()
                    }
                }
            }
            R.id.record_btn_start -> {
                if (!isRecording) {
                    //Start record
                    stopRecording()
                    record_btn_start.background = resources.getDrawable(
                        R.drawable.record_btn_recording,
                        null
                    )
                    record_btn_start.isEnabled = false
                    isRecording = false
                } else {
                    if (checkPermissions()) {
                        val folder =
                            File(
                                Environment.getExternalStorageDirectory()
                                    .toString() + File.separator + "HVAC" + File.separator + "Audios"
                            )
                        if (!folder.exists()) {
                            folder.mkdirs()
                            //Start record
                            sendRecordingSignal()
                            startRecording()
                            record_btn_start.background = resources.getDrawable(
                                R.drawable.record_btn_recording,
                                null
                            )
                            isRecording = true
                        } else {
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
            }
        }

    }

    private fun sendRecordingSignal() {
        var maps = mutableMapOf<String,Any?>()
        maps["Record"] = "True"
        var refdatabase = FirebaseDatabase.getInstance()
        refdatabase.reference
            .child("Dashboard")
            .child("${addExpenses_et_nameMachine.text.toString()}")
            .child("Audio")
            .updateChildren(maps)
    }


    private fun startRecording() {

        //Firebase info
        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth?.currentUser
        val name:String? = user?.displayName

        mr = MediaRecorder()
        record_btn_start.isEnabled = false
        record_btn_list.isEnabled = false
        progress_bar.visibility = View.VISIBLE
        text_spinner_machine= record_spinner_machine.selectedItem.toString()
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //Get app external directory path
        //name -> path = User + timeStamp + ".mp3"
        //val pathname = "Audio.mp3"
        val audioname = name + "_" + text_spinner_machine + "_" + timeStamp
        val pathname = name + "_" + text_spinner_machine + "_" + timeStamp + ".mp3"
        val path = Environment.getExternalStorageDirectory().toString() + "/HVAC/Audios/" + pathname

        // Here are the code to convert the audio to base 64
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
                progress_bar.progress = progresscounter
                progresscounter++
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                mr.stop()
                timer_chromo_counter.stop()
                filenametext.text = "Recording Stopped, File Saved : " + pathname;
                timer_chromo_counter.text = "Finished"
                record_btn_start.isEnabled = true
                record_btn_list.isEnabled = true
                progress_bar.visibility = View.INVISIBLE

                counter = 0
                progresscounter = 0
                //encodeAudio(path)

                // Change button image and set Recording state to false
                record_btn_start.setBackgroundResource(R.drawable.record_btn_stopped)

                //record_btn_start.background = resources.getDrawable(R.drawable.record_btn_stopped, null)
                var base64 = encodeAudio(path)
                sendData(base64, audioname,path)
                resetPing(base64)
            }
        }
        timer.start()

    }

    private fun resetPing(base64: String) {
        var maps = mutableMapOf<String,Any?>()
        maps["record"] = "False"
        maps["base64"] = base64
        var refdatabase = FirebaseDatabase.getInstance()
        refdatabase.reference
            .child("Dashboard")
            .child("${addExpenses_et_nameMachine.text.toString()}")
            .child("Audio")
            .updateChildren(maps)
    }

    private fun stopRecording() {
        //Stop Timer, very obvious
        //Change text on page to file saved
        //filenametext.text = "Recording Stopped, File Saved : " + path
        //Stop media recorder and set it to null for further use to record new audio
        mr.stop()
        mr.release()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodeAudio(path: String): String {
        val audioBytes: ByteArray
        //println(path)
        try {

            // Just to check file size.. Its is correct i-e; Not Zero
            val audioFile = File(path)
            val fileSize = audioFile.length()
            val baos = ByteArrayOutputStream()
            val fis = FileInputStream(File(path))
            val buf = ByteArray(2048)
            var n: Int
            while (-1 != fis.read(buf).also { n = it }) baos.write(buf, 0, n)
            audioBytes = baos.toByteArray()

            // Here goes the Base64 string
            audioBase64 = Base64.getEncoder().encodeToString(audioBytes)
            println(audioBase64)
        } catch (e: Exception) {
            //DiagnosticHelper.writeException(e)
        }
        return audioBase64
    }


    private fun checkPermissions(): Boolean {
        //Check permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //Permission Granted
            return true
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                111
            )
            return false
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Warning")
        builder.setMessage("Please select the machine.")
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val text = parent.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


    /*
        The function sendData() we send the status of the audio and the base64 to database.
     */
    private fun sendData(base64: String, audioname: String,path: String){
        val person = auth?.currentUser
        val uid = person?.uid
        var map = mutableMapOf<String,Any>()
        var map1 = mutableMapOf<String,Any>()
        var database = FirebaseDatabase.getInstance()
        map["base64"] = base64
        map["status"] = ""
        database.reference
                .child("Audio")
                .child("$text_spinner_machine")
                .child("$uid")
                .child("$audioname")
                .updateChildren(map)
        var file = Uri.fromFile(File(path))
        fileref = storageReference!!.child("$path"+".mp3")
        fileref.putFile(file)

        map1["anomaly"] = ""
        database.reference
                .child("Audio")
                .child("$text_spinner_machine")
                .updateChildren(map1)
    }

    private fun getMachines() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Machines")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                machines.removeAt(0)
                for (postSnapshot in snapshot.children) {
                    val name = postSnapshot.key
                    machines.add(name.toString())
                    record_spinner_machine.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1,machines)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}