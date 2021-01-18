package com.samsung.thai.connectiondatabase.API

import com.samsung.thai.connectiondatabase.Models.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {
    @FormUrlEncoded
    @POST("userlogin")
    fun userLogin(
        @Field("empno") empNo:String,
    ):Call<List<LoginResponse>>
}