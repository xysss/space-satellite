package com.xysss.keeplearning.app.network

import com.xysss.keeplearning.app.App
import com.xysss.keeplearning.data.response.PlaceResponse
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Query


interface PlaceService{
    @GET("v2/place?token=${App.WeatherToken}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
}