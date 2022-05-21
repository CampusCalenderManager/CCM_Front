package com.example.ccm

import retrofit2.Call
import retrofit2.http.*

interface ApiAddSchedule {
    @FormUrlEncoded

    @Headers("Content-Type: application/json")
    @POST("/schedule")
    fun postAddSchedule(
        @Header("AccessToken") AccessToken: String,
        @Field("title") title: String,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String,
        @Field("startAlarm") startAlarm: String,
        @Field("endAlarm") endAlarm: String,
        @Field("isShared") isShared: String,
        @Field("color") color: String,
        @Field("organizationId") organizationId: String,
    ): Call<AddScheduleJson>

}