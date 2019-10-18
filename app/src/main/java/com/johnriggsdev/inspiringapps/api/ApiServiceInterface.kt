package com.johnriggsdev.inspiringapps.api

import com.johnriggsdev.inspiringapps.utils.Constants.Companion.BASE_URL
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.DIRECTORY
import com.johnriggsdev.inspiringapps.utils.Constants.Companion.FILE_NAME
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming

interface ApiServiceInterface {
    @Streaming
    @GET("$DIRECTORY$FILE_NAME")
    fun getLogFromApi(): Call<ResponseBody>

    companion object {
        fun create(): ApiServiceInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApiServiceInterface::class.java)
        }
    }
}