package com.example.tp_14804_14861_14876.Activitys

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.Alert
import com.example.tp_14804_14861_14876.Utils.ConnectionReceiver
import com.example.tp_14804_14861_14876.Utils.ReceiverConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener, View.OnClickListener {

    var misshowpass = false
    var misshowconfirmpass = false
    var validationpassword: String? = "false"
    var x:Int = 0
    var auth : FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var referance: DatabaseReference
    lateinit var signup_et_name: EditText
    lateinit var signup_et_surname: EditText
    lateinit var signup_et_email: EditText
    lateinit var signup_et_password: EditText
    lateinit var signup_tv_signin: TextView
    lateinit var signup_btn_signup: Button
    lateinit var password_iv_show: ImageView
    lateinit var password_iv_confirmshow: ImageView
    lateinit var signup_et_confirmpassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        ReceiverConnection.instance.setConnectionListener(this)

        signup_et_name = findViewById<EditText>(R.id.signup_et_name)
        signup_et_surname = findViewById<EditText>(R.id.signup_et_surname)
        signup_et_email = findViewById<EditText>(R.id.signup_et_email)
        signup_et_password = findViewById<EditText>(R.id.signup_et_password)
        signup_tv_signin = findViewById<TextView>(R.id.signup_tv_signin)
        signup_btn_signup = findViewById<Button>(R.id.signup_btn_signup)
        password_iv_show = findViewById<ImageView>(R.id.password_iv_show)
        password_iv_confirmshow = findViewById<ImageView>(R.id.password_iv_confirmshow)
        database = FirebaseDatabase.getInstance()
        referance = database.getReference("Users")
        auth = FirebaseAuth.getInstance()
        signup_et_confirmpassword = findViewById<EditText>(R.id.signup_et_confirmpassword)


        signup_et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = signup_et_password.text.toString()
                validationpassword = validatePassword(pass)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        signup_et_name.setOnClickListener(this)
        signup_et_surname.setOnClickListener(this)
        signup_et_email.setOnClickListener(this)
        signup_et_password.setOnClickListener(this)
        signup_et_confirmpassword.setOnClickListener(this)
        signup_btn_signup.setOnClickListener(this)

        password_iv_show.setOnClickListener {
            misshowpass = !misshowpass
            showPassword(misshowpass)
        }
        password_iv_confirmshow.setOnClickListener {
            misshowconfirmpass = !misshowconfirmpass
            showConfirmPassword(misshowconfirmpass)
        }
        signup_tv_signin.setOnClickListener{
            ToLoginPage()
        }

    }

    private fun ToLoginPage() {
        sendData()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun sendData() {
        val user = auth?.currentUser
        val uid = user?.uid
        var map = mutableMapOf<String,String?>()
        var name = signup_et_name.text.toString() + " " + signup_et_surname.text.toString()
        map["name"]=name
        map["email"]=signup_et_email.text.toString().trim()
        database.reference
            .child("users")
            .child("$uid")
            .setValue(map)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("$name")
            .build()
        user!!.updateProfile(profileUpdates)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.signup_btn_signup ->
                if(signup_et_name.text.isNotEmpty() && signup_et_surname.text.isNotEmpty()
                        && signup_et_email.text.isNotEmpty() && signup_et_password.text.isNotEmpty()
                        && signup_et_confirmpassword.text.isNotEmpty()
                        && (signup_et_confirmpassword.text.toString() == signup_et_password.text.toString())
                        && validationpassword == "false") {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(signup_et_email.text.toString(), signup_et_password.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            ToLoginPage()
                        }
                    }
                }else {
                    when {
                        signup_et_name.text.isEmpty() -> {
                            x = 1
                            showAlert(x)
                        }
                        signup_et_surname.text.isEmpty() -> {
                            x = 2
                            showAlert(x)
                        }
                        signup_et_email.text.isEmpty() -> {
                            x = 3
                            showAlert(x)
                        }
                        signup_et_password.text.isEmpty() -> {
                            x = 4
                            showAlert(x)
                        }
                        ((signup_et_confirmpassword.text.isEmpty()) || (signup_et_confirmpassword.text.toString() != signup_et_password.text.toString()))-> {
                            x = 5
                            showAlert(x)
                        }
                        ((signup_et_confirmpassword.text.toString() == signup_et_password.text.toString()) && (validationpassword != "false"))  -> {
                            x = 6
                            showAlert(x)
                    }
                }
            }
        }
    }

    fun showAlert(x:Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        when(x) {
            1 -> builder.setMessage("An authentication error has occurred. Please define a name.")
            2 -> builder.setMessage("An authentication error has occurred. Please define a surname.")
            3 -> builder.setMessage("An authentication error has occurred. Please define a email.")
            4 -> builder.setMessage("An authentication error has occurred. Please define password.")
            5 -> builder.setMessage("An authentication error has occurred. Passwords do not match.")
            6 -> builder.setMessage("An password verification error has occurred. Please choose a password with 8 or more characters including uppercase, lowercase and digits.")
        }
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    fun showPassword(isShow:Boolean) {
        if (isShow){
            signup_et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_show.setImageResource(R.drawable.ic_hide_password)
        } else {
            signup_et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_show.setImageResource(R.drawable.ic_show_password)
        }
        signup_et_password.setSelection(signup_et_password.text.toString().length)
    }

    fun showConfirmPassword(isShow:Boolean) {
        if (isShow){
            signup_et_confirmpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            signup_et_confirmpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_show_password)
        }
        signup_et_confirmpassword.setSelection(signup_et_confirmpassword.text.toString().length)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder,this)
        }
    }
    fun validatePassword(password: String): String? {
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
