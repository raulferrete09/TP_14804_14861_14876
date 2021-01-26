package com.example.tp_14804_14861_14876.Activitys

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class View_PDF_Files_Activity : AppCompatActivity() {


    lateinit var myPDFListView: ListView
    var databaseReference: DatabaseReference? = null
    var uploadPDFS: ArrayList<uploadPDF>? = null
    var uploadPDFS1: ArrayList<uploadPDF>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view__pdf_files)
        myPDFListView = findViewById<ListView>(R.id.myListView)
        uploadPDFS = ArrayList()
        uploadPDFS1 = ArrayList()
        viewAllFiles()


        myPDFListView.setOnItemClickListener { parent, view, position, id ->
            println(position)
            println(uploadPDFS1)
            println(uploadPDFS)
            val uploadPDF = uploadPDFS!![position]
            val intent = Intent()
            intent.type = Intent.ACTION_VIEW
            intent.data = Uri.parse(uploadPDF.getUrl())
            startActivity(intent)
        }
    }

    private fun viewAllFiles() {
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads")
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                uploadPDFS = ArrayList()
                for (postSnapshot in snapshot.children) {
                    val uploadPDF = postSnapshot.getValue(uploadPDF::class.java)
                    uploadPDFS!!.add(uploadPDF!!)
                }
                val uploads = arrayOfNulls<String>(uploadPDFS!!.size)
                for (i in uploads.indices) {
                    uploads[i] = uploadPDFS!![i]!!.getName()
                }
                val adapter: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
                    applicationContext, android.R.layout.simple_list_item_1, uploads
                ) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getView(position, convertView, parent)
                        val mytext = view.findViewById<View>(android.R.id.text1) as TextView
                        mytext.setTextColor(Color.BLACK)
                        return super.getView(position, convertView, parent)
                    }
                }
                myPDFListView!!.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}