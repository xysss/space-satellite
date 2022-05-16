package com.xysss.keeplearning.serialport.listener

import com.xysss.keeplearning.serialport.model.SystemStateModel

/**
 * 作者 : xys
 * 时间 : 2022-05-11 15:30
 * 描述 : 读取系统信息
 */

interface OnReadSystemStateListener {
    /**
     * 结果
     *
     * @param systemStateModel 系统结果
     */
    fun onResult(systemStateModel: SystemStateModel)
}