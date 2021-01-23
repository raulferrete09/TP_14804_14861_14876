package com.example.tp_14804_14861_14876.Utils

import com.example.tp_14804_14861_14876.Utils.Constants.Companion.CONTENT_TYPE
import com.example.tp_14804_14861_14876.Utils.Constants.Companion.SERVER_KEY
import com.google.android.gms.common.api.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization:key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification

    ): retrofit2.Response<ResponseBody>
}