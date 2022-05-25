package com.xysss.mvvmhelper.ext

import com.kunminx.architecture.ui.callback.UnPeekLiveData

/**
 * 作者 : xys
 * 时间 : 2022-05-24 13:46
 * 描述 : 全局发送消息 通过LiveData实现
 */
object LiveDataEvent {

    //示例：登录成功发送通知
    val loginEvent = UnPeekLiveData<Boolean>()

}