package com.xysss.keeplearning.ui.fragment

import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.serial.port.kit.core.common.TypeConversion
import com.serial.port.manage.data.WrapReceiverData
import com.serial.port.manage.data.WrapSendData
import com.serial.port.manage.listener.OnDataReceiverListener
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.app.ext.logFlag
import com.xysss.keeplearning.app.util.ByteUtils
import com.xysss.keeplearning.databinding.FragmentThreeBinding
import com.xysss.keeplearning.serialport.SenderManager
import com.xysss.keeplearning.serialport.SerialPortHelper
import com.xysss.keeplearning.viewmodel.SettingViewModel
import com.xysss.keeplearning.viewmodel.ThreeFragmentViewModel
import com.xysss.mvvmhelper.ext.logE

/**
 * Author:bysd-2
 * Time:2021/9/2811:16
 */

class ThreeFragment : BaseFragment<ThreeFragmentViewModel, FragmentThreeBinding>() {

    override fun initView(savedInstanceState: Bundle?) {


    }

    override fun initObserver() {
        super.initObserver()

        }
    }
