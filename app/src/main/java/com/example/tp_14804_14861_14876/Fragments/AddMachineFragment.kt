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
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_machine.*
import java.util.regex.Pattern

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

    lateinit var addMachine_et_nameMachine: EditText
    lateinit var addMachine_et_localzation: EditText
    lateinit var addMachine_et_diagnostic: EditText
    lateinit var addMachine_et_user: EditText
    lateinit var addMachine_et_password: EditText
    lateinit var addMachine_et_confirmpassword: EditText
    lateinit var addMachine_btn_create: Button
    lateinit var addMachine_iv_passwordshow: ImageView
    lateinit var addMachine_iv_confirmpasswordshow: ImageView
    var misshowpass = false
    var misshowconfirmpass = false
    var x:Int = 0
    var validationpassword: String? = "false"


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

        addMachine_et_nameMachine = view.findViewById<EditText>(R.id.addMachine_et_nameMachine)
        addMachine_et_localzation = view.findViewById<EditText>(R.id.addMachine_et_localzation)
        addMachine_et_diagnostic = view.findViewById<EditText>(R.id.addMachine_et_diagnostic)
        addMachine_et_user = view.findViewById<EditText>(R.id.addMachine_et_username)
        addMachine_et_password = view.findViewById<EditText>(R.id.addMachine_et_password)
        addMachine_et_confirmpassword = view.findViewById<EditText>(R.id.addMachine_et_confirmpassword)
        addMachine_btn_create = view.findViewById<Button>(R.id.addMachine_btn_create)
        addMachine_iv_passwordshow = view.findViewById<ImageView>(R.id.addMachine_iv_passwordshow)
        addMachine_iv_confirmpasswordshow = view.findViewById<ImageView>(R.id.addMachine_iv_confirmpasswordshow)

        addMachine_et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = addMachine_et_password.text.toString()
                validationpassword = validatePassword(pass)
            }

            override fun afterTextChanged(s: Editable) {}
        })

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
                if(addMachine_et_nameMachine.text.isNotEmpty()
                    && addMachine_et_user.text.isNotEmpty()
                    && addMachine_et_diagnostic.text.isNotEmpty()
                    && addMachine_et_localzation.text.isNotEmpty()
                    && addMachine_et_password.text.isNotEmpty()
                    && addMachine_et_confirmpassword.text.isNotEmpty()
                    && (addMachine_et_password.text.toString() == addMachine_et_confirmpassword.text.toString())
                    && validationpassword == "false"){
                    CreateMachine()
                    settingsMachineFragment = SettingsMachineFragment()
                    transaction = fragmentManager?.beginTransaction()!!
                    transaction.replace(R.id.drawable_frameLayout, settingsMachineFragment)
                    transaction.commit()
                }else {
                    when {
                        addMachine_et_nameMachine.text.isEmpty() -> {
                            x = 1
                            showAlert(x)
                        }
                        addMachine_et_username.text.isEmpty() -> {
                            x = 2
                            showAlert(x)
                        }
                        addMachine_et_localzation.text.isEmpty() -> {
                            x = 3
                            showAlert(x)
                        }
                        addMachine_et_password.text.isEmpty() -> {
                            x = 4
                            showAlert(x)
                        }
                        ((addMachine_et_confirmpassword.text.isEmpty()) || (addMachine_et_confirmpassword.text.toString() != addMachine_et_password.text.toString()))-> {
                            x = 5
                            showAlert(x)
                        }
                        ((addMachine_et_confirmpassword.text.toString() != addMachine_et_password.text.toString()))  -> {
                            x = 6
                            showAlert(x)
                        }
                    }
                }
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

    private fun CreateMachine(){
        var mapanomaly = mutableMapOf<String,Any?>()
        var mapaudio = mutableMapOf<String,Any?>()
        var mapcostpredicted = mutableMapOf<String,Any?>()
        var mapoperatinghours = mutableMapOf<String,Any?>()

        var map = mutableMapOf<String,Any?>()
        var mapacellerometer = mutableMapOf<String,Any?>()
        var mapTemperature = mutableMapOf<String,Any?>()
        var database = FirebaseDatabase.getInstance()

        mapanomaly["Anomaly"] = ""
        mapaudio["base64"] = "1"
        mapaudio["record"] = "False"
        mapaudio["status"] = ""

        mapcostpredicted["maintenance"] = "0"
        mapcostpredicted["other"] = "0"
        mapcostpredicted["breakdown"] = "0"

        mapoperatinghours["OFF"] = "0"
        mapoperatinghours["ON"] = "0"


        map["localization"] = addMachine_et_localzation.text.toString()
        map["diagnostic"] = addMachine_et_diagnostic.text.toString()
        map["username"]=addMachine_et_user.text.toString()
        map["password"]=addMachine_et_password.text.toString()
        mapacellerometer["status"]=""
        mapacellerometer["x"]=""
        mapacellerometer["y"]=""
        mapacellerometer["z"]=""
        mapTemperature["Temperature"]=""
        mapTemperature["status"]=""

        database.reference
            .child("Machines")
            .child("${addMachine_et_nameMachine.text.toString()}")
            .updateChildren(map)

        database.reference
            .child("Accelerometer")
            .child("${addMachine_et_nameMachine.text.toString()}")
            .updateChildren(mapacellerometer)

        database.reference
            .child("Temperature")
            .child("${addMachine_et_nameMachine.text.toString()}")
            .updateChildren(mapTemperature)

        database.reference
            .child("Dashboard")
            .child("${addMachine_et_nameMachine.text.toString()}")
            .updateChildren(mapanomaly)

        database.reference
            .child("Dashboard")
            .child("${addMachine_et_nameMachine.text.toString()}").child("Audio")
            .updateChildren(mapaudio)

        database.reference
            .child("Dashboard")
            .child("${addMachine_et_nameMachine.text.toString()}").child("Cost Predicted")
            .updateChildren(mapcostpredicted)

        database.reference
            .child("Dashboard")
            .child("${addMachine_et_nameMachine.text.toString()}").child("OH")
            .updateChildren(mapoperatinghours)
    }

    fun showAlert(x:Int){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        when(x) {
            1 -> builder.setMessage("An authentication error has occurred. Please define a Machine Name.")
            2 -> builder.setMessage("An authentication error has occurred. Please define a Name.")
            3 -> builder.setMessage("An authentication error has occurred. Please define a Localization.")
            4 -> builder.setMessage("An authentication error has occurred. Please define password.")
            5 -> builder.setMessage("An authentication error has occurred. Passwords do not match.")
            6 -> builder.setMessage("An password verification error has occurred. Please choose a password with 8 or more characters including uppercase, lowercase and digits.")
        }
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
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

}