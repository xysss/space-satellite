package com.xysss.keeplearning.serialport.sender

import com.xysss.keeplearning.serialport.commond.SerialCommandProtocol.onCmdCheckDeviceStatusInfo
import com.xysss.keeplearning.serialport.commond.SerialCommandProtocol.onCmdReadVersionStatus
import com.xysss.keeplearning.serialport.commond.SerialCommandProtocol.test

/**
 * 作者 : xys
 * 时间 : 2022-05-11 15:32
 * 描述 : 发送指令实现
 */

class AdapterSender : Sender {

    override fun sendStartDetect(): ByteArray {
        return onCmdCheckDeviceStatusInfo()
    }

    override fun sendReadVersion(): ByteArray {
        return onCmdReadVersionStatus()
    }

    override fun sendTest(): ByteArray {
        return test()
    }
}