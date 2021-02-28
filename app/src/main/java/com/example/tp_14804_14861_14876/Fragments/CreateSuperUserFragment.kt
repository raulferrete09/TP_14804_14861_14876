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
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateSuperUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateSuperUserFragment : Fragment(), View.OnClickListener {

    lateinit var settingsMachineFragment: SettingsMachineFragment
    lateinit var transaction: FragmentTransaction

    lateinit var createSuperUser_et_username:EditText
    lateinit var createSuperUser_et_id: EditText
    lateinit var createSuperUser_et_password: EditText
    lateinit var createSuperUser_et_confirmpassword: EditText
    lateinit var createSuperUser_btn_create: Button
    lateinit var createSuperUser_iv_passwordshow: ImageView
    lateinit var createSuperUser_iv_confirmpasswordshow: ImageView

    var x:Int = 0
    var auth : FirebaseAuth? = null
    var validationpassword: String? = "false"
    var misshowpass = false
    var misshowconfirmpass = false

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
        return inflater.inflate(R.layout.fragment_create_superuser, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateSuperUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateSuperUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createSuperUser_et_username = view.findViewById<EditText>(R.id.createSuperUser_et_username)
        createSuperUser_et_id = view.findViewById<EditText>(R.id.createSuperUser_et_id)
        createSuperUser_et_password = view.findViewById<EditText>(R.id.createSuperUser_et_password)
        createSuperUser_et_confirmpassword = view.findViewById<EditText>(R.id.createSuperUser_et_confirmpassword)
        createSuperUser_btn_create = view.findViewById<Button>(R.id.createSuperUser_btn_create)
        createSuperUser_iv_passwordshow = view.findViewById<ImageView>(R.id.createSuperUser_iv_passwordshow)
        createSuperUser_iv_confirmpasswordshow = view.findViewById<ImageView>(R.id.createSuperUser_iv_confirmpasswordshow)

        createSuperUser_et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = createSuperUser_et_password.text.toString()
                validationpassword = validatePassword(pass)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        createSuperUser_et_username.setOnClickListener(this)
        createSuperUser_et_id.setOnClickListener(this)
        createSuperUser_et_password.setOnClickListener(this)
        createSuperUser_et_confirmpassword.setOnClickListener(this)
        createSuperUser_btn_create.setOnClickListener(this)
        createSuperUser_iv_passwordshow.setOnClickListener(this)
        createSuperUser_iv_confirmpasswordshow.setOnClickListener(this)

//        //Firebase info
//        auth = FirebaseAuth.getInstance()
//        val user = auth?.currentUser
//        val name = user?.displayName
//        val email = user?.email
//        idUser = user?.uid
//        var photo = user?.photoUrl
//        val superUser = user?.email
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.createSuperUser_btn_create -> {
                if(createSuperUser_et_username.text.toString().isNotEmpty() && createSuperUser_et_id.text.toString().isNotEmpty() &&
                    createSuperUser_et_password.text.toString().isNotEmpty() && createSuperUser_et_confirmpassword.text.toString().isNotEmpty()){
                    if (createSuperUser_et_password.text.toString() == createSuperUser_et_confirmpassword.text.toString() && validationpassword == "false") {
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
            R.id.createSuperUser_iv_passwordshow -> {
                misshowpass = !misshowpass
                showPassword(misshowpass)
            }
            R.id.createSuperUser_iv_confirmpasswordshow -> {
                misshowconfirmpass = !misshowconfirmpass
                showConfirmPassword(misshowconfirmpass)
            }
        }
    }

    private fun showPassword(isShow: Boolean) {
        if (isShow){
            createSuperUser_et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            createSuperUser_iv_passwordshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            createSuperUser_et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            createSuperUser_iv_passwordshow.setImageResource(R.drawable.ic_show_password)
        }
        createSuperUser_et_password.setSelection(createSuperUser_et_password.text.toString().length)
    }

    private fun showConfirmPassword(isShow:Boolean) {
        if (isShow){
            createSuperUser_et_confirmpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            createSuperUser_iv_confirmpasswordshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            createSuperUser_et_confirmpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            createSuperUser_iv_confirmpasswordshow.setImageResource(R.drawable.ic_show_password)
        }
        createSuperUser_et_confirmpassword.setSelection(createSuperUser_et_confirmpassword.text.toString().length)
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
            1 -> builder.setMessage("An authentication error has occurred. The words do not match or the required fields are missing.")
            2 -> builder.setMessage("A password verification error has occurred. Please choose a password with 8 or more characters including uppercase, lowercase and digits.")
        }
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

}