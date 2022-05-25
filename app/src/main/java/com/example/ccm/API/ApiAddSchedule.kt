package com.example.ccm.API

import com.example.ccm.AddScheduleJson
import retrofit2.Call
import retrofit2.http.*

interface ApiAddSchedule {
    //@FormUrlEncoded

    @Headers("Content-Type: application/json")
    @POST("/schedule")
    fun postAddSchedule(
        @Header("AccessToken") AccessToken: String,
        @Body param : AddScheduleJson
        /*@Body title: String,
        @Body startDate: String,
        @Body endDate: String,
        @Body startAlarm: String,
        @Body endAlarm: String,
        @Body isShared: String,
        @Body color: String,
        @Body organizationId: String,*/
    ): Call<AddScheduleJson>

}