package com.xysss.keeplearning.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xysss.keeplearning.serialport.ProtocolAnalysis
import com.xysss.keeplearning.serialport.model.Msg41DataModel
import com.xysss.mvvmhelper.base.BaseViewModel

/**
 * 作者 : xys
 * 时间 : 2022-06-01 17:51
 * 描述 : 描述
 */
class ShellMainSharedViewModel :BaseViewModel(), ProtocolAnalysis.ReceiveDataCallBack{

    val protocolAnalysis = ProtocolAnalysis()

    val msg41Date: LiveData<Msg41DataModel> get() = _msg41Date
    private val _msg41Date= MutableLiveData<Msg41DataModel>()

    fun setListener(){
        //注册回调
        protocolAnalysis.setUiCallback(this)
    }

    override fun onDataReceive(msg41DataModel: Msg41DataModel) {
        _msg41Date.postValue(msg41DataModel)
    }

}