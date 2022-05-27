package com.xysss.keeplearning.ui.fragment

import android.os.Bundle
import android.widget.RadioGroup
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.app.ext.logFlag
import com.xysss.keeplearning.databinding.FragmentThreeBinding
import com.xysss.keeplearning.viewmodel.MainActivityViewModel
import com.xysss.mvvmhelper.ext.logE

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

    override fun initObserver() {
        super.initObserver()

        }
    }
