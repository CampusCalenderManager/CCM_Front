package com.example.ccm.API

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface APIGetMyOrganization {
    @GET("/organization/my")
    fun getMyOrganization(
        @Header("AccessToken") AccessToken: String
    ): Call<MyOrganizationJSON>
}