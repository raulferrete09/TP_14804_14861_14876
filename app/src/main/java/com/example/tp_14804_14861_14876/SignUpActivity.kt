package com.example.tp_14804_14861_14876

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    lateinit var signup_et_name: EditText
    lateinit var signup_et_surname: EditText
    lateinit var signup_et_email: EditText
    lateinit var signup_et_password: EditText
    lateinit var signup_tv_signin: TextView
    lateinit var signup_btn_signup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signup_et_name = findViewById<EditText>(R.id.signup_et_name)
        signup_et_surname = findViewById<EditText>(R.id.signup_et_surname)
        signup_et_email = findViewById<EditText>(R.id.signup_et_email)
        signup_et_password = findViewById<EditText>(R.id.signup_et_password)
        signup_tv_signin = findViewById<TextView>(R.id.signup_tv_signin)
        signup_btn_signup = findViewById<Button>(R.id.signup_btn_signup)

        signup_tv_signin.setOnClickListener{
            ToLoginPage()
        }
        signup_btn_signup.setOnClickListener{
            if(signup_et_name.text.isNotEmpty() && signup_et_surname.text.isNotEmpty()
                && signup_et_email.text.isNotEmpty() && signup_et_password.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(signup_et_email.text.toString()
                    , signup_et_password.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        ToLoginPage()
                    }else{
                        showAlert()
                    }
                }
            }
        }
    }

    private fun ToLoginPage() {
        startActivity(Intent(this,LoginActivity::class.java))
    }

    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("An authentication error has occurred")
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

}