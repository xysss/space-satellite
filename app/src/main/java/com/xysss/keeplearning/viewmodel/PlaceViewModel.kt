package com.xysss.keeplearning.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xysss.keeplearning.data.repository.WeatherRepository
import com.xysss.keeplearning.data.response.Place
import com.xysss.mvvmhelper.base.BaseViewModel

/**
 * 作者 : xys
 * 时间 : 2022-05-16 11:22
 * 描述 : 描述
 */
class PlaceViewModel : BaseViewModel(){

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