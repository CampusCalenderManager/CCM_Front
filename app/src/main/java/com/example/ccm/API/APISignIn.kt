package com.example.ccm.API

import com.example.ccm.SignUpJson
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APISignIn {
    @Headers("Content-Type: application/json")
    @POST("/login")
    fun postSignIn (
        @Body body: SignInJSON
    ): Call<SignInJSON>
}