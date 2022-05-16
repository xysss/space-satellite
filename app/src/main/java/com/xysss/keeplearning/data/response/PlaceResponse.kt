package com.xysss.keeplearning.data.response

import com.google.gson.annotations.SerializedName

class PlaceResponse(val status: String, val places: List<Place>)
//SerializedName  注解对应json 返回字段名称
class Place(val name: String, val location: Location, @SerializedName("formatted_address") val address: String)

class Location(val lng: String, val lat: String)
