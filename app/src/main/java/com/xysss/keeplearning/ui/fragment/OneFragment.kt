package com.xysss.keeplearning.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.serial.port.kit.core.common.TypeConversion
import com.serial.port.manage.data.WrapReceiverData
import com.serial.port.manage.data.WrapSendData
import com.serial.port.manage.listener.OnDataReceiverListener
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.app.ext.logFlag
import com.xysss.keeplearning.data.response.Weather
import com.xysss.keeplearning.data.response.getSky
import com.xysss.keeplearning.databinding.FragmentOneBinding
import com.xysss.keeplearning.serialport.SenderManager
import com.xysss.keeplearning.serialport.SerialPortHelper
import com.xysss.keeplearning.ui.adapter.PlaceAdapter
import com.xysss.keeplearning.viewmodel.MainActivityViewModel
import com.xysss.mvvmhelper.ext.logE
import com.xysss.mvvmhelper.ext.setOnclickNoRepeat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author:bysd-2
 * Time:2021/9/2811:15
 */

class OneFragment : BaseFragment<MainActivityViewModel, FragmentOneBinding>(){
    private var downloadApkPath = ""
    private lateinit var adapter: PlaceAdapter

    override fun initView(savedInstanceState: Bundle?) {
        //bugly进入首页检查更新
        //Beta.checkUpgrade(false, true)

        //请求权限
        requestCameraPermissions()

        val layoutManager = LinearLayoutManager(mActivity)
        mViewBinding.recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, mViewModel.placeList)
        mViewBinding.recyclerView.adapter = adapter
        mViewBinding.swipeRefresh.setColorSchemeResources(R.color.green_577)

        val place = mViewModel.getSavedPlace()
        mViewModel.locationLng =place.location.lng
        mViewModel.locationLat =place.location.lat
        mViewModel.placeName =place.name


        refreshWeather()

        mViewBinding.searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                mViewModel.searchPlaces(content)
            } else {
                mViewBinding.recyclerView.visibility = View.GONE
                mViewBinding.bgImageView.visibility = View.VISIBLE
                mViewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        mViewBinding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        mViewBinding.drawerLayoutOne.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        mViewBinding.placeName.text = mViewModel.placeName+"今日天气"
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中数据
        //温度
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        mViewBinding.humidityOne.text = currentTempText
        //湿度
        val humidityText = "湿度: ${realtime.humidity} %"
        mViewBinding.tV2One.text = humidityText
        //风速
        val windSpeedText = "风速: ${realtime.wind.speed}公里/每小时"
        mViewBinding.tV4One.text = windSpeedText
        //能见度
        val visibilityText="能见度: ${realtime.visibility}公里"
        mViewBinding.tV3One.text = visibilityText
        //天气
        val skyconText="${realtime.skycon}"
        val sky1 = getSky(skyconText)
        mViewBinding.image1One.setImageResource(sky1.icon)

        //mViewBinding.nowInclude.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        //mViewBinding.nowInclude.currentAQI.text = currentPM25Text
        //mViewBinding.nowInclude.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        mViewBinding.forecastInclude.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(mActivity).inflate(R.layout.forecast_item, mViewBinding.forecastInclude.forecastLayout, false)
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
        //mViewBinding.lifeIndexInclude.coldRiskText.text = lifeIndex.coldRisk[0].desc
        //mViewBinding.lifeIndexInclude.dressingText.text = lifeIndex.dressing[0].desc
        //mViewBinding.tV5One.text = "紫外线指数: ${lifeIndex.ultraviolet[0].index}最大(10)"
        mViewBinding.tV5One.text = "紫外线指数: ${lifeIndex.ultraviolet[0].desc}"
        //mViewBinding.lifeIndexInclude.carWashingText.text = lifeIndex.carWashing[0].desc
        mViewBinding.weatherLayout.visibility = View.VISIBLE
    }

    override fun initObserver() {
        super.initObserver()

        mViewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                ToastUtils.showShort("无法成功获取天气信息")
                result.exceptionOrNull()?.printStackTrace()
            }
            mViewBinding.swipeRefresh.isRefreshing = false
        }

        mViewModel.placeLiveData.observe(this){ result->
            val places = result.getOrNull()
            if (places != null) {
                mViewBinding.recyclerView.visibility = View.VISIBLE
                mViewBinding.bgImageView.visibility = View.GONE
                mViewModel.placeList.clear()
                mViewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    /**
     * 请求相机权限
     */
    @SuppressLint("CheckResult")
    private fun requestCameraPermissions() {
        ToastUtils.showShort("请求权限")
        //请求打开相机权限
        val rxPermissions = RxPermissions(requireActivity())
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE).subscribe { aBoolean ->
                if (aBoolean) {
                    ToastUtils.showShort("权限已经打开")
                } else {
                    ToastUtils.showShort("权限被拒绝")
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewClick() {
        setOnclickNoRepeat(
            mViewBinding.searchPlaceBtn,mViewBinding.swipeRefresh
        ) {
            when (it.id) {
                R.id.searchPlaceBtn->{
                    mViewBinding.drawerLayoutOne.openDrawer(GravityCompat.START)
                }

                R.id.constraint1_one->{
                    // 发送数据
                    SerialPortHelper.portManager.send(
                        WrapSendData(
                            SenderManager.getSender().sendTest()
                        ),
                        object : OnDataReceiverListener {
                            override fun onSuccess(data: WrapReceiverData) {
                                "响应数据：${TypeConversion.bytes2HexString(data.data)}".logE(logFlag)
                            }

                            override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                                "发送数据: ${TypeConversion.bytes2HexString(wrapSendData.sendData)}, $msg".logE(
                                    logFlag
                                )
                            }

                            override fun onTimeOut() {
                                "发送或者接收超时".logE(logFlag)
                            }
                        })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        LiveDataEvent.loginEvent.observe(viewLifecycleOwner, Observer {
//            //登录成功通知
//            "登录成功".toast()
//        })
    }

    override fun onPause() {
        super.onPause()
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
    }

}