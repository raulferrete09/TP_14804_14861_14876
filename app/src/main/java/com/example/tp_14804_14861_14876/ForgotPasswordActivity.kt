package com.example.tp_14804_14861_14876

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    lateinit var  fp_et_email: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        fp_et_email = findViewById<EditText>(R.id.fp_et_email)

        var fp_et_email = findViewById<EditText>(R.id.fp_et_email)

            fp_et_email.setOnClickListener {
             SendPassword()
            }
    }

    fun SendPassword(){
        val email = fp_et_email.text.toString()

        auth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener {
                task ->
                if (task.isSuccessful()) {
                    Log.d("TAG", "Email sent.")
                    ReturnMain()
                }

            }
    }

    fun ReturnMain(){
        startActivity(Intent(this,LoginActivity::class.java))
    }
}