package com.xysss.keeplearning.app.network
import com.xysss.keeplearning.app.App
import com.xysss.keeplearning.data.response.DailyResponse
import com.xysss.keeplearning.data.response.RealtimeResponse
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {

    @GET("v2.6/${App.WeatherToken}/{lng},{lat}/realtime")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<RealtimeResponse>

    @GET("v2.6/${App.WeatherToken}/{lng},{lat}/daily")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String, @Query("dailysteps") dailySteps: Int =7): Call<DailyResponse>

}