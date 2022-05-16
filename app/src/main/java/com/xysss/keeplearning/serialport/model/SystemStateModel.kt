package com.xysss.keeplearning.serialport.model

/**
 * 作者 : xys
 * 时间 : 2022-05-11 15:30
 * 描述 : 系统状态
 */

data class SystemStateModel(
    /**
     * 输入电压
     */
    val inputVoltage: Double = 0.0,
    /**
     * 电机电压
     */
    val motorVoltage: Double = 0.0,
    /**
     * VCC电压
     */
    val vccVoltage: Double = 0.0,
    /**
     * MCU电压
     */

    val mcuVoltage: Double = 0.0,
    /**
     * 温度值
     */

    val temperature: Int = 0,
    /**
     * 照度值
     */
    val illumination: Int = 0,
)
