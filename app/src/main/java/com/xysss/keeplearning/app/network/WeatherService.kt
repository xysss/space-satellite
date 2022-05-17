package com.xysss.keeplearning.app.network
import com.xysss.keeplearning.app.App
import com.xysss.keeplearning.data.response.DailyResponse
import com.xysss.keeplearning.data.response.RealtimeResponse
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Path

interface WeatherService {

    @GET("v2.6/${App.WeatherToken}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<RealtimeResponse>

    @GET("v2.6/${App.WeatherToken}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<DailyResponse>

}