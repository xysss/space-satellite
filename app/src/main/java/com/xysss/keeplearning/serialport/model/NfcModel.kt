package com.xysss.keeplearning.serialport.model

/**
 * 作者 : xys
 * 时间 : 2022-06-01 11:31
 * 描述 : 描述
 */
data class NfcModel(
    val nfcStatus: String,
    val num: String,
    val userTime: String,
    val reminder: String,
    val sn: String
)