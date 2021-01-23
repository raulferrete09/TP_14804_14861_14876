package com.example.tp_14804_14861_14876.Fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SubmissionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubmissionsFragment : Fragment(), View.OnClickListener, OnItemSelectedListener {

    lateinit var profile_iv_photo: ImageView
    lateinit var profile_tv_name: TextView
    lateinit var profile_tv_id: TextView
    lateinit var report_ed_anomaly: EditText
    lateinit var transformation: Transformation
    lateinit var submission_btn_firebase: Button
    lateinit var submission_spinner_machine: Spinner
    lateinit var submission_spinner_intervation: Spinner
    lateinit var submission_tv_machine: TextView
    lateinit var submission_tv_intervation: TextView
    lateinit var submission_iv_addphoto: ImageView
    lateinit var submission_iv_addaudio: ImageView

    lateinit var mainFragment: MainFragment
    lateinit var transaction: FragmentTransaction

    lateinit var adpater_number: ArrayAdapter<CharSequence>
    lateinit var adpater_intervation: ArrayAdapter<CharSequence>

    var auth : FirebaseAuth? = null



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
        return inflater.inflate(R.layout.fragment_submissions, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SubmissionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubmissionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_tv_id = view.findViewById<TextView>(R.id.profile_tv_id)
        profile_tv_name = view.findViewById<TextView>(R.id.profile_tv_name)
        profile_iv_photo = view.findViewById<ImageView>(R.id.profile_iv_photo)
        submission_spinner_machine = view.findViewById<Spinner>(R.id.submission_spinner_machine)
        submission_spinner_intervation = view.findViewById<Spinner>(R.id.submission_spinner_intervation)
        submission_tv_machine = view.findViewById<TextView>(R.id.submission_tv_machine)
        submission_tv_intervation = view.findViewById<TextView>(R.id.submission_tv_intervation)
        submission_iv_addphoto = view.findViewById<ImageView>(R.id.submission_iv_addphoto)
        submission_iv_addaudio = view.findViewById<ImageView>(R.id.submission_iv_addaudio)

        report_ed_anomaly = view.findViewById<EditText>(R.id.report_ed_anomaly)
        submission_btn_firebase = view.findViewById<Button>(R.id.submission_btn_firebase)

        adpater_number = ArrayAdapter.createFromResource(requireContext(), R.array.numbers, android.R.layout.simple_spinner_item)
        adpater_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        submission_spinner_machine.adapter = adpater_number
        submission_spinner_machine.onItemSelectedListener = this

        adpater_intervation = ArrayAdapter.createFromResource(requireContext(), R.array.type_of_intervation, android.R.layout.simple_spinner_item)
        adpater_intervation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        submission_spinner_intervation.adapter = adpater_intervation
        submission_spinner_intervation.onItemSelectedListener = this


        report_ed_anomaly.setOnClickListener(this)
        submission_btn_firebase.setOnClickListener(this)
        submission_iv_addphoto.setOnClickListener(this)
        submission_iv_addaudio.setOnClickListener(this)

        //Firebase info
        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth?.currentUser
        val name:String? = user?.displayName
        val id:String? = user?.uid
        var photo = user?.photoUrl

        profile_tv_name.text = name
        profile_tv_id.text = id

// rounded corner image
        val radius = 50
        val margin = 5
        transformation = RoundedCornersTransformation(radius, margin)
        Picasso.get().load(photo).transform(transformation).into(profile_iv_photo)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.submission_btn_firebase -> {
                checkInformation()
            }
            R.id.submission_iv_addphoto -> {
                var openGalleryIntent= Intent(Intent.ACTION_PICK)
                openGalleryIntent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory(). absolutePath+ "/DCIM/HVAC/"),"*/*")
                openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                openGalleryIntent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(openGalleryIntent,1000)
            }
        }
    }

    private fun checkInformation() {
        if(submission_spinner_machine.toString().isNotEmpty() && submission_spinner_intervation.toString().isNotEmpty() && report_ed_anomaly.text.isNotEmpty())  {
            mainFragment = MainFragment()
            transaction = fragmentManager?.beginTransaction()!!
            transaction.replace(R.id.drawable_frameLayout, mainFragment)
            transaction.commit()
        }
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val text = parent.getItemAtPosition(position).toString()
        //Toast.makeText(parent.context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            var imageUri = data?.data
            //uploadImage()
        }
    }
}

