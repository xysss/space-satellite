package com.xysss.keeplearning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.serial.port.manage.data.WrapReceiverData
import com.xysss.keeplearning.data.repository.WeatherRepository
import com.xysss.keeplearning.data.response.Location
import com.xysss.keeplearning.data.response.MaterialInfo
import com.xysss.keeplearning.data.response.Place
import com.xysss.keeplearning.serialport.ProtocolAnalysis
import com.xysss.keeplearning.serialport.SerialPortHelper
import com.xysss.keeplearning.serialport.model.Msg41DataModel
import com.xysss.mvvmhelper.base.BaseViewModel

/**
 * 作者 : xys
 * 时间 : 2022-05-20 16:37
 * 描述 : 描述
 */
class MainActivityViewModel :BaseViewModel(){

    private val locationLiveData = MutableLiveData<Location>()
    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    val weatherLiveData = Transformations.switchMap(locationLiveData) { location ->
        WeatherRepository.refreshWeather(location.lng, location.lat, placeName)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
    }

    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        WeatherRepository.searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    fun savePlace(place: Place) = WeatherRepository.savePlace(place)

    fun getSavedPlace() = WeatherRepository.getSavedPlace()

    fun isPlaceSaved() = WeatherRepository.isPlaceSaved()
}