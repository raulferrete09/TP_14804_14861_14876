package com.example.tp_14804_14861_14876.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.tp_14804_14861_14876.Activitys.SignUpActivity
import com.example.tp_14804_14861_14876.Activitys.SplashScreenActivity
import com.example.tp_14804_14861_14876.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

const val TOPIC = "/topics/myTopic"
private const val CHANNEL_ID = "my_channel"



class FirebaseService: FirebaseMessagingService() {

    var auth : FirebaseAuth? = null
    /*
    Code if we want to just receive the notification in one device where the user is logged
     */
    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
        get() {
            return sharedPref?.getString("token","")
        }
        set(value) {
            sharedPref?.edit()?.putString("token",value)?.apply()
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }
/*
    Function to create a service for our notification information
    After that, some costum information for the notification, things like:
    - Icon of the project
    - Title of the notification
    - Text of the notification
 */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        auth = FirebaseAuth.getInstance()
        val user = auth?.currentUser
        if (user != null) {
            val intent = Intent(this, SplashScreenActivity::class.java)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationID = Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setSmallIcon(R.drawable.hvac_logo)
                //.setStyle(NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        }
    }
/*
Call API function to prepare the notification channel when called
 */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel((channel))

    }

}