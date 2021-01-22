package com.example.tp_14804_14861_14876.Fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(), View.OnClickListener {

    lateinit var profile_iv_photo: ImageView
    lateinit var profile_et_name: EditText
    lateinit var settings_btn_change_profile: Button

    lateinit var mainFragment: MainFragment
    lateinit var settingsFragment: SettingsFragment
    lateinit var temperatureFragment: TemperatureFragment
    lateinit var transaction: FragmentTransaction

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_et_name = view.findViewById<EditText>(R.id.profile_et_name)
        profile_iv_photo = view.findViewById<ImageView>(R.id.profile_iv_photo)
        settings_btn_change_profile = view.findViewById<Button>(R.id.settings_btn_change_profile)


        profile_et_name.setOnClickListener(this)
        profile_iv_photo.setOnClickListener(this)
        settings_btn_change_profile.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile_et_name -> {
                temperatureFragment = TemperatureFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, temperatureFragment)
                transaction.commit()
            }
            R.id.profile_iv_photo ->{
                settingsFragment = SettingsFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, settingsFragment)
                transaction.commit()
            }
            R.id.settings_btn_change_profile -> {
                //updateProfile()
                mainFragment = MainFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, mainFragment)
                transaction.commit()
            }

        }
    }

    private fun updateProfile() {
        //auth.currentUser?.let {
          //  val username =profile_et_name.text.toString()
            //val photoURI = Uri.parse("android.resource://$packageName/${R.drawable.logo_black_square}")
    }
}
