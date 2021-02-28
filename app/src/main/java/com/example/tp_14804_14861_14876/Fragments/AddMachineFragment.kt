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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddMachineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddMachineFragment : Fragment(), View.OnClickListener {

    lateinit var restrictedAreaFragment: RestrictedAreaFragment
    lateinit var settingsMachineFragment: SettingsMachineFragment
    lateinit var transaction: FragmentTransaction

    lateinit var addMachine_et_user: EditText
    lateinit var addMachine_et_password: EditText
    lateinit var addMachine_et_confirmpassword: EditText
    lateinit var addMachine_btn_create: Button
    lateinit var addMachine_iv_passwordshow: ImageView
    lateinit var addMachine_iv_confirmpasswordshow: ImageView
    var misshowpass = false
    var misshowconfirmpass = false


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
        return inflater.inflate(R.layout.fragment_add_machine, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddMachineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddMachineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMachine_et_user = view.findViewById<EditText>(R.id.addMachine_et_username)
        addMachine_et_password = view.findViewById<EditText>(R.id.addMachine_et_password)
        addMachine_et_confirmpassword = view.findViewById<EditText>(R.id.addMachine_et_confirmpassword)
        addMachine_btn_create = view.findViewById<Button>(R.id.addMachine_btn_create)
        addMachine_iv_passwordshow = view.findViewById<ImageView>(R.id.addMachine_iv_passwordshow)
        addMachine_iv_confirmpasswordshow = view.findViewById<ImageView>(R.id.addMachine_iv_confirmpasswordshow)

        addMachine_et_user.setOnClickListener(this)
        addMachine_et_password.setOnClickListener(this)
        addMachine_iv_confirmpasswordshow.setOnClickListener(this)
        addMachine_btn_create.setOnClickListener(this)
        addMachine_iv_passwordshow.setOnClickListener(this)

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
            R.id.addMachine_btn_create -> {
                    settingsMachineFragment = SettingsMachineFragment()
                    transaction = fragmentManager?.beginTransaction()!!
                    transaction.replace(R.id.drawable_frameLayout, settingsMachineFragment)
                    transaction.commit()
//                if(idUser == "nymjU3VGQLck4rWW41J2x83A2CJ3") {
//                    restrictedAreaFragment = RestrictedAreaFragment()
//                    transaction = fragmentManager?.beginTransaction()!!
//                    transaction.replace(R.id.drawable_frameLayout, restrictedAreaFragment)
//                    transaction.commit()
//                } else {
//                    println("nao tem permissão")
//                }
          }
            R.id.addMachine_iv_passwordshow -> {
                misshowpass = !misshowpass
                showPassword(misshowpass)
            }
            R.id.addMachine_iv_confirmpasswordshow -> {
                misshowconfirmpass = !misshowconfirmpass
                showConfirmPassword(misshowconfirmpass)
            }
        }
    }

    private fun showPassword(isShow: Boolean) {
        if (isShow){
            addMachine_et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            addMachine_iv_passwordshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            addMachine_et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            addMachine_iv_passwordshow.setImageResource(R.drawable.ic_show_password)
        }
        addMachine_et_password.setSelection(addMachine_et_password.text.toString().length)
    }

    private fun showConfirmPassword(isShow:Boolean) {
        if (isShow){
            addMachine_et_confirmpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            addMachine_iv_confirmpasswordshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            addMachine_et_confirmpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            addMachine_iv_confirmpasswordshow.setImageResource(R.drawable.ic_show_password)
        }
        addMachine_et_confirmpassword.setSelection(addMachine_et_confirmpassword.text.toString().length)
    }
}