package com.xysss.keeplearning.serialport

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.serial.port.kit.core.common.TypeConversion
import com.serial.port.manage.SerialPortManager
import com.serial.port.manage.data.WrapReceiverData
import com.serial.port.manage.data.WrapSendData
import com.serial.port.manage.listener.OnDataPickListener
import com.serial.port.manage.listener.OnDataReceiverListener
import com.swallowsonny.convertextlibrary.*
import com.xysss.keeplearning.app.ext.*
import com.xysss.keeplearning.app.room.Matter
import com.xysss.keeplearning.app.util.ByteUtils
import com.xysss.keeplearning.app.util.Crc8
import com.xysss.keeplearning.data.annotation.ValueKey
import com.xysss.keeplearning.data.repository.Repository
import com.xysss.keeplearning.data.response.MaterialInfo
import com.xysss.keeplearning.serialport.commond.SerialCommandProtocol
import com.xysss.keeplearning.serialport.listener.OnReadSystemStateListener
import com.xysss.keeplearning.serialport.listener.OnReadVersionListener
import com.xysss.keeplearning.serialport.model.DeviceVersionModel
import com.xysss.keeplearning.serialport.model.NfcModel
import com.xysss.keeplearning.serialport.model.SystemStateModel
import com.xysss.keeplearning.serialport.proxy.SerialPortProxy
import com.xysss.mvvmhelper.ext.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.experimental.and

/**
 * 作者 : xys
 * 时间 : 2022-05-11 14:50
 * 描述 : 工具指令管理 只有使用的时候才会进行初始化
 */


object SerialPortHelper {

    private const val TAG = "SerialPortManager"

    private val mHandler = Handler(Looper.getMainLooper())
    private val mProxy = SerialPortProxy()


    /**
     * 暴露SDK
     */
    val portManager: SerialPortManager
        get() = mProxy.portManager

    /**
     * 内部使用，默认开启串口
     */
    private val serialPortManager: SerialPortManager
        get() {
            // 默认开启串口
            if (!portManager.isOpenDevice) {
                portManager.open()
            }
            return portManager
        }


    /**
     * 读取设备版本信息
     *
     * @param listener 监听回调
     */
    fun readVersion(listener: OnReadVersionListener?) {
        val sends: ByteArray = SenderManager.getSender().sendReadVersion()
        val isSuccess: Boolean =
            serialPortManager.send(
                WrapSendData(sends, 3000, 300, 1),
                object : OnDataReceiverListener {
                    override fun onSuccess(data: WrapReceiverData) {
                        val buffer: ByteArray = data.data
                    }

                    override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                        Log.e(TAG, "onFailed: $msg")
                    }

                    override fun onTimeOut() {
                        Log.d(TAG, "onTimeOut: 发送数据或者接收数据超时")
                    }
                })
        printLog(isSuccess, sends)
    }


    /**
     * 读取设备信息
     *
     * @param listener 监听回调
     */
    fun readSystemState(listener: OnReadSystemStateListener?) {
        val sends: ByteArray = SenderManager.getSender().sendStartDetect()
        val isSuccess: Boolean =
            serialPortManager.send(WrapSendData(sends), object : OnDataReceiverListener {

                override fun onSuccess(data: WrapReceiverData) {
                    val buffer = data.data
                    if (checkCallData(buffer)) {
                        //输入电压
                        val inputVoltage = buffer[3] * 0.1
                        //电机电压
                        val motorVoltage = buffer[4] * 0.1
                        //VCC电压
                        val vccVoltage = buffer[5] * 0.1
                        //MCU电压
                        val mcuVoltage = buffer[6] * 0.1
                        //温度值
                        val bytes = ByteArray(1)
                        bytes[0] = buffer[7]
                        val temperature: Int =
                            TypeConversion.bytes2HexString(bytes)?.substring(0, 2)?.toInt(16) ?: 0
                        //照度值
                        val illumination: Int =
                            ((buffer[8] and 0xFF.toByte()).toInt() shl 8) + (buffer[9] and 0xFF.toByte())
                        listener?.let {
                            runOnUiThread {
                                listener.onResult(
                                    SystemStateModel(
                                        inputVoltage,
                                        motorVoltage,
                                        vccVoltage,
                                        mcuVoltage,
                                        temperature,
                                        illumination
                                    )
                                )
                            }
                        }
                    }
                }

                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    Log.e(TAG, "onFailed: $msg")
                }

                override fun onTimeOut() {
                    Log.d(TAG, "onTimeOut: 发送数据或者接收数据超时")
                }
            })
        printLog(isSuccess, sends)
    }

    /**
     * 检测回调数据是否符合要求
     *
     * @param buffer 回调数据
     * @return true 符合要求 false 数据命令未通过校验
     */
    private fun checkCallData(buffer: ByteArray): Boolean {
        val tempData = TypeConversion.bytes2HexString(buffer)
        Log.i(TAG, "receive serialPort data ：$tempData")
        return buffer[0] == SerialCommandProtocol.baseStart[0] && SerialCommandProtocol.checkHex(
            buffer
        )
    }

    /**
     * 打印发送数据Log
     *
     * @param isSuccess 是否成功
     * @param bytes     数据
     */
    private fun printLog(isSuccess: Boolean, bytes: ByteArray) {
        val tempData = TypeConversion.bytes2HexString(bytes)
        Log.d(
            TAG,
            "buildControllerProtocol:" + tempData + "，结果=" + if (isSuccess) "发送成功" else "发送失败"
        )
    }

    /**
     * 切换到主线程
     *
     * @param runnable Runnable
     */
    private fun runOnUiThread(runnable: Runnable) {
        mHandler.post(runnable)
    }
}