package com.example.tp_14804_14861_14876.Fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_14804_14861_14876.Activitys.MainActivity
import com.example.tp_14804_14861_14876.Activitys.TOPIC
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

const val TOPIC = "/topics/myTopic"
var anomalyPast:String? = null
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(), View.OnClickListener, ReportListAdapter.onItemList_Click,
    AdapterView.OnItemSelectedListener {

    val TAG = "MainFragment"
    var navController: NavController? = null

    lateinit var reportlist: RecyclerView

    //var allFiles: Array<File>? = null
    private lateinit var allFilesReport: Array<File>
    lateinit var timeAgo: TimeAgo
    private var reportListAdapter: ReportListAdapter? = null

    lateinit var add_btn_submission: FloatingActionButton
    lateinit var submissionsFragment: SubmissionsFragment
    lateinit var transaction: FragmentTransaction
    lateinit var dashboadinformation: Layout
    lateinit var dashboard_tv_oknok: TextView
    lateinit var dashboard_tv_anomaly: TextView
    lateinit var dashboard_spinner_machine: Spinner
    lateinit var dashboard_layout: ConstraintLayout
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var adpater_number: ArrayAdapter<CharSequence>
    private lateinit var database: FirebaseDatabase
    var status_temperature: Any? = null
    var status_accelerometer: Any? = null
    var status_audio: Any? = null
    var auth: FirebaseAuth? = null



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).supportActionBar!!.hide()

        add_btn_submission = view.findViewById<FloatingActionButton>(R.id.add_btn_submission)
        dashboard_spinner_machine = view.findViewById<Spinner>(R.id.dashboard_spinner_machine)
        dashboard_tv_oknok = view.findViewById<TextView>(R.id.dasboard_tv_oknok)
        dashboard_tv_anomaly = view.findViewById<TextView>(R.id.dasboard_tv_anomaly)
        dashboard_layout = view.findViewById<ConstraintLayout>(R.id.dasboard_layout)


        adpater_number = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.numbers,
            android.R.layout.simple_spinner_item
        )

        adpater_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dashboard_spinner_machine.adapter = adpater_number
        dashboard_spinner_machine.onItemSelectedListener = this

        reportlist = view.findViewById<RecyclerView>(R.id.report_list_view)

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val path = Environment.getExternalStorageDirectory().toString() + "/HVAC/Reports/"
        val directory = File(path)
        allFilesReport = directory.listFiles()

        reportListAdapter = ReportListAdapter(allFilesReport, this)

        reportlist.setHasFixedSize(true)
        reportlist.layoutManager = LinearLayoutManager(context)
        reportlist.adapter = reportListAdapter

        add_btn_submission.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_btn_submission -> {
                submissionsFragment = SubmissionsFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, submissionsFragment, null)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }

    override fun onClickListener(file: File, position: Int) {

        //For pdf file
        val file = file
        //For pdf file
        val path = Environment.getExternalStorageDirectory().toString() + "/HVAC/Reports/"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.type = "application/*"
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val text = parent.getItemAtPosition(position).toString()
        CheckData()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun CheckData() {
        val MachineNumber = dashboard_spinner_machine.selectedItem.toString()
        println(MachineNumber)
        // Verification Temperature
        database = FirebaseDatabase.getInstance()
        database.reference.child("Temperature")
            .child("M" + "$MachineNumber")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        var map = snapshot.value as Map<String, Any?>
                        status_temperature = map["status"]
                        updateData()
                    }
                }

            })
        database = FirebaseDatabase.getInstance()
        database.reference.child("Accelerometer")
            .child("M" + "$MachineNumber")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        var map = snapshot.value as Map<String, Any?>
                        status_accelerometer = map["status"]
                        updateData()
                    }
                }

            })
        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth?.currentUser
        var uid = user?.uid

        database = FirebaseDatabase.getInstance()
        database.reference.child("Audio")
            .child("M" + "$MachineNumber")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        var map = snapshot.value as Map<String, Any?>
                        status_audio = map["anomaly"]
                        updateData()
                    }
                }

            })


    }

    private fun updateData() {
        println(status_accelerometer)
        println(status_temperature)
        println(status_audio)
        if (status_accelerometer == "OK" && status_temperature == "OK" && status_audio == "") {
            dashboard_tv_oknok.text = "OK"
            dashboard_tv_anomaly.text = ""
            dashboard_layout.setBackgroundColor(resources.getColor(R.color.green))
        } else {
            val anomaly = typeAnomaly()

            dashboard_tv_oknok.text = "ANOMALY"
            dashboard_layout.setBackgroundColor(resources.getColor(R.color.red))

            val MachineNumber = dashboard_spinner_machine.selectedItem.toString()
            if(anomalyPast != anomaly) {
                val title = "ANOMALY"
                val message = "Machine: " + MachineNumber + " - " + anomaly

                PushNotification(
                    NotificationData(title, message),
                    TOPIC
                ).also {
                    sendNotification(it)
                }
                anomalyPast = anomaly
            }



        }
    }

    private fun typeAnomaly():String {
        if(status_accelerometer != "OK" && status_temperature != "OK" && status_audio != ""){
            dashboard_tv_anomaly.text = "Accelerometer, Temperature and Audio"
        }else if(status_temperature != "OK" && status_audio != ""){
            dashboard_tv_anomaly.text = "Temperature and Audio"
        }else if(status_accelerometer != "OK" && status_audio != ""){
            dashboard_tv_anomaly.text = "Accelerometer and Audio"
        }else if(status_accelerometer != "OK" && status_temperature != "OK"){
            dashboard_tv_anomaly.text = "Accelerometer and Temperature"
        }else if(status_accelerometer != "OK") {
            dashboard_tv_anomaly.text = "Accelerometer"
        }else if(status_temperature != "OK"){
            dashboard_tv_anomaly.text = "Temperature"
        }else if (status_audio != ""){
            dashboard_tv_anomaly.text = "Audio"
        }else{
            // Do nothing
        }

        return dashboard_tv_anomaly.toString()


    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            }
            else {
                Log.e(TAG, response.errorBody().toString())
            }
        }catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

}