package com.example.nasaapplication.repository.facadeuser.POD

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PODAPI {
    @GET("planetary/apod")
    fun getPictureOfTheDay(
        @Query("date") curDate: String,
        @Query("api_key") apiKey: String
    ): Call<PODServerResponseData>
}