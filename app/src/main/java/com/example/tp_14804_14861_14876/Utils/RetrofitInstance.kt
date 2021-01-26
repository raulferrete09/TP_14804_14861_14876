package com.example.tp_14804_14861_14876.Utils

import com.example.tp_14804_14861_14876.Utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
    This class with base_url and with some Gson Converter, we can build our interface API
    with retrofit variable

 */
class RetrofitInstance {

    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}