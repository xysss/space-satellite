package com.xysss.keeplearning.data.repository

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.xysss.keeplearning.data.response.Place

import com.xysss.mvvmhelper.base.appContext

object PlaceDao {

    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() =
        appContext.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)

}