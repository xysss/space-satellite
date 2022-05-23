package com.xysss.keeplearning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xysss.keeplearning.data.response.MaterialInfo
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


}