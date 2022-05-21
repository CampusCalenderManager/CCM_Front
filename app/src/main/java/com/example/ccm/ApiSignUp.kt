package com.example.ccm

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiSignUp {
    //@FormUrlEncoded

    @Headers("Content-Type: application/json")
    @POST("/signup")
    fun postSignUp(
        @Header("AccessToken") AccessToken: String,
        @Body param : SignUpJson
    ): Call<SignUpJson>

}