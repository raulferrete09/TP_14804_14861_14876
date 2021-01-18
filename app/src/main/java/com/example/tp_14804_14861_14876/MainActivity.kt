package com.example.tp_14804_14861_14876

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


class MainActivity : AppCompatActivity(),ConnectionReceiver.ConnectionReceiverListener {

    lateinit var drawer_layout:DrawerLayout
    lateinit var navigationview: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var user_iv_photo: ImageView
    lateinit var user_tv_name: TextView
    lateinit var user_tv_id: TextView
    lateinit var hview: View
    lateinit var transformation: Transformation
    lateinit var add_btn_submission: FloatingActionButton



    var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Internet Connection
        ReceiverConnection.instance.setConnectionListener(this)

        setUpNavigationDrawer()

        add_btn_submission = findViewById<FloatingActionButton>(R.id.add_btn_submission)

        add_btn_submission.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }


    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected){
            var alert = Alert()
            val builder = AlertDialog.Builder(this)
            alert.showAlert(builder, this)
        }
    }

    private fun setUpNavigationDrawer(){

        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.main_toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

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
        val margin = 5
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
        navigationview.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.photo_icon ->
                    startActivityForResult(galleryIntent, RESULT_GALLERY)
                    //startActivity(Intent.createChooser(intent, "Open folder"))
                R.id.mic_sound_icon ->
                    startActivity(Intent(this, RecordAudioActivity::class.java))
                R.id.audio_list_icon -> {
                }
            }
            true
        }
    }
}