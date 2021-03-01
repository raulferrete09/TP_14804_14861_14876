package com.example.tp_14804_14861_14876.Fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
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
 * Use the [RestrictedAreaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestrictedAreaFragment : Fragment(), View.OnClickListener {

    lateinit var settingsMachineFragment: SettingsMachineFragment
    lateinit var transaction: FragmentTransaction

    lateinit var restrictedArea_et_user: EditText
    lateinit var restrictedArea_et_password: EditText
    lateinit var restrictedArea_btn_login: Button
    lateinit var restrictedArea_iv_passwordshow: ImageView
    var misshowpass = false


    var auth : FirebaseAuth? = null
    var idUser: String? = null

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
        return inflater.inflate(R.layout.fragment_restricted_area, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RestrictedAreaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RestrictedAreaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restrictedArea_et_user = view.findViewById<EditText>(R.id.restrictedArea_et_user)
        restrictedArea_et_password = view.findViewById<EditText>(R.id.restrictedArea_et_password)
        restrictedArea_btn_login = view.findViewById<Button>(R.id.restrictedArea_btn_login)
        restrictedArea_iv_passwordshow = view.findViewById<ImageView>(R.id.restrictedArea_iv_passwordshow)

        restrictedArea_et_user.setOnClickListener(this)
        restrictedArea_et_password.setOnClickListener(this)
        restrictedArea_btn_login.setOnClickListener(this)
        restrictedArea_iv_passwordshow.setOnClickListener(this)

        //Firebase info
        auth = FirebaseAuth.getInstance()
        val user = auth?.currentUser
        val name = user?.displayName
        val email = user?.email
        idUser = user?.uid
        var photo = user?.photoUrl
        val superUser = user?.email
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.restrictedArea_btn_login -> {
                getSuperUsers()
            }
            R.id.restrictedArea_iv_passwordshow -> {
                misshowpass = !misshowpass
                showPassword(misshowpass)
            }
        }
    }

    private fun showPassword(isShow: Boolean) {
        if (isShow){
            restrictedArea_et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            restrictedArea_iv_passwordshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            restrictedArea_et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            restrictedArea_iv_passwordshow.setImageResource(R.drawable.ic_show_password)
        }
        restrictedArea_et_password.setSelection(restrictedArea_et_password.text.toString().length)
    }

    private fun getSuperUsers(){
        var databaseReference = FirebaseDatabase.getInstance().getReference("SuperUsers")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val id = postSnapshot.key
                    if(id == idUser){
                        var database = FirebaseDatabase.getInstance()
                        database.reference.child("SuperUsers")
                            .child("$idUser")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.value != null) {
                                        var map = snapshot.value as Map<String, Any?>
                                        var name = map["username"]
                                        var password = map["password"]
                                        if( restrictedArea_et_user.text.toString() == name && restrictedArea_et_password.text.toString() == password) {
                                            settingsMachineFragment = SettingsMachineFragment()
                                            transaction = fragmentManager?.beginTransaction()!!
                                            transaction.replace(R.id.drawable_frameLayout, settingsMachineFragment)
                                            transaction.commit()
                                        }
                                    }
                                }
                            })
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}