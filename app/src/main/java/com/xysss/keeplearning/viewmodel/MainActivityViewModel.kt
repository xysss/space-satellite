package com.xysss.keeplearning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xysss.keeplearning.data.repository.WeatherRepository
import com.xysss.keeplearning.data.response.Location
import com.xysss.keeplearning.data.response.MaterialInfo
import com.xysss.keeplearning.data.response.Place
import com.xysss.mvvmhelper.base.BaseViewModel

/**
 * 作者 : xys
 * 时间 : 2022-05-20 16:37
 * 描述 : 描述
 */
class MainActivityViewModel :BaseViewModel(){

    val bleDate: LiveData<MaterialInfo> get() = _bleDate
    val bleState: LiveData<String> get() = _bleState
    val progressNum: LiveData<Int> get() = _progressNum
    val numShow: LiveData<String> get() = _numShow

    private val _bleDate= MutableLiveData<MaterialInfo>()
    private val _bleState= MutableLiveData<String>()
    private val _progressNum= MutableLiveData<Int>()
    private val _numShow= MutableLiveData<String>()

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