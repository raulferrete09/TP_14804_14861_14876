package com.example.tp_14804_14861_14876.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment(), View.OnClickListener {

    lateinit var mainFragment: MainFragment
    lateinit var profileFragment: ProfileFragment
    lateinit var passwordFragment: PasswordFragment
    lateinit var transaction: FragmentTransaction
    lateinit var transformation: Transformation


    lateinit var change_tv_profile: TextView
    lateinit var change_tv_password: TextView
    lateinit var forget_tv_password: TextView

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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SettingsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        change_tv_profile = view.findViewById<TextView>(R.id.change_tv_profile)
        change_tv_password = view.findViewById<TextView>(R.id.change_tv_password)
        forget_tv_password = view.findViewById<TextView>(R.id.forget_tv_password)

        change_tv_profile.setOnClickListener(this)
        change_tv_password.setOnClickListener(this)
        forget_tv_password.setOnClickListener(this)

        //Firebase info
        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth?.currentUser
        val name:String? = user?.displayName
        val id:String? = user?.uid
        val email:String? = user?.email
        var photo = user?.photoUrl

// rounded corner image
        val radius = 50
        val margin = 5
        transformation = RoundedCornersTransformation(radius, margin)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.change_tv_profile -> {
                profileFragment = ProfileFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, profileFragment)
                transaction.commit()
            }
            R.id.change_tv_password -> {
                passwordFragment = PasswordFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, passwordFragment)
                transaction.commit()
            }
            R.id.forget_tv_password -> {
                resetPassword()
                mainFragment = MainFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, mainFragment)
                transaction.commit()
            }
        }
    }

    private fun changePhoto() {
        TODO("Not yet implemented")
    }
    /*
    Function to reset Password, which send a email to the user email
     */
    private fun resetPassword() {
        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth?.currentUser
        val email:String? = user?.email

        auth?.sendPasswordResetEmail(email!!)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity,"Check your email account", Toast.LENGTH_LONG).show()
                    }
                }
    }
}