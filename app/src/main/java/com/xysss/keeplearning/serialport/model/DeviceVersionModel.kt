package com.xysss.keeplearning.serialport.model

/**
 * 作者 : xys
 * 时间 : 2022-05-11 15:28
 * 描述 : 硬件版本信息
 */

data class DeviceVersionModel(
    val serialNumber: String?,
    val hardwareAmount: String?,
    val hardwareAppAmount: String?,
)
