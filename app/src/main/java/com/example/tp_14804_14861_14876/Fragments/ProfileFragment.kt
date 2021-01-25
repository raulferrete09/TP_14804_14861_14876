package com.example.tp_14804_14861_14876.Fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tp_14804_14861_14876.Activitys.MainActivity
import com.example.tp_14804_14861_14876.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(), View.OnClickListener {

    lateinit var profile_iv_photo: ImageView
    lateinit var profile_et_name: EditText
    lateinit var settings_btn_change_profile: Button
    var imageUri: Uri? = null

    lateinit var mainFragment: MainFragment
    lateinit var transaction: FragmentTransaction

    var auth : FirebaseAuth? = null
    var uid: String? = null
    lateinit var fileref: StorageReference
    private var storageReference: StorageReference? = null
    private lateinit var database: FirebaseDatabase

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        val user = auth?.currentUser
        uid = user?.uid
        storageReference = FirebaseStorage.getInstance().reference.child("Users Image").child("$uid")
        fileref = storageReference!!.child("picture.jpg")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_et_name = view.findViewById<EditText>(R.id.profile_et_name)
        profile_iv_photo = view.findViewById<ImageView>(R.id.profile_iv_photo)
        settings_btn_change_profile = view.findViewById<Button>(R.id.settings_btn_change_profile)


        val user = auth?.currentUser
        val image = user?.photoUrl
        mainFragment = MainFragment()
        profile_et_name.setOnClickListener(this)
        profile_iv_photo.setOnClickListener(this)
        settings_btn_change_profile.setOnClickListener(this)

        try {
            fileref?.downloadUrl?.addOnSuccessListener { task ->
                Glide.with(this).load(task).override(300,300).apply(RequestOptions.circleCropTransform()).into(profile_iv_photo)
            }
        }catch (e: Exception){
            e.printStackTrace()
            Glide.with(this).load(image).override(300,300).apply(RequestOptions.circleCropTransform()).into(profile_iv_photo)
        }

        Glide.with(this).load(image).override(300,300).apply(RequestOptions.circleCropTransform()).into(profile_iv_photo)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile_iv_photo ->{
                var openGalleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(openGalleryIntent,1000)

            }
            R.id.settings_btn_change_profile -> {
                updateProfile()
                mainFragment = MainFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, mainFragment)
                transaction.commit()
            }

        }
    }

    private fun updateProfile() {
        val user = auth?.currentUser
        val name = profile_et_name.text.toString()

        if(name.isNotEmpty()) {
            val profile_name_Updates = UserProfileChangeRequest.Builder()
                    .setDisplayName("$name")
                    .build()
            user!!.updateProfile(profile_name_Updates)
            var map = HashMap<String,Any>()
            map["name"]=name
            FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child("$uid")
                    .updateChildren(map)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            println(imageUri)
            uploadImage()
        }
    }

    private fun uploadImage(){
        if (imageUri != null){

            var uploadTask: StorageTask<*>
            uploadTask = fileref.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> Contiuation@{ task ->
                if(!task.isSuccessful ){
                    task.exception?.let{
                        throw it
                    }
                }
                return@Contiuation fileref.downloadUrl
            }).addOnCompleteListener {task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    val map = HashMap<String, Any>()
                    map["photo"] = url
                    //userReference!!.updateChildren(map)
                    FirebaseDatabase.getInstance()
                            .reference
                            .child("users")
                            .child("$uid")
                            .updateChildren(map)

                    fileref.downloadUrl.addOnSuccessListener { task ->
                        Glide.with(this).load(task).override(300,300).apply(RequestOptions.circleCropTransform()).into(profile_iv_photo)
                     //   Glide.with(mainFragment).load(task).override(300,300).apply(RequestOptions.circleCropTransform()).into(user_iv_photo)
                     //   user_iv_photo.setImageResource(task);
                    }
                }
            }
        }
    }
}

