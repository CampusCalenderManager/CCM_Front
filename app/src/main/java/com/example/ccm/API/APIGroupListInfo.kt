package com.example.ccm.API

import retrofit2.Call
import retrofit2.http.*

interface APIGroupListInfo {
    //@FormUrlEncoded

    @Headers("Content-Type: application/json")
    @GET("/organization/my")
    fun getGroupListInfo(
        @Header("AccessToken") AccessToken: String,
    ): Call<GroupListInfoJSON>

}