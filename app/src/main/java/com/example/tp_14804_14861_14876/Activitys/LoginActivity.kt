package com.example.tp_14804_14861_14876.Activitys

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Base64
import android.util.Log
import android.widget.*
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.Alert
import com.example.tp_14804_14861_14876.Utils.ConnectionReceiver
import com.example.tp_14804_14861_14876.Utils.ReceiverConnection
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.Login
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


class LoginActivity : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {
    var auth : FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 12502
    var callbackManager : CallbackManager? = null
    var misshowpass = false
    lateinit var login_btn_signin:Button
    lateinit var login_btn_gmail:Button
    lateinit var login_btn_facebook:Button
    lateinit var login_tv_signup: TextView
    lateinit var login_et_email:EditText
    lateinit var login_et_password:EditText
    lateinit var login_tv_forgpass:TextView
    lateinit var password_iv_show: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_btn_signin = findViewById<Button>(R.id.login_btn_signin)
        login_btn_gmail = findViewById<Button>(R.id.login_btn_gmail)
        login_btn_facebook = findViewById<Button>(R.id.login_btn_facebook)
        login_tv_signup = findViewById<TextView>(R.id.login_tv_signup)
        login_et_email = findViewById<EditText>(R.id.login_et_email)
        login_et_password = findViewById<EditText>(R.id.login_et_password)
        login_tv_forgpass = findViewById<TextView>(R.id.login_tv_forgpass)
        password_iv_show = findViewById<ImageView>(R.id.password_iv_show)

        auth = FirebaseAuth.getInstance()
        if(auth?.currentUser!= null) {
            startActivity(Intent(this, MainActivity::class.java))
        }


        baseContext.registerReceiver(ConnectionReceiver(),IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ReceiverConnection.instance.setConnectionListener(this)



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
        password_iv_show.setOnClickListener {
            misshowpass = !misshowpass
            showPassword(misshowpass)
        }
        showPassword(misshowpass)
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1082061836398-n5ovmh9vjckaqrm68r3omcl2pvbgvu9d.apps.googleusercontent.com") //values.xml
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        //printHashKey()
        callbackManager = CallbackManager.Factory.create()

    }
    //NFDt/SXshxmq1FhkMKa3tkEgyDI=

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder,this)
        }
    }

    fun signupaccount(){
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    fun forgetpassword(){
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
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
        if(login_et_email.text.toString().isNotEmpty() && login_et_password.text.toString().isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(
                login_et_email.text.toString(),
                login_et_password.text.toString()
            )
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Creating a user account
                        moveMainPage(task.result?.user)
                    } else if (task.exception?.message.isNullOrEmpty()) {
                        //Show the error message
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    } else {
                        //Login if you have account
                        signinEmail()
                    }
                }
        } else {
            showAlert()
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
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    fun showPassword(isShow:Boolean) {
        if (isShow){
            login_et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_show.setImageResource(R.drawable.ic_hide_password)
        } else {
            login_et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_show.setImageResource(R.drawable.ic_show_password)
        }
        login_et_password.setSelection(login_et_password.text.toString().length)
    }
    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("An authentication error has occurred. Please set an email and/or password")
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

}