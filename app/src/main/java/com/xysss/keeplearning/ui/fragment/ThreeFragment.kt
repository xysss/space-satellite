package com.xysss.keeplearning.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.RadioGroup
import androidx.core.view.GravityCompat
import com.serial.port.kit.core.common.TypeConversion
import com.serial.port.manage.data.WrapReceiverData
import com.serial.port.manage.data.WrapSendData
import com.serial.port.manage.listener.OnDataReceiverListener
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.app.ext.logFlag
import com.xysss.keeplearning.databinding.FragmentThreeBinding
import com.xysss.keeplearning.serialport.SenderManager
import com.xysss.keeplearning.serialport.SerialPortHelper
import com.xysss.keeplearning.viewmodel.MainActivityViewModel
import com.xysss.mvvmhelper.ext.logE
import com.xysss.mvvmhelper.ext.setOnclickNoRepeat

/**
 * Author:bysd-2
 * Time:2021/9/2811:16
 */

class ThreeFragment : BaseFragment<MainActivityViewModel, FragmentThreeBinding>() {

    override fun initView(savedInstanceState: Bundle?) {

        mViewBinding.sceneRadioGroup1.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                mViewBinding.radiobutton1.id->
                {
                    "radiobutton1".logE(logFlag)
                }
                mViewBinding.radiobutton2.id->
                {
                    "radiobutton2".logE(logFlag)
                }
                mViewBinding.radiobutton3.id->
                {
                    "radiobutton3".logE(logFlag)
                }
                mViewBinding.radiobutton4.id->
                {
                    "radiobutton4".logE(logFlag)
                }
                mViewBinding.radiobutton5.id->
                {
                    "radiobutton5".logE(logFlag)
                }
                mViewBinding.radiobutton6.id->
                {
                    "radiobutton6".logE(logFlag)
                }
            }
        })

        mViewBinding.sceneRadioGroupTime.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                mViewBinding.radiobutton1Time.id->
                {
                    "radiobutton1Time".logE(logFlag)
                }
                mViewBinding.radiobutton2Time.id->
                {
                    "radiobutton2Time".logE(logFlag)
                }
                mViewBinding.radiobutton3Time.id->
                {
                    "radiobutton3Time".logE(logFlag)
                }
                mViewBinding.radiobutton4Time.id->
                {
                    "radiobutton4Time".logE(logFlag)
                }
                mViewBinding.radiobutton5Time.id->
                {
                    "radiobutton5Time".logE(logFlag)
                }
                mViewBinding.radiobutton6Time.id->
                {
                    "radiobutton6Time".logE(logFlag)
                }
            }
        })
    }

    override fun onBindViewClick() {
        setOnclickNoRepeat(
            mViewBinding.line1Three,mViewBinding.line2Three,mViewBinding.line3Three,mViewBinding.line4Three
        ) {
            when (it.id) {
                R.id.line1_three->{
                    SerialPortHelper.portManager.send(
                        WrapSendData(SenderManager.getSender().sendReadVersion()),
                        object : OnDataReceiverListener {
                            override fun onSuccess(data: WrapReceiverData) {
                                "响应数据：${TypeConversion.bytes2HexString(data.data)}".logE(logFlag)
                            }

                            override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                                "发送数据: ${TypeConversion.bytes2HexString(wrapSendData.sendData)}, $msg".logE(logFlag)
                            }

                            override fun onTimeOut() {
                                "发送或者接收超时".logE(logFlag)
                            }
                        })
                }
                R.id.line3_three->{
                }
                R.id.line4_three->{
                }

                R.id.line2_three->{
                    // 发送数据
                    SerialPortHelper.readVersion(null)
                }
            }
        }
    }

    override fun initObserver() {
        super.initObserver()

        }
    }
