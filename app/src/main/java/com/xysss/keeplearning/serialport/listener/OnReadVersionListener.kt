package com.xysss.keeplearning.serialport.listener

import com.xysss.keeplearning.serialport.model.DeviceVersionModel

/**
 * 作者 : xys
 * 时间 : 2022-05-11 15:27
 * 描述 : 读取版本
 */
interface OnReadVersionListener {
    /**
     * 结果
     *
     * @param deviceVersionModel 设备信息
     */
    fun onResult(deviceVersionModel: DeviceVersionModel)
}