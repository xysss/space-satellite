package com.xysss.keeplearning.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.serial.port.kit.core.common.TypeConversion
import com.serial.port.manage.data.WrapReceiverData
import com.serial.port.manage.data.WrapSendData
import com.serial.port.manage.listener.OnDataReceiverListener
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.app.ext.logFlag
import com.xysss.keeplearning.databinding.FragmentOneBinding
import com.xysss.keeplearning.serialport.SenderManager
import com.xysss.keeplearning.serialport.SerialPortHelper
import com.xysss.keeplearning.viewmodel.OneFragmentViewModel
import com.xysss.mvvmhelper.ext.*

/**
 * Author:bysd-2
 * Time:2021/9/2811:15
 */

class OneFragment : BaseFragment<OneFragmentViewModel, FragmentOneBinding>(){
    private var downloadApkPath = ""

    override fun initView(savedInstanceState: Bundle?) {
        //bugly进入首页检查更新
        //Beta.checkUpgrade(false, true)

        //请求权限
        requestCameraPermissions()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun initObserver() {
        super.initObserver()
    }

    /**
     * 请求相机权限
     */
    @SuppressLint("CheckResult")
    private fun requestCameraPermissions() {
        ToastUtils.showShort("请求相机权限")
        //请求打开相机权限
        val rxPermissions = RxPermissions(requireActivity())
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
            mViewBinding.humidityOne,mViewBinding.constraint1One
        ) {
            when (it.id) {
                R.id.humidity_one->{


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

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
    }


}