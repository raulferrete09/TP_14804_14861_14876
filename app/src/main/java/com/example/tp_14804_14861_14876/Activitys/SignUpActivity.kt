package com.example.tp_14804_14861_14876.Activitys

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import com.example.tp_14804_14861_14876.Notification.*
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener, View.OnClickListener {

    // Inicialization of the Project variables

    val TAG = "SignUpActivity"
    var misshowpass = false
    var misshowconfirmpass = false
    var validationpassword: String? = "false"
    var x:Int = 0
    var auth : FirebaseAuth? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var referance: DatabaseReference
    lateinit var signup_et_name: EditText
    lateinit var signup_et_surname: EditText
    lateinit var signup_et_email: EditText
    lateinit var signup_et_password: EditText
    lateinit var signup_tv_signin: TextView
    lateinit var signup_btn_signup: Button
    lateinit var password_iv_show: ImageView
    lateinit var password_iv_confirmshow: ImageView
    lateinit var signup_et_confirmpassword: EditText
    lateinit var progressDialog: ProgressDialog
    lateinit var id: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

         //Internet Connection
        baseContext.registerReceiver(ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ReceiverConnection.instance.setConnectionListener(this)

        signup_et_name = findViewById<EditText>(R.id.signup_et_name)
        signup_et_surname = findViewById<EditText>(R.id.signup_et_surname)
        signup_et_email = findViewById<EditText>(R.id.signup_et_email)
        signup_et_password = findViewById<EditText>(R.id.signup_et_password)
        signup_tv_signin = findViewById<TextView>(R.id.signup_tv_signin)
        signup_btn_signup = findViewById<Button>(R.id.signup_btn_signup)
        password_iv_show = findViewById<ImageView>(R.id.password_iv_show)
        password_iv_confirmshow = findViewById<ImageView>(R.id.password_iv_confirmshow)
        database = FirebaseDatabase.getInstance()
        referance = database.getReference("Users")
        auth = FirebaseAuth.getInstance()
        signup_et_confirmpassword = findViewById<EditText>(R.id.signup_et_confirmpassword)


        signup_et_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = signup_et_password.text.toString()
                validationpassword = validatePassword(pass)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        signup_et_name.setOnClickListener(this)
        signup_et_surname.setOnClickListener(this)
        signup_et_email.setOnClickListener(this)
        signup_et_password.setOnClickListener(this)
        signup_et_confirmpassword.setOnClickListener(this)
        signup_btn_signup.setOnClickListener(this)

        password_iv_show.setOnClickListener {
            misshowpass = !misshowpass
            showPassword(misshowpass)
        }
        password_iv_confirmshow.setOnClickListener {
            misshowconfirmpass = !misshowconfirmpass
            showConfirmPassword(misshowconfirmpass)
        }
        signup_tv_signin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            id = FirebaseAuth.getInstance().uid.toString()
            val token = it.token
            println(token)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

    }
    /*
    Function which elaborate our notification
     */
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            }
            else {
                Log.e(TAG, response.errorBody().toString())
            }
        }catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
    /*
    Function which sends the info to the firebase, and returns the user to the Login Activity
     */
    private fun ToLoginPage() {
        sendData()
        startActivity(Intent(this, LoginActivity::class.java))
    }
    /*
    Function to save data from the user
     */
    private fun sendData() {
        val user = auth?.currentUser
        val uid = user?.uid
        var map = mutableMapOf<String,String?>()
        var name = signup_et_name.text.toString() + " " + signup_et_surname.text.toString()
        map["name"]=name
        map["email"]=signup_et_email.text.toString().trim()
        database.reference
            .child("users")
            .child("$uid")
            .setValue(map)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("$name")
            .build()
        user!!.updateProfile(profileUpdates)
    }

    /*
    This function is the most important of the Sign Up Activity
    - Check if any textView is empty and check if the password is validated (send an alert, in
    case of one this problems happen
    - If there's no problem in the credentials, the user account is created, the app returns to
    Login Activity and send a notification telling the user that the account was
    created successfully
     */
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.signup_btn_signup ->
                if(signup_et_name.text.isNotEmpty() && signup_et_surname.text.isNotEmpty()
                        && signup_et_email.text.isNotEmpty() && signup_et_password.text.isNotEmpty()
                        && signup_et_confirmpassword.text.isNotEmpty()
                        && (signup_et_confirmpassword.text.toString() == signup_et_password.text.toString())
                        && validationpassword == "false") {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(signup_et_email.text.toString(), signup_et_password.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //startFirebaseService()
                            ToLoginPage()
                            progressDialog()
                            val title = "Sign Up"
                            val message = "Account created successfully"
                            if (FirebaseAuth.getInstance().uid == id)
                            PushNotification(
                                NotificationData(title, message),
                                TOPIC
                            ).also {
                                sendNotification(it)
                            }


                        }
                    }
                }else {
                    when {
                        signup_et_name.text.isEmpty() -> {
                            x = 1
                            showAlert(x)
                        }
                        signup_et_surname.text.isEmpty() -> {
                            x = 2
                            showAlert(x)
                        }
                        signup_et_email.text.isEmpty() -> {
                            x = 3
                            showAlert(x)
                        }
                        signup_et_password.text.isEmpty() -> {
                            x = 4
                            showAlert(x)
                        }
                        ((signup_et_confirmpassword.text.isEmpty()) || (signup_et_confirmpassword.text.toString() != signup_et_password.text.toString()))-> {
                            x = 5
                            showAlert(x)
                        }
                        ((signup_et_confirmpassword.text.toString() == signup_et_password.text.toString()) && (validationpassword != "false"))  -> {
                            x = 6
                            showAlert(x)
                    }
                }
            }
        }
    }
    /*
    Function to show user an alert in case of any problem relative with credentials errors
     */
    fun showAlert(x:Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        when(x) {
            1 -> builder.setMessage("An authentication error has occurred. Please define a name.")
            2 -> builder.setMessage("An authentication error has occurred. Please define a surname.")
            3 -> builder.setMessage("An authentication error has occurred. Please define a email.")
            4 -> builder.setMessage("An authentication error has occurred. Please define password.")
            5 -> builder.setMessage("An authentication error has occurred. Passwords do not match.")
            6 -> builder.setMessage("An password verification error has occurred. Please choose a password with 8 or more characters including uppercase, lowercase and digits.")
        }
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }
    /*
    These two consecutive functions, allows user to check password and confirm password credentials
     */
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

    fun showConfirmPassword(isShow:Boolean) {
        if (isShow){
            signup_et_confirmpassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_hide_password)
        } else {
            signup_et_confirmpassword.transformationMethod = PasswordTransformationMethod.getInstance()
            password_iv_confirmshow.setImageResource(R.drawable.ic_show_password)
        }
        signup_et_confirmpassword.setSelection(signup_et_confirmpassword.text.toString().length)
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
    Function that validates the password with the necessary credentials
    -At least one upper case, one lower case, one number, and a combination of 8 or more characters
     */
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
    Function for the transition between activities
     */
    override fun onBackPressed() {
        progressDialog.dismiss()
    }
    /*
        Function to check start Firebase Service
    */
    private fun startFirebaseService() {
        val intent = Intent(this, FirebaseMessagingService::class.java)
        startService(intent)
    }
}
