package com.example.ccm.API

import retrofit2.Call
import retrofit2.http.*

interface APIJoinGroup {
    //@FormUrlEncoded

    @Headers("Content-Type: application/json")
    @POST("/organization/member")
    fun postJoinGroup(
        @Header("AccessToken") AccessToken: String,
        @Body param : JoinGroupJSON
    ): Call<Void>
}