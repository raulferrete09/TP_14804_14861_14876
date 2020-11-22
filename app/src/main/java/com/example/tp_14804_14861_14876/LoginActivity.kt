package com.example.tp_14804_14861_14876

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var login_btn_signin = findViewById<Button>(R.id.login_btn_signin)
    var login_et_email = findViewById<EditText>(R.id.login_et_email)
    var login_et_password = findViewById<EditText>(R.id.login_et_password)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        login_btn_signin.setOnClickListener{
            signinAndSignup()
        }
    }
    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(login_et_email.text.toString(),login_et_password.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    //Creating a user account
                    moveMainPage(task.result?.user)
                }else if(task.exception?.message.isNullOrEmpty()){
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }else{
                    //Login if you have account
                    signinEmail()
                }
            }
    }
    fun signinEmail(){
        auth?.signInWithEmailAndPassword(login_et_email.text.toString(),login_et_password.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }
    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}