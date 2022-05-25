package com.xysss.keeplearning.ui.fragment

import android.os.Bundle
import com.xysss.keeplearning.R
import com.xysss.keeplearning.app.base.BaseFragment
import com.xysss.keeplearning.databinding.FragmentFiveBinding
import com.xysss.keeplearning.viewmodel.MainActivityViewModel
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
        setOnclickNoRepeat(mViewBinding.buttonToWeather) {
            when (it.id) {
                R.id.buttonToWeather -> {
                }
            }
        }
    }

}