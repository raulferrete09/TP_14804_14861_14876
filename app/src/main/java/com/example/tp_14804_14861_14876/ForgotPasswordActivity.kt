package com.example.tp_14804_14861_14876

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : AppCompatActivity(),ConnectionReceiver.ConnectionReceiverListener {
    var auth : FirebaseAuth? = null
    lateinit var  fp_et_email: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        baseContext.registerReceiver(ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ReceiverConnection.instance.setConnectionListener(this)

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

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder,this)
        }
    }
}