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

    private val transcodingBytesList = ArrayList<Byte>()
    private lateinit var afterBytes: ByteArray
    private val newLengthBytes = ByteArray(2)
    private var newLength = 0
    private var beforeIsFF = false

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

    private val onDataPickListener: OnDataPickListener = object : OnDataPickListener {
        override fun onSuccess(data: WrapReceiverData) {
            "统一响应数据：${TypeConversion.bytes2HexString(data.data)}".logE(logFlag)

            scope.launch(Dispatchers.IO) {
                for (byte in data.data)
                    addRecLinkedDeque(byte)
            }
        }
    }

    @Synchronized
    fun addRecLinkedDeque(byte: Byte) {
        if (!recLinkedDeque.offer(byte)){
            "recLinkedDeque空间已满".logE("xysLog")
        }
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
                        if (checkCallData(buffer)) {
                            val serializeId: Int =
                                ((buffer[7] and 0xFF.toByte()).toInt() shl 24) + ((buffer[8] and 0xFF.toByte()).toInt() shl 16) + ((buffer[9] and 0xFF.toByte()).toInt() shl 8) + (buffer[10] and 0xFF.toByte())
                            listener?.let {
                                runOnUiThread {
                                    listener.onResult(
                                        DeviceVersionModel(
                                            String.format("%s", serializeId),
                                            String.format("v %s.%s", buffer[3], buffer[4]),
                                            String.format("v %s.%s", buffer[5], buffer[6])
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

    suspend fun startDealMessage() {
        while (true) {
            recLinkedDeque.poll()?.let {
                if (it == ByteUtils.FRAME_START) {
                    transcodingBytesList.clear()
                    transcodingBytesList.add(it)
                } else if (beforeIsFF) {
                    when (it) {
                        ByteUtils.FRAME_FF -> {
                            transcodingBytesList.add(ByteUtils.FRAME_FF)
                        }
                        ByteUtils.FRAME_00 -> {
                            transcodingBytesList.add(ByteUtils.FRAME_START)
                        }
                        else -> {
                            transcodingBytesList.add(ByteUtils.FRAME_FF)
                            transcodingBytesList.add(it)
                        }
                    }
                    beforeIsFF = false
                } else if (!beforeIsFF) {
                    if (it == ByteUtils.FRAME_FF) {
                        beforeIsFF = true
                    } else {
                        beforeIsFF = false
                        transcodingBytesList.add(it)
                    }
                }

                //取协议数据长度
                if (transcodingBytesList.size == 3) {
                    newLengthBytes[0] = transcodingBytesList[1]
                    newLengthBytes[1] = transcodingBytesList[2]
                    newLength = newLengthBytes.readInt16BE()
                    "协议长度: $newLength".logE("xysLog")
                }

                if (transcodingBytesList.size == newLength && transcodingBytesList.size > 9) {
                    transcodingBytesList.let { arrayList ->
                        afterBytes = ByteArray(arrayList.size)
                        for (k in afterBytes.indices) {
                            afterBytes[k] = arrayList[k]
                        }
                    }

                    isRecOK = if (afterBytes[0] == ByteUtils.FRAME_START && afterBytes[afterBytes.size - 1] == ByteUtils.FRAME_END) {
                        //CRC校验
                        if (Crc8.isFrameValid(afterBytes, afterBytes.size)) {
                            analyseMessage(afterBytes)  //分发数据
                            //"协议正确: ${afterBytes.toHexString()}".logE("xysLog")
                            true
                        } else {
                            "CRC校验错误，协议长度: $newLength : ${afterBytes.toHexString()}".logE("xysLog")
                            false
                        }
                    } else {
                        "协议开头结尾不对:  ${afterBytes.toHexString()}".logE("xysLog")
                        false
                    }
                    transcodingBytesList.clear()
                } else if (newLength < 9 && transcodingBytesList.size > 9) { //协议长度不够
                    "解析协议不完整，协议长度: $newLength  解析长度：${transcodingBytesList.size} ,${transcodingBytesList.toHexString()}".logE("xysLog")
                    isRecOK = false
                    //BleHelper.retryHistoryMessage(recordCommand,alarmCommand)
                    transcodingBytesList.clear()
                }
            }
        }
    }

    private suspend fun analyseMessage(mBytes: ByteArray?) {
        mBytes?.let {
            when (it[4]) {
                //设备信息
                ByteUtils.Msg03 -> {
                    scope.launch(Dispatchers.IO) {
                        dealMsg03(it)
                    }
                }
                //设置设备工作模式响
                ByteUtils.Msg4F -> {
                    scope.launch(Dispatchers.IO) {
                        dealMsg4F(it)
                    }
                }
                //获取设备净化功能响应
                ByteUtils.Msg73 -> {
                    scope.launch(Dispatchers.IO) {
                        dealMsg73(it)
                    }
                }
                //数据响应/通知
                ByteUtils.Msg41 -> {
                    scope.launch(Dispatchers.IO) {
                        dealMsg41(it)
                    }
                }



                //物质库信息
                ByteUtils.MsgA0 -> {
                    scope.launch(Dispatchers.IO) {
                        dealMsgA0(it)
                    }
                }
                else -> it[4].toInt().logE("xysLog")
            }
        }
    }


    private fun dealMsgA1(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 77) {
                //物质索引号
                val matterIndex = it.readByteArrayBE(7, 4).readInt32LE()
                //cf值
                val mcfNum = String.format("%.2f", it.readByteArrayBE(11, 4).readFloatLE())
                var i = 35
                while (i < it.size)
                    if (it[i] == ByteUtils.FRAME_00) break else i++
                val tempBytes: ByteArray = it.readByteArrayBE(35, i - 35)
                val matterName = String(tempBytes)
                val matter = Matter(matterIndex, matterName, mcfNum)
                if (Repository.forgetMatterIsExist(matter.voc_index_matter) == 0) {
                    Repository.insertMatter(matter)
                }
            } else {
                "查询物质信息协议长度不为77，实际长度：${it.size}".logE("xysLog")
            }
        }
    }

    private fun dealMsgA0(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 17) {
                //物质库个数
                val matterSum = it.readByteArrayBE(7, 4).readInt32LE()
                "物质库个数：$matterSum".logE("xysLog")
                mmkv.putInt(ValueKey.matterSum, matterSum)

                //当前选中索引
                val choiceIndex = it.readByteArrayBE(11, 4).readInt32LE()
            } else {
                "查询物质信息协议长度不为17，实际长度：${it.size}".logE("xysLog")
            }
        }
    }

    private fun dealMsg03(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 33) {
                //版本号
                mmkv.putString(ValueKey.deviceHardwareVersion,it[7].toInt().toString()+":"+it[8].toInt().toString())
                mmkv.putString(ValueKey.deviceSoftwareVersion,it[9].toInt().toString()+":"+it[10].toInt().toString())
                //设备序列号
                var i = 12
                while (i < it.size)
                    if (it[i] == ByteUtils.FRAME_00) break else i++
                val tempBytes: ByteArray = it.readByteArrayBE(12, i - 12)
                mmkv.putString(ValueKey.deviceId,String(tempBytes))
                "设备信息解析成功: ${String(tempBytes)}".logE("xysLog")
            }
        }
    }

    private fun dealMsg4F(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 10) {
                "解析成功: ${it[9].toInt()}".logE("xysLog")
            }
        }
    }

    private fun dealMsg73(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 11) {
                "解析成功: ${it[9].toInt()}，${it[10].toInt()}".logE("xysLog")
            }
        }
    }

    private fun dealMsg41(mBytes: ByteArray) {
        mBytes.let {
            if (it.size > 11) {
                "解析成功: ${it[9].toInt()}，${it[10].toInt()}".logE("xysLog")
            }
        }
    }

    private fun dealMsg80(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 71) {
                //设备序列号
                var i = 49
                while (i < it.size)
                    if (it[i] == ByteUtils.FRAME_00) break else i++
                val tempBytes: ByteArray = it.readByteArrayBE(49, i - 49)

                mmkv.putString(ValueKey.deviceHardwareVersion,it[7].toInt().toString()+":"+it[8].toInt().toString())
                mmkv.putString(ValueKey.deviceSoftwareVersion,it[9].toInt().toString()+":"+it[10].toInt().toString())
                mmkv.putInt(ValueKey.deviceBattery,it[11].toInt())
                mmkv.putInt(ValueKey.deviceFreeMemory,it[12].toInt())
                mmkv.putInt(ValueKey.deviceRecordSum,it.readByteArrayBE(13, 4).readInt32LE())
                mmkv.putInt(ValueKey.deviceAlarmSum,it.readByteArrayBE(17, 4).readInt32LE())
                mmkv.putInt(ValueKey.deviceCurrentRunningTime,it.readByteArrayBE(21, 4).readInt32LE())
                mmkv.putInt(ValueKey.deviceCurrentAlarmNumber,it.readByteArrayBE(25, 4).readInt32LE())
                mmkv.putInt(ValueKey.deviceCumulativeRunningTime,it.readByteArrayBE(29, 4).readInt32LE())
                mmkv.putString(ValueKey.deviceDensityMax,String.format("%.2f", it.readByteArrayBE(33, 4).readFloatLE()))
                mmkv.putString(ValueKey.deviceDensityMin,String.format("%.2f", it.readByteArrayBE(37, 4).readFloatLE()))
                mmkv.putString(ValueKey.deviceTwaNumber,String.format("%.2f", it.readByteArrayBE(41, 4).readFloatLE()))
                mmkv.putString(ValueKey.deviceSteLNumber,String.format("%.2f", it.readByteArrayBE(45, 4).readFloatLE()))
                mmkv.putString(ValueKey.deviceId,String(tempBytes))
                mmkv.putString(ValueKey.recTopicValue, recTopicDefault +String(tempBytes)+"/")
                mmkv.putString(ValueKey.sendTopicValue, sendTopicDefault +String(tempBytes)+"/")

                "设备信息解析成功: ${String(tempBytes)}".logE("xysLog")

            }
        }
    }

    private suspend fun dealMsg90(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 69) {
                //浓度值
                val concentrationNum = String.format("%.2f", it.readByteArrayBE(7, 4).readFloatLE())
                //报警状态
                val concentrationState = it.readByteArrayBE(11, 4).readInt32LE()
                //物质库索引
                val materialLibraryIndex = it.readByteArrayBE(15, 4).readInt32LE()
                //浓度单位
                val concentrationUnit: String = when (it[19].toInt()) {
                    0 -> "ppm"
                    1 -> "ppb"
                    2 -> "mg/m3"
                    else -> ""
                }
                //CF值
                val cfNum = it.readByteArrayBE(23, 4).readFloatLE()
                //物质名称
                var i = 27
                while (i < it.size)
                    if (it[i] == ByteUtils.FRAME_00) break else i++
                val tempBytes: ByteArray = it.readByteArrayBE(27, i - 27)
                //val name = tempBytes.toAsciiString()
                val name = String(tempBytes)
                //tempBytes.toHexString().logE("xysLog")
                val materialInfo = MaterialInfo(
                    concentrationNum, concentrationState.toString(),
                    materialLibraryIndex, concentrationUnit, cfNum.toString(), name
                )

            }
        }
    }

}