package com.example.ccm.API

import retrofit2.Call
import retrofit2.http.*

interface APICreateGroup {
    //@FormUrlEncoded

    @Headers("Content-Type: application/json")
    @POST("/organization")
    fun postCreateGroup(
        @Header("AccessToken") AccessToken: String,
        @Body param : CreateGroupJSON
    ): Call<CreateGroupCode>
}