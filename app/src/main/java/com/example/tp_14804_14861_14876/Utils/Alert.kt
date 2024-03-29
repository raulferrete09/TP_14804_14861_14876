package com.example.tp_14804_14861_14876.Utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity

/*
Class created to show a alert in case of problems on the app
 */
class Alert: AppCompatActivity() {
    fun showAlert(builder: AlertDialog.Builder, context: Context){
        var choose:Int = 0
        builder.setTitle("No internet connection")
        builder.setMessage("Please choose the type of internet connection you want.")
        builder.setCancelable(false)
        builder.setPositiveButton("Wifi") { dialogInterface: DialogInterface, i: Int ->
            try {
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }catch (e:NullPointerException){
                e.printStackTrace()
            }
        }
        builder.setNegativeButton("Mobile Data") { dialogInterface: DialogInterface, i: Int ->
            try {
                context.startActivity(Intent(Settings.ACTION_DATA_USAGE_SETTINGS))
            }catch (e:NullPointerException){
                e.printStackTrace()
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
