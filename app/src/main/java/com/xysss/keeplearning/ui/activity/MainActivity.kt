package com.xysss.keeplearning.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ToastUtils
import com.serial.port.kit.core.common.TypeConversion
import com.serial.port.manage.data.WrapReceiverData
import com.serial.port.manage.data.WrapSendData
import com.serial.port.manage.listener.OnDataPickListener
import com.serial.port.manage.listener.OnDataReceiverListener
import com.swallowsonny.convertextlibrary.*
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseActivity
import com.xysss.keeplearning.app.ext.*
import com.xysss.keeplearning.app.room.Alarm
import com.xysss.keeplearning.app.room.Matter
import com.xysss.keeplearning.app.room.Record
import com.xysss.keeplearning.app.util.ByteUtils
import com.xysss.keeplearning.app.util.Crc8
import com.xysss.keeplearning.app.util.FileUtils
import com.xysss.keeplearning.data.annotation.ValueKey
import com.xysss.keeplearning.data.repository.Repository
import com.xysss.keeplearning.data.response.MaterialInfo
import com.xysss.keeplearning.databinding.ActivityMainBinding
import com.xysss.keeplearning.serialport.SenderManager
import com.xysss.keeplearning.serialport.SerialPortHelper
import com.xysss.keeplearning.serialport.listener.OnReadSystemStateListener
import com.xysss.keeplearning.serialport.listener.OnReadVersionListener
import com.xysss.keeplearning.serialport.model.DeviceVersionModel
import com.xysss.keeplearning.serialport.model.SystemStateModel
import com.xysss.keeplearning.ui.adapter.MainAdapter
import com.xysss.keeplearning.ui.fragment.OneFragment
import com.xysss.keeplearning.viewmodel.MainActivityViewModel
import com.xysss.keeplearning.viewmodel.TestViewModel
import com.xysss.mvvmhelper.base.appContext
import com.xysss.mvvmhelper.ext.hideStatusBar
import com.xysss.mvvmhelper.ext.logE
import com.xysss.mvvmhelper.net.manager.NetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<MainActivityViewModel, ActivityMainBinding>() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val transcodingBytesList = ArrayList<Byte>()
    private lateinit var afterBytes: ByteArray
    private val newLengthBytes = ByteArray(2)
    private var newLength = 0
    private var beforeIsFF = false

    override fun initView(savedInstanceState: Bundle?) {

        hideStatusBar(this)
        //mToolbar.setCenterTitle(R.string.bottom_title_read)
        //进行竖向方向的滑动
        //mViewBinding.mainViewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        mViewBinding.mainViewPager.adapter = MainAdapter(this)
        mViewBinding.mainViewPager.offscreenPageLimit = mViewBinding.mainViewPager.adapter!!.itemCount
        mViewBinding.mainViewPager.isUserInputEnabled = true  //true:滑动，false：禁止滑动

        "android pxToDp: ${pxToDp(68F)}".logE(logFlag)

        // 打开串口
        if (!SerialPortHelper.portManager.isOpenDevice) {
            val open = SerialPortHelper.portManager.open()
            "串口打开${if (open) "成功" else "失败"}".logE(logFlag)
        }

//        SerialPortHelper.readSystemState(object : OnReadSystemStateListener {
//            override fun onResult(systemStateModel: SystemStateModel) {
//                "onResult: $systemStateModel".logE(logFlag)
//            }
//        })
//
//        SerialPortHelper.readVersion(object : OnReadVersionListener {
//            override fun onResult(deviceVersionModel: DeviceVersionModel) {
//                "onResult: $deviceVersionModel".logE(logFlag)
//            }
//        })
//
//
//        // 发送数据
//        SerialPortHelper.portManager.send(
//            WrapSendData(
//                SenderManager.getSender().sendStartDetect()
//            ),
//            object : OnDataReceiverListener {
//                override fun onSuccess(data: WrapReceiverData) {
//                    "响应数据：${TypeConversion.bytes2HexString(data.data)}".logE(logFlag)
//                }
//
//                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
//                    "发送数据: ${TypeConversion.bytes2HexString(wrapSendData.sendData)}, $msg".logE(logFlag)
//                }
//
//                override fun onTimeOut() {
//                    "发送或者接收超时".logE(logFlag)
//                }
//            })

    }


    private fun pxToDp(pxValue: Float): Int {
        val density: Float = appContext.resources.displayMetrics.density
        val dpi: Int = appContext.resources.displayMetrics.densityDpi
        val widthPixels: Int = appContext.resources.displayMetrics.widthPixels
        val heightPixels: Int = appContext.resources.displayMetrics.heightPixels
        "density: $density，dpi: $dpi，widthPixels: $widthPixels，heightPixels: $heightPixels".logE(logFlag)

        return (pxValue / density + 0.5f).toInt()
    }

    /**
     * 示例，在Activity/Fragment中如果想监听网络变化，可重写onNetworkStateChanged该方法
     */
    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        if (netState.isSuccess) {
            ToastUtils.showShort("终于有网了!")
        } else {
            ToastUtils.showShort("网络无连接!")
        }
    }

    override fun showToolBar() = false

    override fun onDestroy() {
        super.onDestroy()
        //取消协程
        job.cancel()

        // 关闭串口
        val close = SerialPortHelper.portManager.close()
        "串口关闭${if (close) "成功" else "失败"}".logE(logFlag)

//        // 读取版本信息
//        SerialPortHelper.readVersion(object : OnReadVersionListener {
//            override fun onResult(deviceVersionModel: DeviceVersionModel) {
//                Log.d(TAG, "onResult: $deviceVersionModel")
//            }
//        })
//
//        // 读取系统信息
//        SerialPortHelper.readSystemState(object : OnReadSystemStateListener {
//            override fun onResult(systemStateModel: SystemStateModel) {
//                Log.d(TAG, "onResult: $systemStateModel")
//            }
//        })

         //切换串口
//        val switchDevice = SerialPortHelper.portManager.switchDevice(path = "/dev/ttyS1")
//        Log.d(TAG, "串口切换${if (switchDevice) "成功" else "失败"}")

         //切换波特率
//        val switchDevice = SerialPortHelper.portManager.switchDevice(baudRate = 9600)
//        Log.d(TAG, "波特率切换${if (switchDevice) "成功" else "失败"}")

    }

    override fun onPause() {
        super.onPause()
        // 移除统一监听回调
        SerialPortHelper.portManager.removeDataPickListener(onDataPickListener)
    }

    override fun onResume() {
        super.onResume()
        // 增加统一监听回调
        SerialPortHelper.portManager.addDataPickListener(onDataPickListener)
        scope.launch(Dispatchers.IO) {
            startDealMessage()
        }
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

    private suspend fun startDealMessage() {
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
                ByteUtils.Msg80 -> {
                    scope.launch(Dispatchers.IO) {
                        dealMsg80(it)
                    }
                }
                //实时数据
                ByteUtils.Msg90 -> {
                    scope.launch(Dispatchers.IO) {
                        dealMsg90(it)
                    }
                }
                //历史记录
                ByteUtils.Msg81 -> {
                    scope.launch(Dispatchers.IO) {
                    }
                }
                //物质信息
                ByteUtils.MsgA1 -> {
                    scope.launch(Dispatchers.IO) {
                        dealMsgA1(it)
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

    @Synchronized
    private fun addRecLinkedDeque(byte: Byte) {
        if (!recLinkedDeque.offer(byte)){
            "recLinkedDeque空间已满".logE("xysLog")
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
                mmkv.putString(ValueKey.recTopicValue, recTopicDefault+String(tempBytes)+"/")
                mmkv.putString(ValueKey.sendTopicValue, sendTopicDefault+String(tempBytes)+"/")

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
