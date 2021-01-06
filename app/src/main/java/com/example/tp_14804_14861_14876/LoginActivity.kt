package com.example.tp_14804_14861_14876

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*



//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;


class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 12502
    var callbackManager : CallbackManager? = null
    lateinit var login_btn_signin:Button
    lateinit var login_btn_gmail:Button
    lateinit var login_btn_facebook:Button
    lateinit var login_tv_signup: TextView
    lateinit var login_et_email:EditText
    lateinit var login_et_password:EditText
    lateinit var login_tv_forgpass:TextView

    //lateinit var layoutDisconnected: LinearLayout
    //lateinit var layoutConnected: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo= cm.activeNetworkInfo
        /*val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("An authentication error has occurred")
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()*/
        if(networkInfo != null && networkInfo.isConnected) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("No internet connection")
            builder.setMessage("Please choose the type of internet connection you want.")
            val dialog: AlertDialog = builder.create()
            builder.setPositiveButton("Wifi", { dialogInterface: DialogInterface, i: Int ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            })
            builder.setNegativeButton("Mobile Data", { dialogInterface: DialogInterface, i: Int ->
                startActivity(Intent(Settings.ACTION_DATA_USAGE_SETTINGS))
            })
            builder.setNeutralButton("Cancel", { dialogInterface: DialogInterface, i: Int -> })
            builder.show()
        }
        //layoutDisconnected = findViewById<LinearLayout>(R.id.layoutDisconnected)
        //layoutConnected = findViewById<LinearLayout>(R.id.layoutConnected)

        /*val networkConnection  = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->
            if(isConnected) {

            } else {

                //startActivity(Intent(this,NetworkConnectionActivity::class.java))

            }
        })*/

        /*val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo= cm.activeNetworkInfo

        if(networkInfo != null && networkInfo.isConnected){
            //startActivity(Intent(this,MainActivity::class.java))

            //you have connected to the internet

            /*if(networkInfo.type == ConnectivityManager.TYPE_WIFI){
                Toast.makeText(baseContext,"Connected via WIFI Network",Toast.LENGTH_SHORT).show()
            }
            if(networkInfo.type == ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(baseContext,"Connected via MOBLIE Network",Toast.LENGTH_SHORT).show()
                //startActivity(Intent(this,LoginActivity::class.java))

            }*/

        }else{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("No internet connection")
            builder.setMessage("Please choose the type of internet connection you want.")
            builder.setPositiveButton("Wifi",{ dialogInterface: DialogInterface, i: Int ->
                startActivity(Intent( Settings.ACTION_WIFI_SETTINGS))
            })
            builder.setNegativeButton("Mobile Data",{ dialogInterface: DialogInterface, i: Int ->
                startActivity(Intent( Settings.ACTION_DATA_USAGE_SETTINGS))
            })
            builder.setNeutralButton("Cancel",{ dialogInterface: DialogInterface, i: Int -> })
            builder.show()
            //Toast.makeText(baseContext,"No Internet Connection",Toast.LENGTH_SHORT).show()

            //startActivity(Intent( Settings.ACTION_NETWORK_OPERATOR_SETTINGS))
        }*/
        //isNetworkAvailable()

        login_btn_signin = findViewById<Button>(R.id.login_btn_signin)
        login_btn_gmail = findViewById<Button>(R.id.login_btn_gmail)
        login_btn_facebook = findViewById<Button>(R.id.login_btn_facebook)
        login_tv_signup = findViewById<TextView>(R.id.login_tv_signup)
        login_et_email = findViewById<EditText>(R.id.login_et_email)
        login_et_password = findViewById<EditText>(R.id.login_et_password)
        login_tv_forgpass = findViewById<TextView>(R.id.login_tv_forgpass)

        auth = FirebaseAuth.getInstance()

        /*login_btn_signin.setOnClickListener {
            signinAndSignup()
        }*/
        login_btn_signin.setOnClickListener {
            signinAndSignup()
        }
        login_btn_gmail.setOnClickListener {
            googleLogin()
        }
        login_btn_facebook.setOnClickListener {
            facebookLogin()
        }
        login_tv_signup.setOnClickListener {
            signupaccount()
        }
        login_tv_forgpass.setOnClickListener {
            forgetpassword()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1082061836398-n5ovmh9vjckaqrm68r3omcl2pvbgvu9d.apps.googleusercontent.com") //values.xml
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        //printHashKey()
        callbackManager = CallbackManager.Factory.create()
    }
    //NFDt/SXshxmq1FhkMKa3tkEgyDI=
    fun showAlert(){
        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo= cm.activeNetworkInfo
        /*val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("An authentication error has occurred")
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()*/

        val builder = AlertDialog.Builder(this)
        builder.setTitle("No internet connection")
        builder.setMessage("Please choose the type of internet connection you want.")
        val dialog: AlertDialog =builder.create()
        builder.setPositiveButton("Wifi",{ dialogInterface: DialogInterface, i: Int ->
            while(true){
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                if(networkInfo != null && networkInfo.isConnected){
                Toast.makeText(baseContext,"No Internet Connection",Toast.LENGTH_SHORT).show()
                    finish()
                break
            }
            }
            })
        builder.setNegativeButton("Mobile Data",{ dialogInterface: DialogInterface, i: Int ->
            startActivity(Intent( Settings.ACTION_DATA_USAGE_SETTINGS))
            while(true){
                if(networkInfo != null && networkInfo.isConnected){
                    Toast.makeText(baseContext,"No Internet Connection",Toast.LENGTH_SHORT).show()
                    finish()
                    break
                }
            }
        })
        builder.setNeutralButton("Cancel",{ dialogInterface: DialogInterface, i: Int -> })
        builder.show()

    }

    fun signupaccount(){
        startActivity(Intent(this,SignUpActivity::class.java))
    }

    fun forgetpassword(){
        startActivity(Intent(this,ForgotPasswordActivity::class.java))
    }

    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }
    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)

    }
    fun facebookLogin(){
        LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile","email"))

        LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
                    override fun onSuccess(result: LoginResult?) {
                        //Second step
                        handleFacebookAccessToken(result?.accessToken)
                    }

                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException?) {

                    }

                })
    }
    fun handleFacebookAccessToken(token : AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
                ?.addOnCompleteListener {
                    task ->
                    if(task.isSuccessful){

                        //Third step
                        //Login
                        moveMainPage(task.result?.user)
                    }else{
                        //Show the error message
                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                    }
                }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode,resultCode,data)
        if(requestCode == GOOGLE_LOGIN_CODE) {

            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            //result é através do SHA (canto superior direito GRADLE -> Tasks -> Android -> run signingReport)
            if (result != null) {
                if(result.isSuccess) {
                    
                    var account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                }
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
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
    /*fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }*/

}