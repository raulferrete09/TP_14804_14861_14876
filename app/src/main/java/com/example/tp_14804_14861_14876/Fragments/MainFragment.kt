package com.example.tp_14804_14861_14876.Fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_14804_14861_14876.Activitys.MainActivity
import com.example.tp_14804_14861_14876.Activitys.uploadPDF
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.ReportsPDF
import com.example.tp_14804_14861_14876.Utils.TimeAgo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.io.File
import java.text.SimpleDateFormat
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

    lateinit var myPDFListView: ListView
    var databaseReference: DatabaseReference? = null
    var reportsPDF: ArrayList<ReportsPDF>? = null


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
        dashboard_spinner_machine = view.findViewById<Spinner>(R.id.dasboard_spinner_machine)
        dashboard_tv_oknok = view.findViewById<TextView>(R.id.dasboard_tv_oknok)
        dashboard_tv_anomaly = view.findViewById<TextView>(R.id.dasboard_tv_anomaly)
        dashboard_layout = view.findViewById<ConstraintLayout>(R.id.dasboard_layout)

        myPDFListView = view.findViewById<ListView>(R.id.myListView)
        reportsPDF = ArrayList()
        viewAllFiles()

        adpater_number = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.numbers,
            android.R.layout.simple_spinner_item
        )

        adpater_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dashboard_spinner_machine.adapter = adpater_number
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
                        val mytext = view.findViewById<View>(android.R.id.text1) as TextView
                        mytext.setTextColor(Color.BLACK)
                        return super.getView(position, convertView, parent)
                    }
                }
                myPDFListView!!.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}