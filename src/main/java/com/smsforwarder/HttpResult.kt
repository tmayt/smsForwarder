package com.smsforwarder

data class HttpResult(
    val success: Boolean,
    val responseCode: Int = 0,
    val responseBody: String = "",
    val errorMessage: String = ""
)
