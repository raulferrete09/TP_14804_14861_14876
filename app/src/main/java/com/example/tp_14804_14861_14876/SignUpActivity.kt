package com.example.tp_14804_14861_14876

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {

    var misshowpass = false
    var misshowconfirmpass = false
    var validationpassword: String? = "false"
    lateinit var signup_et_name: EditText
    lateinit var signup_et_surname: EditText
    lateinit var signup_et_email: EditText
    lateinit var signup_et_password: EditText
    lateinit var signup_tv_signin: TextView
    lateinit var signup_btn_signup: Button
    lateinit var password_iv_show: ImageView
    lateinit var password_iv_confirmshow: ImageView
    lateinit var signup_et_confirmepassword: EditText

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
        signup_et_confirmepassword = findViewById<EditText>(R.id.signup_et_confirmepassword)


        signup_et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = signup_et_password.text.toString()
                validationpassword = validatePassword(pass)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        signup_tv_signin.setOnClickListener{
            ToLoginPage()
        }
        signup_btn_signup.setOnClickListener{
            if(signup_et_name.text.isNotEmpty() && signup_et_surname.text.isNotEmpty()
                && signup_et_email.text.isNotEmpty() && signup_et_password.text.isNotEmpty()
                && signup_et_confirmepassword.text.isNotEmpty()
                && (signup_et_confirmepassword.getText().toString() == signup_et_password.getText().toString()))
            {
                if(validationpassword == "false") {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(signup_et_email.text.toString()
                        , signup_et_password.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful){
                            ToLoginPage()
                        }
                    }
                } else {
                        showAlert()
                }
            }
        }
        password_iv_show.setOnClickListener {
            misshowpass = !misshowpass
            showPassword(misshowpass)
        }
        password_iv_confirmshow.setOnClickListener {
            misshowconfirmpass = !misshowconfirmpass
            showconfirmPassword(misshowconfirmpass)
        }

    }

    private fun ToLoginPage() {
        startActivity(Intent(this,LoginActivity::class.java))
    }

    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("An authentication error has occurred. Please choose a password with 8 or more characters including uppercase, lowercase and digits.")
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

    fun showconfirmPassword(isShow:Boolean) {
        if (isShow){
            signup_et_confirmepassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            signup_et_confirmepassword.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_show_password)
        }
        signup_et_confirmepassword.setSelection(signup_et_confirmepassword.text.toString().length)
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