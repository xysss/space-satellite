package com.xysss.keeplearning.data.response
import com.google.gson.annotations.SerializedName

class RealtimeResponse(val status: String, val result: Result) {

    class Result(val realtime: Realtime)

    class Realtime(val skycon: String, val temperature: Float,val humidity :Float,val visibility:Float,
                   @SerializedName("air_quality") val airQuality: AirQuality,
                   @SerializedName("wind") val wind: Wind)

    class AirQuality(val aqi: AQI,val pm25 :Float)

    class Wind(val speed: Float,val direction :Float)

    class AQI(val chn: Float)

}