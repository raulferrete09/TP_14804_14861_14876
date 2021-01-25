package com.example.tp_14804_14861_14876.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_14804_14861_14876.Activitys.MainActivity
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.ReportListAdapter
import com.example.tp_14804_14861_14876.Utils.TimeAgo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
class MainFragment : Fragment(), View.OnClickListener, ReportListAdapter.onItemList_Click {

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
    lateinit var dashboard_tv_machine: TextView
    lateinit var dashboard_layout: ConstraintLayout
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var OK_m1: Button
    lateinit var NOK_m1: Button
    lateinit var OK_m2: Button
    lateinit var NOK_m2: Button

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
        dashboard_tv_machine = view.findViewById<TextView>(R.id.dasboard_tv_machine)
        dashboard_tv_oknok = view.findViewById<TextView>(R.id.dasboard_tv_oknok)
        dashboard_tv_anomaly = view.findViewById<TextView>(R.id.dasboard_tv_anomaly)
        dashboard_layout = view.findViewById<ConstraintLayout>(R.id.dasboard_layout)


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

        OK_m1 = view.findViewById<Button>(R.id.OK_m1)
        NOK_m1 = view.findViewById<Button>(R.id.NOK_m1)
        OK_m2 = view.findViewById<Button>(R.id.OK_m2)
        NOK_m2 = view.findViewById<Button>(R.id.NOK_m2)

        OK_m1.setOnClickListener(this)
        NOK_m1.setOnClickListener(this)
        OK_m2.setOnClickListener(this)
        NOK_m2.setOnClickListener(this)

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
            R.id.OK_m1 -> {
                val anomaly = ""
                dashboard_tv_anomaly.text = anomaly
                dashboard_tv_oknok.text = "OK"
                dashboard_tv_machine.text = "Machine 1"
                dashboard_layout.setBackgroundColor(resources.getColor(R.color.green))
            }
            R.id.NOK_m1 -> {
                val anomaly = "Temperatura demasioado alta"
                dashboard_tv_anomaly.text = anomaly
                dashboard_tv_oknok.text = "NOK"
                dashboard_tv_machine.text = "Machine 1"
                dashboard_layout.setBackgroundColor(resources.getColor(R.color.red))
            }
            R.id.OK_m2 -> {
                val anomaly = ""
                dashboard_tv_anomaly.text = anomaly
                dashboard_tv_oknok.text = "OK"
                dashboard_tv_machine.text = "Machine 2"
                dashboard_layout.setBackgroundColor(resources.getColor(R.color.green))
            }
            R.id.NOK_m2 -> {
                val anomaly = "Som demasioado alto"
                dashboard_tv_anomaly.text = anomaly
                dashboard_tv_oknok.text = "NOK"
                dashboard_tv_machine.text = "Machine 2"
                dashboard_layout.setBackgroundColor(resources.getColor(R.color.red))
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
}