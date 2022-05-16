package com.xysss.keeplearning.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.xysss.keeplearning.data.repository.Repository
import com.xysss.keeplearning.data.repository.WeatherRepository
import com.xysss.keeplearning.data.response.Location
import com.xysss.mvvmhelper.base.BaseViewModel

/**
 * 作者 : xys
 * 时间 : 2022-05-13 14:57
 * 描述 : 描述
 */
class WeatherViewModel :BaseViewModel(){

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

//    fun login(phoneNumber: String, password: String) {
//        rxHttpRequest {
//            onRequest = {
//                loginData.value = UserRepository.login(phoneNumber,password).await()
//            }
//            loadingType = LoadingType.LOADING_DIALOG //选传
//            loadingMessage = "正在登录中....." // 选传
//            requestCode = NetUrl.LOGIN // 如果要判断接口错误业务 - 必传
//        }
//    }


}