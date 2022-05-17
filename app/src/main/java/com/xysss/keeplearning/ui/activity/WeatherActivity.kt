package com.xysss.keeplearning.ui.activity

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseActivity
import com.xysss.keeplearning.data.response.Weather
import com.xysss.keeplearning.data.response.getSky
import com.xysss.keeplearning.databinding.ActivityWeatherBinding
import com.xysss.keeplearning.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * 作者 : xys
 * 时间 : 2022-05-16 11:34
 * 描述 : 描述
 */
class WeatherActivity :BaseActivity<WeatherViewModel,ActivityWeatherBinding>(){
    override fun initView(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }
        if (mViewModel.locationLng.isEmpty()) {
            mViewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (mViewModel.locationLat.isEmpty()) {
            mViewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (mViewModel.placeName.isEmpty()) {
            mViewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        mViewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            mViewBinding.swipeRefresh.isRefreshing = false
        }
        mViewBinding.swipeRefresh.setColorSchemeResources(R.color.green_577)
        refreshWeather()
        mViewBinding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        mViewBinding.nowInclude.navBtn.setOnClickListener {
            mViewBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
        mViewBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        })
    }

    fun refreshWeather() {
        mViewModel.refreshWeather(mViewModel.locationLng, mViewModel.locationLat)
        mViewBinding.swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        mViewBinding.nowInclude.placeName.text = mViewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        mViewBinding.nowInclude.currentTemp.text = currentTempText
        mViewBinding.nowInclude.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        mViewBinding.nowInclude.currentAQI.text = currentPM25Text
        mViewBinding.nowInclude.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        mViewBinding.forecastInclude.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mViewBinding.forecastInclude.forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            mViewBinding.forecastInclude.forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        mViewBinding.lifeIndexInclude.coldRiskText.text = lifeIndex.coldRisk[0].desc
        mViewBinding.lifeIndexInclude.dressingText.text = lifeIndex.dressing[0].desc
        mViewBinding.lifeIndexInclude.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        mViewBinding.lifeIndexInclude.carWashingText.text = lifeIndex.carWashing[0].desc
        mViewBinding.weatherLayout.visibility = View.VISIBLE
    }

//    private fun hideSystemUI() {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }
//
//    private fun showSystemUI() {
//        WindowCompat.setDecorFitsSystemWindows(window, true)
//        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
//    }

}
