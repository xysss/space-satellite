package com.xysss.keeplearning.ui.fragment

import android.os.Bundle
import android.widget.RadioGroup
import androidx.fragment.app.activityViewModels
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.app.ext.logFlag
import com.xysss.keeplearning.app.util.ByteUtils
import com.xysss.keeplearning.databinding.FragmentThreeBinding
import com.xysss.keeplearning.serialport.SerialPortHelper
import com.xysss.keeplearning.viewmodel.ShellMainSharedViewModel
import com.xysss.keeplearning.viewmodel.ThreeFragmentViewModel
import com.xysss.mvvmhelper.ext.logE
import com.xysss.mvvmhelper.ext.setOnclickNoRepeat

/**
 * Author:bysd-2
 * Time:2021/9/2811:16
 */

class ThreeFragment : BaseFragment<ThreeFragmentViewModel, FragmentThreeBinding>() {

    //获取共享ViewModel
    private val shellMainSharedViewModel: ShellMainSharedViewModel by activityViewModels()
    private var switchFlag: Boolean= false

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

        //获取版本号
        SerialPortHelper.readVersion()

        //获取设备设置时间和风速
        SerialPortHelper.getDevicePurifyReq()

        //设置设备时间和风速
        SerialPortHelper.setDevicePurifyReq(1000,20)
    }

    override fun onBindViewClick() {
        setOnclickNoRepeat(
            mViewBinding.line1Three,mViewBinding.line2Three,mViewBinding.line3Three,mViewBinding.line4Three,
            mViewBinding.image1Three
        ) {
            when (it.id) {
                R.id.image1_three->{
                    switchFlag = if (switchFlag){
                        mViewBinding.image1Three.setImageResource(R.drawable.switch_off_icon)
                        false
                    }else{
                        mViewBinding.image1Three.setImageResource(R.drawable.switch_on_icon)
                        true
                    }
                }
                R.id.line1_three->{
                    SerialPortHelper.setWorkModel(ByteUtils.Msg00)
                }
                R.id.line2_three->{
                    SerialPortHelper.setWorkModel(ByteUtils.Msg01)
                }
                R.id.line3_three->{
                    SerialPortHelper.setWorkModel(ByteUtils.Msg02)
                }
                R.id.line4_three->{
                    SerialPortHelper.setWorkModel(ByteUtils.Msg03)
                }
            }
        }
    }

    override fun initObserver() {
        super.initObserver()

        shellMainSharedViewModel.msg41Date.observe(this){

            it.toString().logE(logFlag)
            mViewBinding.tV16Three.text=it.dust
            mViewBinding.tV10Three.text=it.temp+"°"
            mViewBinding.tV11Three.text=it.dumity
            mViewBinding.tV12Three.text=it.voc


        }
    }
}
