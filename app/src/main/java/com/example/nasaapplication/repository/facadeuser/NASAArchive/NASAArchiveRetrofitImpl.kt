package com.example.nasaapplication.repository.facadeuser.NASAArchive

import com.example.nasaapplication.Constants
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class NASAArchiveRetrofitImpl {
    fun getRetrofitImpl(): NASAArchiveAPI {
        val nasaArchiveRetrofit = Retrofit.Builder()
            .baseUrl(Constants.NASA_ARCHIVE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(createOkHttpClient(NASAArchiveInterceptor()))
            .build()
        return nasaArchiveRetrofit.create(NASAArchiveAPI::class.java)
    }

    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }

    inner class NASAArchiveInterceptor: Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            return chain.proceed(chain.request())
        }
    }
}