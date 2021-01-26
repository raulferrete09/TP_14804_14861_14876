package com.example.tp_14804_14861_14876.Activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import com.example.tp_14804_14861_14876.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
/*
Class that allows a splash screen while the app isn't started
After the splash screen, the app checks if any account is logged
if one account is logged, the app automatically moves to Main Activity
otherwise, the app starts in Login Activity to let the user log in his account
 */
class SplashScreenActivity : AppCompatActivity() {

    var googleSignInClient: GoogleSignInClient? = null
    var auth : FirebaseAuth? = null

    val splashtime: Long =3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            if(auth?.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        , splashtime)
    }
}