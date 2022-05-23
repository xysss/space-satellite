package com.xysss.keeplearning.serialport.sender

/**
 * 作者 : xys
 * 时间 : 2022-05-11 15:32
 * 描述 : 发送指令接口
 */

interface Sender {
    /**
     * 主板：检测
     * @return true 发送成功
     */
    fun sendStartDetect(): ByteArray

    /**
     * 主板：检测版本号
     * @return true 发送成功
     */
    fun sendReadVersion(): ByteArray


    fun sendTest(): ByteArray
}