package com.example.nasaapplication.repository.facadeuser.NASAArchive

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NASAArchiveAPI {
    @GET("search")
    fun getNASAArchive(
        // encoded = true нужен для использования в запросе нескольких слов
        // (корректная работа с пробелами)
        @Query("q", encoded = true) question: String,
        @Query("media_type") mediaType: String
    ): Call<NASAArchiveServerResponseWelcome>
}