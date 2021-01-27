package com.example.tp_14804_14861_14876.Notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

/*
Class for a Service that will send the notification information
 */
class ServiceNotification : Service() {

    val TAG = "Service"
    var auth : FirebaseAuth? = null

    private val listenerAudio = object: ChildEventListener {

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            auth = FirebaseAuth.getInstance()
            val user = auth?.currentUser
            if(snapshot.key == "status" && snapshot.value != "") {
                val title = "ANOMALY"
                val message = "Audio anomaly found"

                if (user != null){
                    PushNotification(
                        NotificationData(title, message),
                        TOPIC
                    ).also {
                        sendNotification(it)
                   }
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private val listenerAccelerometer = object: ChildEventListener {

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            auth = FirebaseAuth.getInstance()
            val user = auth?.currentUser
            if(snapshot.key == "status" && snapshot.value != "OK") {
                val title = "ANOMALY"
                val message = "Accelerometer anomaly found"

                if (user != null){
                    PushNotification(
                        NotificationData(title, message),
                        TOPIC
                    ).also {
                        sendNotification(it)
                    }
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    private val listenerTemperature = object: ChildEventListener {

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            auth = FirebaseAuth.getInstance()
            val user = auth?.currentUser
            if(snapshot.key == "status" && snapshot.value != "OK") {
                val title = "ANOMALY"
                val message = "Temperature anomaly found"

                if (user != null){
                    PushNotification(
                        NotificationData(title, message),
                        TOPIC
                    ).also {
                        sendNotification(it)
                    }
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }
    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().reference.child("Audio/MOne/").addChildEventListener(listenerAudio)
        FirebaseDatabase.getInstance().reference.child("Audio/MTwo/").addChildEventListener(listenerAudio)
        FirebaseDatabase.getInstance().reference.child("Audio/MThree/").addChildEventListener(listenerAudio)
        FirebaseDatabase.getInstance().reference.child("Audio/MFour/").addChildEventListener(listenerAudio)
        FirebaseDatabase.getInstance().reference.child("Audio/MFive/").addChildEventListener(listenerAudio)

        FirebaseDatabase.getInstance().reference.child("Accelerometer/MOne/").addChildEventListener(listenerAccelerometer)
        FirebaseDatabase.getInstance().reference.child("Accelerometer/MTwo/").addChildEventListener(listenerAccelerometer)
        FirebaseDatabase.getInstance().reference.child("Accelerometer/MThree/").addChildEventListener(listenerAccelerometer)
        FirebaseDatabase.getInstance().reference.child("Accelerometer/MFour/").addChildEventListener(listenerAccelerometer)
        FirebaseDatabase.getInstance().reference.child("Accelerometer/MFive/").addChildEventListener(listenerAccelerometer)

        FirebaseDatabase.getInstance().reference.child("Temperature/MOne/").addChildEventListener(listenerTemperature)
        FirebaseDatabase.getInstance().reference.child("Temperature/MTwo/").addChildEventListener(listenerTemperature)
        FirebaseDatabase.getInstance().reference.child("Temperature/MThree/").addChildEventListener(listenerTemperature)
        FirebaseDatabase.getInstance().reference.child("Temperature/MFour/").addChildEventListener(listenerTemperature)
        FirebaseDatabase.getInstance().reference.child("Temperature/MFive/").addChildEventListener(listenerTemperature)


    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // If we get killed, after returning from here, restart
        return START_STICKY
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
}