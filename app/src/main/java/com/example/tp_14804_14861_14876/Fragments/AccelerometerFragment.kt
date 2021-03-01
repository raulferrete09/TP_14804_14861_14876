package com.example.tp_14804_14861_14876.Fragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.ReportsPDF
import com.google.firebase.database.*
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccerelometerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccerelometerFragment : Fragment(), AdapterView.OnItemSelectedListener {

    // Inicialization of the Project variables
    lateinit var accelerometer_iv_icon: ImageView
    lateinit var accelerometer_tv_anomaly: TextView
    lateinit var accelerometer_progress_bar: ProgressBar
    lateinit var accelerometer_tv_x: TextView
    lateinit var accelerometer_tv_y: TextView
    lateinit var accelerometer_tv_z: TextView
    lateinit var accelerometer_spinner_machine: Spinner
    lateinit var machines: ArrayList<String>
    var accelerometer_x: Any? = null
    var accelerometer_y: Any? = null
    var accelerometer_z: Any? = null
    var accelerometer_status: Any? = null
    private lateinit var database: FirebaseDatabase
    var databaseReference: DatabaseReference? = null


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
        return inflater.inflate(R.layout.fragment_accelerometer, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccerelometerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccerelometerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accelerometer_iv_icon = view.findViewById<ImageView>(R.id.accelerometer_iv_icon)
        accelerometer_tv_anomaly = view.findViewById<TextView>(R.id.accelerometer_tv_anomaly)
        accelerometer_progress_bar = view.findViewById<ProgressBar>(R.id.accelerometer_progress_bar)
        accelerometer_tv_x = view.findViewById<TextView>(R.id.accelerometer_tv_x)
        accelerometer_tv_y = view.findViewById<TextView>(R.id.accelerometer_tv_y)
        accelerometer_tv_z = view.findViewById<TextView>(R.id.accelerometer_tv_z)
        accelerometer_spinner_machine = view.findViewById<Spinner>(R.id.accelerometer_spinner_machine)

        accelerometer_x = ""
        accelerometer_y = ""
        accelerometer_z = ""
        accelerometer_status = ""

        machines = arrayListOf<String>()
        getMachines()
        accelerometer_spinner_machine.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1,machines)
        accelerometer_spinner_machine.onItemSelectedListener = this

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val text = parent.getItemAtPosition(position).toString()
        updateData()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun updateData(){
        val MachineNumber = accelerometer_spinner_machine.selectedItem.toString()
        println(MachineNumber)
        database = FirebaseDatabase.getInstance()
        database.reference.child("Accelerometer")
                .child("$MachineNumber")
                .addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.value != null) {
                            var map = snapshot.value as Map<String, Any?>
                            accelerometer_x = map["x"]
                            accelerometer_y = map["y"]
                            accelerometer_z = map["z"]
                            accelerometer_status = map["status"]

                            //Define the attributes of the layout so that the user is able to distinguish if an anomaly occurs
                            if (accelerometer_status == "OK") {
                                accelerometer_tv_anomaly.text = "OK"
                                accelerometer_tv_anomaly.setTextColor(Color.argb(255,44,174,49))
                                accelerometer_progress_bar.indeterminateDrawable.setColorFilter(Color.argb(255,44,174,49), PorterDuff.Mode.SRC_IN)
                                accelerometer_tv_x.text = "x = " + accelerometer_x.toString()
                                accelerometer_tv_y.text = "y = " + accelerometer_y.toString()
                                accelerometer_tv_z.text = "z = " + accelerometer_z.toString()


                            } else {
                                accelerometer_tv_anomaly.text = "Anomaly"
                                accelerometer_tv_anomaly.setTextColor(Color.argb(255,234,16,67))
                                accelerometer_progress_bar.indeterminateDrawable.setColorFilter(Color.argb(255,234,16,67), PorterDuff.Mode.SRC_IN)
                                accelerometer_tv_x.text = "x = " + accelerometer_x.toString()
                                accelerometer_tv_y.text = "y = " + accelerometer_y.toString()
                                accelerometer_tv_z.text = "z = " + accelerometer_z.toString()
                            }
                        }
                    }

                })
    }

    private fun getMachines() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Machines")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val name = postSnapshot.key
                    machines.add(name.toString())
                    accelerometer_spinner_machine.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1,machines)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
