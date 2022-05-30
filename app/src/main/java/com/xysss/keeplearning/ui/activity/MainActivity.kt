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
            SerialPortHelper.startDealMessage()
        }
    }

    private val onDataPickListener: OnDataPickListener = object : OnDataPickListener {
        override fun onSuccess(data: WrapReceiverData) {
            "统一响应数据：${TypeConversion.bytes2HexString(data.data)}".logE(logFlag)

            scope.launch(Dispatchers.IO) {
                for (byte in data.data)
                    SerialPortHelper.addRecLinkedDeque(byte)
            }
        }
    }
}
