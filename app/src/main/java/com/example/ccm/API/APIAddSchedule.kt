package com.example.ccm.API

import retrofit2.Call
import retrofit2.http.*

interface ApiAddSchedule {
    @Headers("Content-Type: application/json")
    @POST("/schedule")
    fun postAddSchedule(
        @Header("AccessToken") AccessToken: String,
        @Body param : AddScheduleJSON
    ): Call<GetScheduleJSON>
}