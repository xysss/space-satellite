package com.xysss.keeplearning.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.blankj.utilcode.util.ServiceUtils.bindService
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.serial.port.kit.core.common.TypeConversion
import com.serial.port.manage.data.WrapReceiverData
import com.serial.port.manage.data.WrapSendData
import com.serial.port.manage.listener.OnDataPickListener
import com.serial.port.manage.listener.OnDataReceiverListener
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.bugly.crashreport.CrashReport
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.app.ext.*
import com.xysss.keeplearning.app.room.Matter
import com.xysss.keeplearning.app.service.MQTTService
import com.xysss.keeplearning.app.util.BleHelper
import com.xysss.keeplearning.app.util.FileUtils
import com.xysss.keeplearning.data.annotation.ValueKey
import com.xysss.keeplearning.data.repository.Repository
import com.xysss.keeplearning.databinding.FragmentOneBinding
import com.xysss.keeplearning.serialport.SenderManager
import com.xysss.keeplearning.serialport.SerialPortHelper
import com.xysss.keeplearning.serialport.listener.OnReadSystemStateListener
import com.xysss.keeplearning.serialport.listener.OnReadVersionListener
import com.xysss.keeplearning.serialport.model.DeviceVersionModel
import com.xysss.keeplearning.serialport.model.SystemStateModel
import com.xysss.keeplearning.ui.activity.*
import com.xysss.keeplearning.viewmodel.BlueToothViewModel
import com.xysss.mvvmhelper.base.appContext
import com.xysss.mvvmhelper.ext.*
import com.xysss.mvvmhelper.net.LoadingDialogEntity
import com.xysss.mvvmhelper.net.LoadingType.Companion.LOADING_CUSTOM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Author:bysd-2
 * Time:2021/9/2811:15
 */

class OneFragment : BaseFragment<BlueToothViewModel, FragmentOneBinding>(){
    companion object {
        private const val TAG = "OneFragment"
    }
    private var downloadApkPath = ""

    override fun initView(savedInstanceState: Bundle?) {
        //bugly进入首页检查更新
        //Beta.checkUpgrade(false, true)

        //请求权限
        requestCameraPermissions()
    }

    override fun onResume() {
        super.onResume()
        // 增加统一监听回调
        SerialPortHelper.portManager.addDataPickListener(onDataPickListener)
    }

    override fun onPause() {
        super.onPause()
        // 移除统一监听回调
        SerialPortHelper.portManager.removeDataPickListener(onDataPickListener)
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

    override fun onDestroyView() {
        super.onDestroyView()
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewClick() {
        setOnclickNoRepeat(
            mViewBinding.loginBtn, mViewBinding.testPageBtn, mViewBinding.testListBtn,
            mViewBinding.testDownload, mViewBinding.testUpload, mViewBinding.testCrash,
            mViewBinding.getPermission, mViewBinding.testRoom, mViewBinding.linkSerialPort,

        ) {
            when (it.id) {
                R.id.linkSerialPort->{
                    SerialPortHelper.readSystemState(object : OnReadSystemStateListener {
                        override fun onResult(systemStateModel: SystemStateModel) {
                            Log.d(TAG, "onResult: $systemStateModel")
                        }
                    })

                    // 打开串口
                    if (!SerialPortHelper.portManager.isOpenDevice) {
                        val open = SerialPortHelper.portManager.open()
                        Log.d(TAG, "串口打开${if (open) "成功" else "失败"}")
                    }

                    // 关闭串口
                    val close = SerialPortHelper.portManager.close()
                    Log.d(TAG, "串口关闭${if (close) "成功" else "失败"}")

                    SerialPortHelper.readVersion(object : OnReadVersionListener {
                        override fun onResult(deviceVersionModel: DeviceVersionModel) {
                            Log.d(TAG, "onResult: $deviceVersionModel")
                        }
                    })
                    // 发送数据
                    SerialPortHelper.portManager.send(
                        WrapSendData(
                            SenderManager.getSender().sendStartDetect()
                        ),
                        object : OnDataReceiverListener {
                            override fun onSuccess(data: WrapReceiverData) {
                                Log.d(TAG, "响应数据：${TypeConversion.bytes2HexString(data.data)}")
                            }

                            override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                                Log.e(
                                    TAG,
                                    "发送数据: ${TypeConversion.bytes2HexString(wrapSendData.sendData)}, $msg"
                                )
                            }

                            override fun onTimeOut() {
                                Log.e(TAG, "发送或者接收超时")
                            }
                        })

                    // 切换串口
//                    val switchDevice = SerialPortHelper.portManager.switchDevice(path = "/dev/ttyS1")
//                    Log.d(TAG, "串口切换${if (switchDevice) "成功" else "失败"}")

                    // 切换波特率
//                    val switchDevice = SerialPortHelper.portManager.switchDevice(baudRate = 9600)
//                    Log.d(TAG, "波特率切换${if (switchDevice) "成功" else "失败"}")
                }

                //以下为demo按钮
                R.id.testRoom -> {
                    toStartActivity(RoomSampleActivity::class.java)
                }
                R.id.getPermission -> {
                    requestCameraPermissions()
                }
                R.id.loginBtn -> {
                    toStartActivity(LoginActivity::class.java)
                }
                R.id.testPageBtn -> {
                    toStartActivity(TestActivity::class.java)
                }
                R.id.testListBtn -> {
                    toStartActivity(ListActivity::class.java)
                }

                R.id.testDownload -> {
                    mViewModel.downLoad({
                        //下载中
                        mViewBinding.testUpdateText.text = "下载进度：${it.progress}%"
                    }, {
                        //下载完成
                        downloadApkPath = it
                        showDialogMessage("下载成功，路径为：${it}")
                    }, {
                        //下载失败
                        showDialogMessage(it.msg)
                    })
                }
                R.id.testUpload -> {
                    mViewModel.upload(downloadApkPath, {
                        //上传中 进度
                        mViewBinding.testUpdateText.text = "上传进度：${it.progress}%"
                    }, {
                        //上传完成
                        showDialogMessage("上传成功：${it}")
                    }, {
                        //上传失败
                        showDialogMessage("${it.msg}--${it.message}")
                    })
                }
                R.id.testCrash -> {
                    //测试捕获异常
                    CrashReport.testJavaCrash()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        BleHelper.gatt?.close()
        super.onDestroy()
    }

    private val onDataPickListener: OnDataPickListener = object : OnDataPickListener {
        override fun onSuccess(data: WrapReceiverData) {
            Log.d(TAG, "统一响应数据：${TypeConversion.bytes2HexString(data.data)}")
        }
    }


}