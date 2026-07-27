package com.smsforwarder

data class LogEntry(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val type: LogType,
    val message: String,
    val sender: String = "",
    val smsBody: String = "",
    val webhookUrl: String = "",
    val conditionName: String = "",
    val httpMethod: String = "",
    val payload: String = "",
    val responseCode: Int = 0,
    val success: Boolean = false
) {
    enum class LogType {
        SMS_RECEIVED,
        CONDITION_MATCHED,
        FORWARDING,
        HTTP_POST_SENT,
        HTTP_POST_FAILED,
        HTTP_REQUEST_SENT,
        HTTP_REQUEST_FAILED,
        ERROR
    }
}
