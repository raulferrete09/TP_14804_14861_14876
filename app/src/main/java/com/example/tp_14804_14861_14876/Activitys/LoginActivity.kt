package com.example.tp_14804_14861_14876.Activitys

import android.app.AlertDialog
import android.app.ProgressDialog
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

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
    lateinit var progressDialog: ProgressDialog
    private lateinit var database: FirebaseDatabase
    private lateinit var referance: DatabaseReference



/*
This function will check every action on buttons and also the token to make the google login
with success
 */
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
        database = FirebaseDatabase.getInstance()
        referance = database.getReference("Users")

        auth = FirebaseAuth.getInstance()

        //Internet Connection
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
        callbackManager = CallbackManager.Factory.create()

    }
    /*
        Function to check Internet connection
    */

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder,this)
        }
    }
    /*
        Function that takes the user to the activity related to "Sign Up", with the startActivity
     */
    fun signupaccount(){
        startActivity(Intent(this, SignUpActivity::class.java))
    }
    /*
        Function that takes the user to the activity related to "forget password", with the startActivity
     */
    fun forgetpassword(){
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }
    /*
        Function to check info of Google Login
     */
    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }
    /*
        Function to check the permissions necessary for the login phase
    */
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
    /*
        Function to communicate Facebook Login with Firebase and do the Login Facebook in case of success
     */
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
    /*
        Function to check Google credentials with Firebase
     */
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

    /*
        Function to communicate Google Login with Firebase and do the Login Google in case of success
     */
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
    /*
        Function for the sign in with user credentials
     */
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
    /*
        Function for the login with Emails created by the users
     */
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
    /*
        Function to move us from Activity after the login
     */
    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            progressDialog()
            startActivity(Intent(this, MainActivity::class.java))
            sendData()
            finish()
        }
    }
    /*
        Function to give us the option of watching the password
     */
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
    /*
        Function to alert the user if something is wrong with requirements necessary
     */
    fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("An authentication error has occurred. Please set an email and/or password")
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }
    /*
        Function for the transition between activities
    */
    private fun progressDialog() {
        //Initialize Progress Dialog
        progressDialog = ProgressDialog(this)
        //Show Dialog
        progressDialog.show()
        //Set Content View
        progressDialog.setContentView(R.layout.progress_dialog)
        //Set Transparent background
        progressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    /*
        Function to stop the loading from the previous function
    */
    override fun onBackPressed() {
        progressDialog.dismiss()
    }
    /*
        Function to save data from the user
    */
    private fun sendData(){
        val person = auth?.currentUser
        val uid = person?.uid
        var name = person?.displayName
        var email = person?.email
        var map = mutableMapOf<String,Any?>()

        map["name"]=name
        map["email"]=email
        database.reference
            .child("users")
            .child("$uid")
            .updateChildren(map)
    }
}