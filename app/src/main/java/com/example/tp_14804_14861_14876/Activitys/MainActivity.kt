package com.example.tp_14804_14861_14876.Activitys

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tp_14804_14861_14876.Fragments.*
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.Alert
import com.example.tp_14804_14861_14876.Utils.ConnectionReceiver
import com.example.tp_14804_14861_14876.Utils.ReceiverConnection
import com.example.tp_14804_14861_14876.Notification.ServiceNotification
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {

    // Inicialization of the Project variables
    lateinit var drawer_layout: DrawerLayout
    lateinit var navigationview: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var user_iv_photo: ImageView
    lateinit var user_tv_name: TextView
    lateinit var user_tv_id: TextView
    lateinit var hview: View

    lateinit var mainFragment: MainFragment
    lateinit var recordFragment: RecordFragment
    lateinit var audioListFragment: AudioListFragment
    lateinit var temperatureFragment: TemperatureFragment
    lateinit var accerelometerFragment: AccerelometerFragment
    lateinit var settingsFragment: SettingsFragment

    var auth : FirebaseAuth? = null
    var image_uri: Uri? = null
    lateinit var fileref: StorageReference
    private var storageReference: StorageReference? = null
    private var database: FirebaseDatabase? = null
    private val IMAGE_CAPTURE_CODE = 1001
    private val PERMISSION_CODE = 1000
    lateinit var audioServiceIntent: Intent
    lateinit var temperatureServiceIntent: Intent
    lateinit var accelerometerServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Internet Connection
        baseContext.registerReceiver(ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ReceiverConnection.instance.setConnectionListener(this)

        audioServiceIntent = Intent(this, ServiceNotification::class.java)
        startService(audioServiceIntent)

        setUpNavigationDrawer()

    }
    private fun setUpNavigationDrawer() {
        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        //hide toolbar
        supportActionBar?.hide()

        drawer_layout = findViewById<DrawerLayout>(R.id.drawer_layout)
        navigationview = findViewById<NavigationView>(R.id.nav_view)
        hview = navigationview.getHeaderView(0)

        user_tv_id = hview.findViewById<TextView>(R.id.user_tv_id)
        user_tv_name = hview.findViewById<TextView>(R.id.user_tv_name)
        user_iv_photo = hview.findViewById<ImageView>(R.id.user_iv_photo)

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        var RESULT_GALLERY = 0

        user_iv_photo.setOnClickListener {

            //Checks whether the Main Fragment is displayed. If it is not displayed, it shows
            if ( !mainFragment.isVisible) {
                this.mainFragment = MainFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.drawable_frameLayout, mainFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }

        }

        //Firebase info
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val user: FirebaseUser? = auth?.currentUser
        val name:String? = user?.displayName
        val id:String? = user?.uid
        val image = user?.photoUrl
        val firedabase = database!!.getReference("users").child("$id")

        storageReference = FirebaseStorage.getInstance().reference.child("Users Image").child("$id")
        fileref = storageReference!!.child("picture.jpg")

        println(name)
        user_tv_name.text = name
        user_tv_id.text = id

        // rounded corner image
        try {
            fileref?.downloadUrl?.addOnSuccessListener { task ->
                Glide.with(this).load(task).override(300,300).apply(RequestOptions.circleCropTransform()).into(user_iv_photo)
            }
        }catch (e: Exception){
            Glide.with(this).load(image).override(300,300).apply(RequestOptions.circleCropTransform()).into(user_iv_photo)
            e.printStackTrace()
        }

        firedabase.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    var map = snapshot.value as Map<String, Any>
                    user_tv_name.text = map["name"].toString()
                    var photo = map["photo"].toString()
                    var photo_uri = Uri.parse(photo)
                    if (map["photo"] == null) {
                        Glide.with(baseContext).load(image).override(300, 300)
                            .apply(RequestOptions.circleCropTransform()).into(user_iv_photo)
                    } else {
                        Glide.with(baseContext).load(photo_uri).override(300, 300)
                            .apply(RequestOptions.circleCropTransform()).into(user_iv_photo)
                    }
                } else {
                    Glide.with(baseContext).load(image).override(300, 300)
                        .apply(RequestOptions.circleCropTransform()).into(user_iv_photo)
                }
            }
        })

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.openNavDrawer,
            R.string.closeNavDrawer
        )

        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        mainFragment = MainFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.drawable_frameLayout, mainFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        /*Button Fuctions
        here the function is defined for each button
         */
        navigationview.setNavigationItemSelectedListener{
            when (it.itemId) {
                /*
                On the R.id.photo_icon and  R.id.audio_list_icon the app checks the permission to create the folder HVAC in the DCIM folder
                If the app already have the permission its possible to see the photos present in the phone
                 */
                R.id.photo_icon -> {
                    if (checkPermissions()) {
                        val folder =
                            File(
                                getExternalStorageDirectory()
                                    .toString() + File.separator + "DCIM" + File.separator + "HVAC"
                            )
                        if (!folder.exists()) {
                            folder.mkdirs()
                            var openGalleryIntent = Intent(
                                Intent.ACTION_VIEW,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startActivityForResult(openGalleryIntent, 111)
                        } else {
                            var openGalleryIntent = Intent(
                                Intent.ACTION_VIEW,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startActivityForResult(openGalleryIntent, 111)
                        }
                    }
                }
                R.id.audio_list_icon -> {
                    if (checkPermissions()) {
                        val folder =
                            File(
                                getExternalStorageDirectory()
                                    .toString() + File.separator + "DCIM" + File.separator + "HVAC"
                            )
                        if (!folder.exists()) {
                            folder.mkdirs()
                            audioListFragment = AudioListFragment()
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.drawable_frameLayout, audioListFragment, null)
                                .addToBackStack(
                                    null
                                )
                                .commit()
                        } else {
                            audioListFragment = AudioListFragment()
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.drawable_frameLayout, audioListFragment, null)
                                .addToBackStack(
                                    null
                                )
                                .commit()
                        }
                    }
                }
                R.id.mic_sound_icon -> {
                    recordFragment = RecordFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.drawable_frameLayout, recordFragment, null).addToBackStack(
                            null
                        )
                        .commit()
                }
                R.id.temperature_icon -> {
                    temperatureFragment = TemperatureFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.drawable_frameLayout, temperatureFragment, null)
                        .addToBackStack(
                            null
                        )
                        .commit()
                }
                R.id.accelerometer_icon -> {
                    accerelometerFragment = AccerelometerFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.drawable_frameLayout, accerelometerFragment, null)
                        .addToBackStack(
                            null
                        )
                        .commit()
                }
                R.id.take_photo_icon -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_DENIED
                        ) {
                            val permission = arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            requestPermissions(permission, PERMISSION_CODE)
                        } else {
                            val folder =
                                File(getExternalStorageDirectory().toString() + File.separator + "DCIM" + File.separator + "HVAC")
                            if (!folder.exists()) {
                                folder.mkdirs()
                                openCamera()
                            } else {
                                openCamera()
                            }
                        }
                    } else {
                        openCamera()
                    }
                }
                /*
                    Button to open settings
                 */
                R.id.settings_icon -> {
                    settingsFragment = SettingsFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.drawable_frameLayout, settingsFragment, null).addToBackStack(
                            null
                        )
                        .commit()
                }
                /*
                    Button to log out
                 */
                R.id.logout_icon -> {
                    disconnectFromGoogle()
                    disconnectFromFacebook()
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            drawer_layout.closeDrawers()
            true
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val image_string = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/DCIM/HVAC/" + "photo" + timeStamp + ".jpg"
        )
        values.put(MediaStore.Images.Media.TITLE, "New picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera")
        values.put(MediaStore.Images.Media.DATA, image_string.toString())
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraintent:Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        //camera intent
        startActivityForResult(cameraintent, IMAGE_CAPTURE_CODE)
    }
/*
Function that checks permissions, using the requestPermissions as his auxiliary
 */
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            //Permission Granted
            return true
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                111
            )
            return false
        }

    }
    /*
    Function to disconnect the user from Facebook Account
     */
    private fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            GraphRequest.Callback {
                LoginManager.getInstance().logOut()
            }).executeAsync()
    }
    /*
    Function to disconnect the user from Google Account
     */
    private fun disconnectFromGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(this.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
    }
/*
    Function that checks the Internet connection
 */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder, this)
        }
    }
/*
Function to request the permissions necessary to app tasks
 */
    override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                }
            }
        }
    }
}