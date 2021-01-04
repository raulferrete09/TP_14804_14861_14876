package com.example.tp_14804_14861_14876

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    lateinit var  fp_et_email: EditText
    lateinit var  fp_btn_sendpass: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        fp_et_email = findViewById<EditText>(R.id.fp_et_email)
        fp_btn_sendpass = findViewById<Button>(R.id.fp_btn_sendpass)

        fp_btn_sendpass.setOnClickListener {
             SendPassword()
            }
    }

    fun SendPassword(){
        val email = fp_et_email.text.toString()

        auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener {
                task ->
                if (task.isSuccessful()) {
                    Toast.makeText(baseContext,"Email Sent", Toast.LENGTH_SHORT).show()
                    ReturnMain()
                }

            }
    }

    fun ReturnMain(){
        startActivity(Intent(this,LoginActivity::class.java))
    }
}