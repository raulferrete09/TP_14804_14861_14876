package com.example.tp_14804_14861_14876.Fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat.setBackgroundTintList
import com.example.tp_14804_14861_14876.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TemperatureFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TemperatureFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var temperature_iv_icon: ImageView
    lateinit var temperature_tv_anomaly: TextView
    lateinit var temperature_progress_bar: ProgressBar
    lateinit var temperature_tv_temperature: TextView
    lateinit var temperature_spinner_machine: Spinner
    lateinit var adpater_number: ArrayAdapter<CharSequence>
    var status: Any? = null
    var temperature: Any? = null
    private lateinit var database: FirebaseDatabase

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
        return inflater.inflate(R.layout.fragment_temperature, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TemperatureFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TemperatureFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        temperature_iv_icon = view.findViewById<ImageView>(R.id.temperature_iv_icon)
        temperature_tv_anomaly = view.findViewById<TextView>(R.id.temperature_tv_anomaly)
        temperature_progress_bar = view.findViewById<ProgressBar>(R.id.temperature_progress_bar)
        temperature_tv_temperature = view.findViewById<TextView>(R.id.temperature_tv_temperature)
        temperature_spinner_machine = view.findViewById<Spinner>(R.id.temperature_spinner_machine)

        adpater_number = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.numbers,
            android.R.layout.simple_spinner_item
        )

        adpater_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        temperature_spinner_machine.adapter = adpater_number
        temperature_spinner_machine.onItemSelectedListener = this

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val text = parent.getItemAtPosition(position).toString()
        updateData()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun updateData(){
        val MachineNumber = temperature_spinner_machine.selectedItem.toString()
        println(MachineNumber)
        database = FirebaseDatabase.getInstance()
        database.reference.child("Temperature")
                .child("M"+"$MachineNumber")
                .addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.value != null) {
                            var map = snapshot.value as Map<String, Any?>
                            status = map["status"]
                            temperature = map["Temperature"]

                            // get text firebase
                            if (status == "OK") {
                                temperature_tv_anomaly.text = "OK"
                                temperature_tv_anomaly.setTextColor(Color.argb(255,44,174,49))
                                temperature_progress_bar.indeterminateDrawable.setColorFilter(Color.argb(255,44,174,49), PorterDuff.Mode.SRC_IN)
                                temperature_tv_temperature.text = "x = " + temperature.toString()



                            } else {
                                temperature_tv_anomaly.text = "Anomaly"
                                temperature_tv_anomaly.setTextColor(Color.argb(255,234,16,67))
                                temperature_progress_bar.indeterminateDrawable.setColorFilter(Color.argb(255,234,16,67), PorterDuff.Mode.SRC_IN)
                                temperature_tv_temperature.text = "x = " + temperature.toString()

                            }
                        }
                    }

                })
    }


}