package com.example.tp_14804_14861_14876.Activitys

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.example.tp_14804_14861_14876.Fragments.*
import com.example.tp_14804_14861_14876.R
import com.example.tp_14804_14861_14876.Utils.Alert
import com.example.tp_14804_14861_14876.Utils.ConnectionReceiver
import com.example.tp_14804_14861_14876.Utils.ReceiverConnection
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File


class MainActivity : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener{

    lateinit var drawer_layout:DrawerLayout
    lateinit var navigationview: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var user_iv_photo: ImageView
    lateinit var user_tv_name: TextView
    lateinit var user_tv_id: TextView
    lateinit var hview: View
    lateinit var transformation: Transformation
    var FolderName: String? = null

    lateinit var mainFragment: MainFragment
    lateinit var recordFragment: RecordFragment
    lateinit var audioListFragment: AudioListFragment
    lateinit var temperatureFragment: TemperatureFragment
    lateinit var accerelometerFragment: AccerelometerFragment
    lateinit var settingsFragment: SettingsFragment


    var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Internet Connection
        ReceiverConnection.instance.setConnectionListener(this)
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


        //Firebase info
        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth?.currentUser
        val name:String? = user?.displayName
        val id:String? = user?.uid
        var photo = user?.photoUrl

        user_tv_name.text = name
        user_tv_id.text = id

        // rounded corner image
        val radius = 50
        val margin = 0
        transformation = RoundedCornersTransformation(radius, margin)
        Picasso.get().load(photo).transform(transformation).into(user_iv_photo)

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

        navigationview.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.photo_icon -> {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            111
                        )
                    }
                    mainFragment = MainFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.drawable_frameLayout, mainFragment, null).addToBackStack(null)
                        .commit()
                }
                R.id.audio_list_icon -> {
                    audioListFragment = AudioListFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.drawable_frameLayout, audioListFragment, null).addToBackStack(
                            null
                        )
                        .commit()
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
                R.id.settings_icon -> {
                    settingsFragment = SettingsFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.drawable_frameLayout, settingsFragment, null).addToBackStack(
                            null
                        )
                        .commit()
                }

            }
            drawer_layout.closeDrawers()
            true
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder, this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "audios")
        println(file)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (!file.exists()) {
                file.mkdirs()
                Toast.makeText(this@MainActivity, "Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Folder Already Exists", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }
}