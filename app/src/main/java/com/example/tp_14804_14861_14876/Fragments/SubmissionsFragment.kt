package com.example.tp_14804_14861_14876.Fragments

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SubmissionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubmissionsFragment : Fragment(), View.OnClickListener, OnItemSelectedListener {

    lateinit var profile_iv_photo: ImageView
    lateinit var profile_tv_name: TextView
    lateinit var profile_tv_id: TextView
    lateinit var report_ed_anomaly: EditText
    lateinit var submission_btn_firebase: Button
    lateinit var submission_spinner_machine: Spinner
    lateinit var submission_spinner_intervation: Spinner
    lateinit var submission_tv_machine: TextView
    lateinit var submission_tv_intervation: TextView
    lateinit var submission_iv_addphoto: ImageView
    lateinit var submission_iv_addaudio: ImageView

    lateinit var mainFragment: MainFragment
    lateinit var transaction: FragmentTransaction

    lateinit var adpater_number: ArrayAdapter<CharSequence>
    lateinit var adpater_intervation: ArrayAdapter<CharSequence>

    var auth : FirebaseAuth? = null
    lateinit var name: String
    lateinit var email: String
    lateinit var uid: String
    var count: Int = 0
    var sound_count: Int = 0
    var selector = 0
    private var images: ArrayList<Uri?>? = null
    private var audios: ArrayList<Uri?>? = null
    lateinit var fileref: StorageReference
    lateinit var ImageRef: StorageReference
    private var storageReference: StorageReference? = null
    private var Image_storageReference: StorageReference? = null
    var photo_name: String = ""
    var sound_name: String = ""
    lateinit var url:String
    var url_sound = ""
    lateinit var doc:Document
    var lista_images = ArrayList<String>()
    var lista_sounds = ArrayList<String>()
    var photo_url_check = 0
    var sound_url_check = 0
    lateinit var pathname: String
    lateinit var timeStamp:String
    lateinit var text_spinner_machine: String
    lateinit var text_spinner_intervation: String
    lateinit var text_reportanomaly: String

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        images = ArrayList()
        audios = ArrayList()
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submissions, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SubmissionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubmissionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_tv_id = view.findViewById<TextView>(R.id.profile_tv_id)
        profile_tv_name = view.findViewById<TextView>(R.id.profile_tv_name)
        profile_iv_photo = view.findViewById<ImageView>(R.id.profile_iv_photo)
        submission_spinner_machine = view.findViewById<Spinner>(R.id.submission_spinner_machine)
        submission_spinner_intervation = view.findViewById<Spinner>(R.id.submission_spinner_intervation)
        submission_tv_machine = view.findViewById<TextView>(R.id.submission_tv_machine)
        submission_tv_intervation = view.findViewById<TextView>(R.id.submission_tv_intervation)
        submission_iv_addphoto = view.findViewById<ImageView>(R.id.submission_iv_addphoto)
        submission_iv_addaudio = view.findViewById<ImageView>(R.id.submission_iv_addaudio)

        report_ed_anomaly = view.findViewById<EditText>(R.id.report_ed_anomaly)
        submission_btn_firebase = view.findViewById<Button>(R.id.submission_btn_firebase)

        adpater_number = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.numbers,
            android.R.layout.simple_spinner_item
        )
        adpater_number.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        submission_spinner_machine.adapter = adpater_number
        submission_spinner_machine.onItemSelectedListener = this

        adpater_intervation = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.type_of_intervation,
            android.R.layout.simple_spinner_item
        )
        adpater_intervation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        submission_spinner_intervation.adapter = adpater_intervation
        submission_spinner_intervation.onItemSelectedListener = this


        report_ed_anomaly.setOnClickListener(this)
        submission_btn_firebase.setOnClickListener(this)
        submission_iv_addphoto.setOnClickListener(this)
        submission_iv_addaudio.setOnClickListener(this)

        //Firebase info
        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth?.currentUser
        name = user?.displayName.toString()
        email = user?.email.toString()
        uid = user?.uid.toString()
        var image = user?.photoUrl
        Image_storageReference = FirebaseStorage.getInstance().reference.child("Users Image").child("$uid")
        fileref = Image_storageReference!!.child("picture.jpg")
        storageReference = FirebaseStorage.getInstance()
                .reference
                .child("Reports")
                .child("$uid")

        profile_tv_name.text = name
        profile_tv_id.text = uid

        //get text
        text_spinner_machine = submission_spinner_machine.selectedItem.toString()
        text_spinner_intervation = submission_spinner_intervation.selectedItem.toString()
        text_reportanomaly = report_ed_anomaly.text.toString()

        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        pathname = "Report" + "_" + text_spinner_intervation + "_" + timeStamp

