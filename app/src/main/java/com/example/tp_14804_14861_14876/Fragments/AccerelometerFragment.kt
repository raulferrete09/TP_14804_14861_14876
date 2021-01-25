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

    lateinit var accelerometer_iv_icon: ImageView
    lateinit var accelerometer_tv_anomaly: TextView
    lateinit var accelerometer_progress_bar: ProgressBar
    lateinit var accelerometer_tv_x: TextView
    lateinit var accelerometer_tv_y: TextView
    lateinit var accelerometer_tv_z: TextView
    lateinit var accelerometer_spinner_machine: Spinner
    lateinit var adpater_number: ArrayAdapter<CharSequence>

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
        return inflater.inflate(R.layout.fragment_accerelometer, container, false)
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

        adpater_number = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.numbers,
            android.R.layout.simple_spinner_item
        )

        adpater_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accelerometer_spinner_machine.adapter = adpater_number
        accelerometer_spinner_machine.onItemSelectedListener = this

        // get text firebase
        val accelerometer_x = 1.91
        val accelerometer_y = 9.94
        val accelerometer_z = 5.35
        accelerometer_tv_anomaly.text = "2"
        if (accelerometer_tv_anomaly.text == "1") {
            accelerometer_tv_anomaly.text = "OK"
            accelerometer_tv_anomaly.setTextColor(Color.argb(255,44,174,49))
            accelerometer_progress_bar.indeterminateDrawable.setColorFilter(Color.argb(255,44,174,49), PorterDuff.Mode.SRC_IN)
            accelerometer_tv_x.text = "x = " + accelerometer_x.toString() + " g"
            accelerometer_tv_y.text = "y = " + accelerometer_y.toString() + " g"
            accelerometer_tv_z.text = "x = " + accelerometer_z.toString() + " g"


        } else {
            accelerometer_tv_anomaly.text = "Anomaly"
            accelerometer_tv_anomaly.setTextColor(Color.argb(255,234,16,67))
            accelerometer_progress_bar.indeterminateDrawable.setColorFilter(Color.argb(255,234,16,67), PorterDuff.Mode.SRC_IN)
            accelerometer_tv_x.text = "x = " + accelerometer_x.toString() + " g"
            accelerometer_tv_y.text = "y = " + accelerometer_y.toString() + " g"
            accelerometer_tv_z.text = "z = " + accelerometer_z.toString() + " g"
        }

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val text = parent.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}