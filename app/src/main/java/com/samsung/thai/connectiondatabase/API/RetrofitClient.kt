package com.samsung.thai.connectiondatabase.API

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {
    //localhost
        private const val BASE_URL = "http://192.168.7.114:3000/"
    //server
//    private const val BASE_URL = "http://107.101.4.199:3000/"
    val instance: Api by lazy{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(Api::class.java)
    }

}