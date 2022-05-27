package com.example.ccm.API

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface APIGetSchedule {
    @GET("/schedule/my")
    fun getMySchedule(
        @Header("AccessToken") AccessToken: String
    ): Call<GetSchedulesJSON>
}