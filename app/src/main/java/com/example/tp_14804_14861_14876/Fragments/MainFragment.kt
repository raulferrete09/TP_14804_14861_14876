package com.example.tp_14804_14861_14876.Fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.Activitys.MainActivity
import com.example.tp_14804_14861_14876.Notification.TOPIC
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.ReportsPDF
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    // Inicialization of the Project variables

    lateinit var add_btn_submission: FloatingActionButton
    lateinit var submissionsFragment: SubmissionsFragment
    lateinit var transaction: FragmentTransaction
    lateinit var dashboard_tv_oknok: TextView
    lateinit var dashboard_tv_anomaly: TextView
    lateinit var dashboard_spinner_machine: Spinner
    lateinit var dashboard_layout: ConstraintLayout
    lateinit var adpater_number: ArrayAdapter<CharSequence>
    lateinit var machine: String
    private lateinit var database: FirebaseDatabase
    var machinePast = ""
    var status_temperature: Any? = null
    var status_accelerometer: Any? = null
    var status_audio: Any? = null
    var auth: FirebaseAuth? = null
    var anomalyPast:String? = null
    var MachineNumberPast:String? = null


    lateinit var myPDFListView: ListView
    var databaseReference: DatabaseReference? = null
    var reportsPDF: ArrayList<ReportsPDF>? = null
    lateinit var machines: ArrayList<String>


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

        (requireActivity() as MainActivity).supportActionBar!!.hide()

        add_btn_submission = view.findViewById<FloatingActionButton>(R.id.add_btn_submission)
        dashboard_spinner_machine = view.findViewById<Spinner>(R.id.dashboard_spinner_machine)
        dashboard_tv_oknok = view.findViewById<TextView>(R.id.dasboard_tv_oknok)
        dashboard_tv_anomaly = view.findViewById<TextView>(R.id.dasboard_tv_anomaly)
        dashboard_layout = view.findViewById<ConstraintLayout>(R.id.dasboard_layout)

        myPDFListView = view.findViewById<ListView>(R.id.myListView)
        reportsPDF = ArrayList()
        viewAllFiles()

        machines = arrayListOf<String>("")
        getMachines()
        dashboard_spinner_machine.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1,machines)
        dashboard_spinner_machine.onItemSelectedListener = this


        add_btn_submission.setOnClickListener(this)

        myPDFListView.setOnItemClickListener { parent, view, position, id ->
            val uploadPDF = reportsPDF!![position]
            val intent = Intent()
            intent.type = Intent.ACTION_VIEW
            intent.data = Uri.parse(uploadPDF.getUrl())
            startActivity(intent)
        }

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

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val text = parent.getItemAtPosition(position).toString()
        machine = dashboard_spinner_machine.selectedItem.toString()
        CheckData()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun viewAllFiles() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Reports")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reportsPDF = ArrayList()
                for (postSnapshot in snapshot.children) {
                    val ReportsPDF = postSnapshot.getValue(ReportsPDF::class.java)
                    reportsPDF!!.add(ReportsPDF!!)
                }
                val uploads = arrayOfNulls<String>(reportsPDF!!.size)
                for (i in uploads.indices) {
                    uploads[i] = reportsPDF!![i]!!.getName()
                }
                val adapter: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
                    requireActivity(), android.R.layout.simple_list_item_1, uploads
                ) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getView(position, convertView, parent)
                        val reportext = view.findViewById<View>(android.R.id.text1) as TextView
                        reportext.setTextColor(Color.BLACK)
                        return super.getView(position, convertView, parent)
                    }
                }
                myPDFListView!!.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /*
    On the fun CheckData()
     */
    private fun CheckData() {
        val MachineNumber = dashboard_spinner_machine.selectedItem.toString()
        // Temperature Verification
        database = FirebaseDatabase.getInstance()
        database.reference.child("Temperature")
                .child("$MachineNumber")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null) {
                            var map = snapshot.value as Map<String, Any?>
                            status_temperature = map["status"]
                            if (MachineNumber == machine){
                                updateData()
                            }
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
                            if (MachineNumber == machine){
                                updateData()
                            }
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
                            status_audio = map["status"]
                            if (MachineNumber == machine){
                                updateData()
                            }
                        }
                    }
                })
    }
    /*
      The function shows the user if exist or not an anomaly
   */

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
            dashboard_layout.setBackgroundResource(R.color.red)
            val MachineNumber = dashboard_spinner_machine.selectedItem.toString()
            if(anomalyPast != anomaly || MachineNumberPast != MachineNumber) {
                anomalyPast = anomaly
                MachineNumberPast = MachineNumber
            }
        }
    }
    /*
        The Function to detect what kind of anomaly the system have, which can have three different
        anomalys
    */
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
        return dashboard_tv_anomaly.text.toString()
    }

    private fun getMachines() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Machines")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                machines.removeAt(0)
                for (postSnapshot in snapshot.children) {
                    val name = postSnapshot.key
                    machines.add(name.toString())
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}