// rounded corner image
        try {
            fileref?.downloadUrl?.addOnSuccessListener { task ->
                Glide.with(this).load(task).override(300,300).apply(RequestOptions.circleCropTransform()).into(profile_iv_photo)
            }
        }catch (e: Exception){
            e.printStackTrace()
            Glide.with(this).load(image).override(300,300).apply(RequestOptions.circleCropTransform()).into(profile_iv_photo)
        }
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.submission_btn_firebase -> {
                if (checkPermissions()) {
                    globallist_photos()
                    globallist_sound()
                    val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + "HVAC" + File.separator + "Reports")
                    if (!folder.exists()) {
                        folder.mkdirs()
                        if (checkInformation()) {
                            //shareData()
                            showAlertConfirmation()
                        } else {
                            showAlertError()
                        }
                    } else {
                        if (checkInformation()) {
                            //shareData()
                            showAlertConfirmation()
                        } else {
                            showAlertError()
                        }
                    }
                }
            }
            R.id.submission_iv_addphoto -> {
                if (checkPermissions()) {
                    selector = 0
                    selectFile(selector)

                }
            }
            R.id.submission_iv_addaudio -> {
                if (checkPermissions()) {
                    selector = 1
                    selectFile(selector)
                }
            }
        }
    }

    private fun selectFile(selector: Int) {
        var intent= Intent(Intent.ACTION_PICK)
        intent.setType("*/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action = Intent.ACTION_GET_CONTENT
        if(selector == 0) {
            startActivityForResult(intent, 1000)
        }else{
            startActivityForResult(intent, 1100)
        }

    }

    private fun checkInformation(): Boolean {
        return submission_spinner_machine.toString().isNotEmpty() && submission_spinner_intervation.toString().isNotEmpty() && report_ed_anomaly.text.isNotEmpty()
    }

    private fun savePDF(email: String, name: String) {
        //create object of Document class
        doc = com.itextpdf.text.Document()
        val data = timeStamp.substring(0, 4) + "/" + timeStamp.substring(4, 6) + "/" + timeStamp.substring(6, 8) + " at " + timeStamp.substring(9,11) + "h" + timeStamp.substring(11,13)
        //val data = timeStamp.substring(0, 4) + "/" + timeStamp.substring(5, 6) + "/" + timeStamp.substring(7, 8) + " at " + timeStamp.substring(9,10) + ":" + timeStamp.substring(11,12)
        var path = Environment.getExternalStorageDirectory().toString() + "/" + "HVAC/Reports/" + pathname + ".pdf"

        try {
            //create instance of PDFWriter class
            PdfWriter.getInstance(doc, FileOutputStream(path))
            //open the document for writing
            doc.open()

            val font_title: Font = Font(FontFactory.getFont(FontFactory.TIMES_BOLD, 20.0f))
            val font_text: Font = Font(FontFactory.getFont(FontFactory.TIMES_BOLD, 14.0f))
            val title = Chunk("Intervention report of $data", font_title)
            val text_machine = Chunk("Machine", font_text)
            val text_intervation = Chunk("Type of Intervation", font_text)
            val text_report = Chunk("Report", font_text)
            val text_photos = Chunk("Photos", font_text)
            val text_audio = Chunk("Audios", font_text)
            val text_name = Chunk("Name", font_text)
            val text_email = Chunk("Email", font_text)

            //create the pdf structure
            doc.addAuthor("HVAC")
            doc.add(Paragraph(Chunk(title)))
            doc.add(Paragraph(" "))
            doc.add(Paragraph(" "))
            doc.add(Paragraph(" "))
            doc.add(Paragraph(Chunk(text_name)))
            doc.add(Paragraph(name))
            doc.add(Paragraph(Chunk(text_email)))
            doc.add(Paragraph(email))
            doc.add(Paragraph(" "))
            doc.add(Paragraph(" "))
            doc.add(Paragraph(Chunk(text_machine)))
            doc.add(Phrase(text_spinner_machine))
            doc.add(Paragraph(Chunk(text_intervation)))
            doc.add(Phrase(text_spinner_intervation))
            doc.add(Paragraph(" "))
            doc.add(Paragraph(" "))
            doc.add(Paragraph(Chunk(text_report)))
            doc.add(Paragraph(text_reportanomaly))
            doc.add(Paragraph(" "))
            doc.add(Paragraph(" "))
            doc.add(Paragraph(Chunk(text_photos)))
            //add uri photos
            for(i in 0 until count){
                var url_photo = lista_images[i]
                var anchor = Anchor(url_photo)
                anchor.reference=url_photo
                doc.add(anchor)
                doc.add(Paragraph(" "))
            }
            doc.add(Paragraph(" "))
            //add uri videos
            doc.add(Paragraph(Chunk(text_audio)))
            for(i in 0 until sound_count){
                var url_sound = lista_sounds[i]
                var anchor = Anchor(url_sound)
                anchor.reference=url_sound
                doc.add(anchor)
                doc.add(Paragraph(" "))
            }
            doc.close()

            var savePDF = storageReference!!
                    .child("$pathname")
                    .child("PDF")
                    .child("$pathname")

            var pathpdf = Uri.fromFile(File(path))
            if (pathpdf != null) {
                savePDF.putFile(pathpdf)
            }


            Toast.makeText(activity, "$pathname.pdf \nsaved to success", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun shareData() {

        val pathFile = File(Environment.getExternalStorageDirectory().toString() + "/" + "HVAC/Reports/" + pathname + ".pdf")
        val pathUri = FileProvider.getUriForFile(requireContext(),
            "com.example.tp_14804_14861_14876.provider",
            pathFile
        )
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "*/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, pathUri)

        startActivity(Intent.createChooser(intent, "Please select app: "))
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val text = parent.getItemAtPosition(position).toString()
        //Toast.makeText(parent.context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            if(data!!.clipData != null){
                //Pick multiple images
                //get number of images
                count = data.clipData!!.itemCount
                for(i in 0 until count){
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    println(imageUri)
                    images!!.add(imageUri)
                }
                sendData_Image()
            }else{
                //Pick single image
                count = 1
                val imageUri = data.data
                images!!.add(imageUri)
                sendData_Image()
            }
        }
        if(requestCode == 1100 && resultCode == Activity.RESULT_OK){
            if(data!!.clipData != null){
                //Pick multiple images
                //get number of images
                sound_count = data.clipData!!.itemCount
                for(i in 0 until sound_count){
                    val soundUri = data.clipData!!.getItemAt(i).uri
                    println(soundUri)
                    audios!!.add(soundUri)
                }
                sendData_Sound()

            }else{
                //Pick single image
                sound_count = 1
                val soundUri = data.data
                audios!!.add(soundUri)
                sendData_Sound()
            }
        }

    }

    private fun globallist_photos(){
        for (i in 0 until count){
            var file = images!![i]
            photo_name = file.toString()

            var file_string_modified = photo_name
            val total_count = photo_name.length
            val total_count_after = file_string_modified.replace("/", "").length
            val numberofslash = total_count - total_count_after
            for (i in 0 until numberofslash) {
                photo_name = photo_name
                photo_name = photo_name.substringAfter("/")
            }
            photo_name=photo_name.replace("%","_")
            val UstorageReference = FirebaseStorage.getInstance()
                    .reference
                    .child("Reports")
                    .child("$uid")
                    .child("$pathname")
                    .child("Photos")
            ImageRef = UstorageReference!!.child("$photo_name")
            uploadImage(file, ImageRef,count)
        }
    }

    private fun globallist_sound(){
        for (i in 0 until sound_count){
            var file = audios!![i]

            var file_string = file.toString()
            var file_string_modified = file_string
            val total_count=file_string.length
            val total_count_after = file_string_modified.replace("/","").length
            val numberofslash = total_count - total_count_after
            sound_name = file_string
            // Split the string only to obtain the name of the file
            for (i in 0 until numberofslash){
                sound_name = sound_name
                sound_name = sound_name.substringAfter("/")
            }
            sound_name=sound_name.replace("%","_")
            val UstorageReference = FirebaseStorage.getInstance()
                    .reference
                    .child("Reports")
                    .child("$uid")
                    .child("$pathname")
                    .child("Audios")
                ImageRef = UstorageReference!!.child("$sound_name")
            uploadSound(file, ImageRef,sound_count)
        }
    }

    private fun sendData_Image() {
        //Image
        for(i in 0 until 2){
            for (i in 0 until count) {
                var file = images!![i]
                var file_string = file.toString()
                var file_string_modified = file_string
                val total_count = file_string.length
                val total_count_after = file_string_modified.replace("/", "").length
                val numberofslash = total_count - total_count_after
                photo_name = file_string
                // Split the string only to obtain the name of the file
                for (i in 0 until numberofslash) {
                    photo_name = photo_name
                    photo_name = photo_name.substringAfter("/")
                }
                photo_name = photo_name.replace("%", "_")
                println(photo_name)
                var fileaudio = storageReference!!
                        .child("$pathname")
                        .child("Photos")
                        .child("$photo_name")
                if (file != null) {
                    fileaudio.putFile(file)
                }
            }
        }
    }

    private fun sendData_Sound(){
        //Sounds
        for (i in 0 until 1) {
            for (i in 0 until sound_count) {
                var file_sound = audios!![i]

                var file_sound_string = file_sound.toString()
                sound_name = file_sound_string
                val total_count = file_sound_string.length
                val total_count_after = file_sound_string.replace("/", "").length
                val numberofslash = total_count - total_count_after

                // Split the string only to obtain the name of the file
                for (i in 0 until numberofslash) {
                    sound_name = sound_name
                    sound_name = sound_name.substringAfter("/")
                }
                sound_name = sound_name.replace("%", "_")
                println(sound_name)
                var fileaudio = storageReference!!
                        .child("$pathname")
                        .child("Audios")
                        .child("$sound_name")
                if (file_sound != null) {
                    fileaudio.putFile(file_sound)
                }
            }
        }
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //Permission Granted
            return true
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(
                    permission.WRITE_EXTERNAL_STORAGE
                ),
                111
            )
            return false
        }
    }

    fun uploadImage(File_Photo_Sound: Uri?,fileref: StorageReference, count: Int){

        if (File_Photo_Sound != null){

            fileref.downloadUrl.addOnSuccessListener{ task ->
                url = task.toString()+".html"
                lista_images.add(url)
                if (lista_images.size == count){
                    //Chamar função que usa os links
                    photo_url_check = 1
                }

            }

        }else{
            url=""
        }
    }

    fun uploadSound(File_Photo_Sound: Uri?,fileref: StorageReference, count: Int){

        if (File_Photo_Sound != null){

            fileref.downloadUrl.addOnSuccessListener{ task ->
                url_sound = task.toString()+".html"
                lista_sounds.add(url_sound)
                if (lista_sounds.size == count){
                    sound_url_check = 1
                }
            }

        }else{
            url=""
        }
    }

    fun saveAllPDF() {
        var saveallpdf = FirebaseStorage.getInstance().reference.child("Reports").child("$uid").child("$pathname").child("PDF").child("$pathname")
        var database = FirebaseDatabase.getInstance().reference.child("Reports").child("$pathname")
        saveallpdf.downloadUrl.addOnSuccessListener { task ->
            var url_pdf = task.toString()
            var map = mutableMapOf<String, Any?>()
            map["name"] = pathname
            map["url"] = url_pdf
            database.updateChildren(map)
        }
    }

    fun showAlertConfirmation(){
        var choose:Int = 0
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("File Share")
        builder.setMessage("Please confirm your submission.")
        builder.setCancelable(false)
        builder.setPositiveButton("Confirm") { dialogInterface: DialogInterface, i: Int ->
            try {
                savePDF(email, name)
                val splashtime: Long = 1000
                Handler().postDelayed({
                    saveAllPDF()
                },splashtime)
                shareData()
                mainFragment = MainFragment()
                transaction = fragmentManager?.beginTransaction()!!
                transaction.replace(R.id.drawable_frameLayout, mainFragment)
                transaction.commit()
            }catch (e:NullPointerException){
                e.printStackTrace()
            }
        }
        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            try {
                Toast.makeText(activity,"Submission Cancelled ",Toast.LENGTH_SHORT).show()
            }catch (e:NullPointerException){
                e.printStackTrace()
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    fun showAlertError(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage("Please detail the problem. ")
        builder.setPositiveButton("Accept",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

}

