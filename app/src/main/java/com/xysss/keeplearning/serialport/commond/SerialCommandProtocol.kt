package com.xysss.keeplearning.serialport.commond

import com.serial.port.manage.command.protocol.BaseProtocol
import com.xysss.keeplearning.app.util.ByteUtils
import com.xysss.keeplearning.app.util.Crc8

/**
 * 作者 : xys
 * 时间 : 2022-05-11 15:33
 * 描述 : 命令池
 */

object SerialCommandProtocol : BaseProtocol() {
    var baseStart = byteArrayOf(0x55.toByte())
    var baseEnd = byteArrayOf(0x23.toByte())

    /**
     * 系统状态参数读取,检测机器状态信息
     */
    var systemState = byteArrayOf(0xA1.toByte())
    var deviceInfo = byteArrayOf(0x00.toByte(), 0xB5.toByte())

    /**
     * 读取主板版本号
     */
    var readVersion = byteArrayOf(0x55.toByte(),0x00.toByte(), 0xAD.toByte(),0x05.toByte(),0x02.toByte(),0x00.toByte(),0x00.toByte())

    /**
     * 升级指令
     */
    var upgrade = byteArrayOf(0xAA.toByte())
    var readyForUpgrade = byteArrayOf(0x01.toByte(), 0x00.toByte(), 0xAB.toByte())

    var testCommond = byteArrayOf(0x55.toByte(), 0x00.toByte(), 0x0A.toByte(), 0x09.toByte(),
        0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x23.toByte())

    /**
     * 检查机器运行状态信息
     *
     * @return 0xAA 0xA1 0x00 0xB5
     */
    fun onCmdCheckDeviceStatusInfo(): ByteArray {
        return buildControllerProtocol(
            baseStart,
            systemState,
            deviceInfo
        )
    }

    fun test(): ByteArray {
        return buildControllerProtocol(
            testCommond
        )
    }

    /**
     * 获取主板版本号
     *
     * @return 0xAA 0xA9 0x00 0xAD
     */
    fun onCmdReadVersionStatus(): ByteArray {
        val readVersion = readVersion+Crc8.cal_crc8_t(readVersion,readVersion.size) + ByteUtils.FRAME_END

        return buildControllerProtocol(
            readVersion
        )
    }

    /**
     * 准备进入升级模式
     *
     * @return 0xAA 0xAA 0x01 0x00 0xAB
     */
    fun onCmdReadyForUpgrade(): ByteArray {
        return buildControllerProtocol(
            baseStart,
            upgrade,
            readyForUpgrade
        )
    }

    /**
     * 校验板子发回来的结果集
     *
     * @return
     */
    fun checkHex(ret: ByteArray): Boolean {
        var tempRet = 0
        for (i in 0 until ret.size - 1) {
            tempRet += ret[i]
        }
        return (tempRet.inv() + 1).toByte() == ret[ret.size - 1]
    }
}