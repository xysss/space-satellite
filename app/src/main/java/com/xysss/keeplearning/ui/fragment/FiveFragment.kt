package com.xysss.keeplearning.ui.fragment

import android.os.Bundle
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.app.ext.logFlag
import com.xysss.keeplearning.app.util.ByteUtils
import com.xysss.keeplearning.databinding.FragmentFiveBinding
import com.xysss.keeplearning.serialport.SerialPortHelper
import com.xysss.keeplearning.viewmodel.MainActivityViewModel
import com.xysss.mvvmhelper.ext.logE
import com.xysss.mvvmhelper.ext.setOnclickNoRepeat
import com.xysss.mvvmhelper.ext.toStartActivity

/**
 * 作者 : xys
 * 时间 : 2022-05-16 16:26
 * 描述 : 描述
 */
class FiveFragment : BaseFragment<MainActivityViewModel, FragmentFiveBinding>() {
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun onBindViewClick() {
        setOnclickNoRepeat(mViewBinding.button1) {
            when (it.id) {
                R.id.button1 -> {
                    var speedValue=mViewBinding.edit1.text.toString()
                    if (speedValue!=""){
                        "风速大小： $speedValue".logE(logFlag)
                        SerialPortHelper.setDevicePurifyReq(1000,speedValue.toInt())

                    }
                }
            }
        }
    }

}