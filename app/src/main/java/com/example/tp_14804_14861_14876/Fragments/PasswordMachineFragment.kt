package com.example.tp_14804_14861_14876.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PasswordMachineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PasswordMachineFragment : Fragment(), View.OnClickListener {

    var currentmisshowpass = false
    var misshowpass = false
    var misshowconfirmpass = false
    var x:Int = 0
    var validationpassword: String? = "false"
    var auth : FirebaseAuth? = null
    var idUser: String? = null
    var oldPassword: Any? = null

    lateinit var settingsMachineFragment: SettingsMachineFragment
    lateinit var transaction: FragmentTransaction
    lateinit var settingsMachine_et_currentpassword: EditText
    lateinit var settingsMachine_et_password: EditText
    lateinit var settingsMachine_et_confirmpassword: EditText
    lateinit var password_iv_currentshow: ImageView
    lateinit var password_iv_show: ImageView
    lateinit var password_iv_confirmshow: ImageView
    lateinit var settingsmachine_btn_change_password: Button

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
        return inflater.inflate(R.layout.fragment_password_machine, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PasswordMachineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PasswordMachineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsMachine_et_currentpassword = view.findViewById<EditText>(R.id.settingsMachine_et_currentpassword)
        settingsMachine_et_password = view.findViewById<EditText>(R.id.settingsMachine_et_password)
        settingsMachine_et_confirmpassword = view.findViewById<EditText>(R.id.settingsMachine_et_confirmpassword)
        password_iv_currentshow = view.findViewById<ImageView>(R.id.password_iv_currentshow)
        password_iv_show = view.findViewById<ImageView>(R.id.password_iv_show)
        password_iv_confirmshow = view.findViewById<ImageView>(R.id.password_iv_confirmshow)
        settingsmachine_btn_change_password = view.findViewById<Button>(R.id.settingsmachine_btn_change_password)

        settingsMachine_et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = settingsMachine_et_password.text.toString()
                validationpassword = validatePassword(pass)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        settingsMachine_et_currentpassword.setOnClickListener(this)
        settingsMachine_et_password.setOnClickListener(this)
        settingsMachine_et_confirmpassword.setOnClickListener(this)
        password_iv_currentshow.setOnClickListener(this)
        password_iv_show.setOnClickListener(this)
        password_iv_confirmshow.setOnClickListener(this)
        settingsmachine_btn_change_password.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()
        val user = auth?.currentUser
        idUser = user?.uid

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.password_iv_currentshow -> {
                currentmisshowpass = !currentmisshowpass
                showCurrentPassword(currentmisshowpass)
            }
            R.id.password_iv_show -> {
                misshowpass = !misshowpass
                showPassword(misshowpass)
            }
            R.id.password_iv_confirmshow -> {
                misshowconfirmpass = !misshowconfirmpass
                showConfirmPassword(misshowconfirmpass)
            }
            R.id.settingsmachine_btn_change_password -> {
                changePassword()
            }
        }
    }

    private fun showCurrentPassword(isShow: Boolean) {
        if (isShow){
            settingsMachine_et_currentpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_currentshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            settingsMachine_et_currentpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_currentshow.setImageResource(R.drawable.ic_show_password)
        }
        settingsMachine_et_currentpassword.setSelection(settingsMachine_et_currentpassword.text.toString().length)    }

    private fun showPassword(isShow: Boolean) {
        if (isShow){
            settingsMachine_et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_show.setImageResource(R.drawable.ic_hide_password)
        } else {
            settingsMachine_et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_show.setImageResource(R.drawable.ic_show_password)
        }
        settingsMachine_et_password.setSelection(settingsMachine_et_password.text.toString().length)    }

    private fun showConfirmPassword(isShow:Boolean) {
        if (isShow){
            settingsMachine_et_confirmpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            settingsMachine_et_confirmpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_show_password)
        }
        settingsMachine_et_confirmpassword.setSelection(settingsMachine_et_confirmpassword.text.toString().length)
    }
    private fun changePassword() {
        getPassword()
        if(settingsMachine_et_currentpassword.text.isNotEmpty() && settingsMachine_et_password.text.isNotEmpty()
            && settingsMachine_et_confirmpassword.text.isNotEmpty()) {
            if (settingsMachine_et_password.text.toString() == settingsMachine_et_confirmpassword.text.toString() && validationpassword == "false"
                && settingsMachine_et_currentpassword.text.toString() == oldPassword ) {
                updatePassword()
                settingsMachineFragment = SettingsMachineFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, settingsMachineFragment)
                transaction.commit()
            } else {
                x = 2
                showAlert(x)
            }
        } else {
            x = 1
            showAlert(x)
        }
    }

    private fun validatePassword(password: String): String? {
        val upperCase = Pattern.compile("[A-Z]")
        val lowerCase = Pattern.compile("[a-z]")
        val digitCase = Pattern.compile("[0-9]")
        var validate: String = "false"
        if (lowerCase.matcher(password).find()
            && upperCase.matcher(password).find()
            && digitCase.matcher(password).find()
            && password.length >= 8) {
            validate = "false"
        } else {
            validate = "true"
        }
        return validate
    }

    /*
        The Function showAlert() brings to the user the possible error that occurred exists 2 options the first one "An authentication error has occurred. Unfilled passwords and/or passwords do not match."
        and the second one "A password verification error has occurred. Please choose a password with 8 or more characters including uppercase, lowercase, and digits."
     */
    private fun showAlert(x:Int){
        val builder = AlertDialog.Builder(requireContext())
        when(x) {
            1 -> builder.setMessage("An authentication error has occurred. Unfilled passwords and/or passwords do not match.")
            2 -> builder.setMessage("A password verification error has occurred. Please choose a password with 8 or more characters including uppercase, lowercase and digits.")
        }
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    private fun updatePassword(){
        var map = mutableMapOf<String,Any?>()
        var database = FirebaseDatabase.getInstance()
        map["password"]=settingsMachine_et_password.text.toString()
        database.reference
            .child("SuperUsers")
            .child("${idUser}")
            .updateChildren(map)
    }

    private fun getPassword(){
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
                        oldPassword = map["password"]
                    }
                }
            })
    }

}