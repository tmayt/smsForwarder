package com.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val settingsManager = SettingsManager(context)
            val logManager = LogManager(context)

            if (!settingsManager.isEnabled()) {
                return
            }

            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (smsMessages.isNullOrEmpty()) {
                return
            }

            // پیامک‌های چندقسمتی را به یک متن کامل بچسبان
            val sender = smsMessages[0].originatingAddress ?: ""
            val messageBody = smsMessages.joinToString("") { it.messageBody ?: "" }

            Log.d("SmsReceiver", "SMS received from: $sender, parts=${smsMessages.size}, body: $messageBody")

            val matchedCondition = matchesAnyCondition(context, sender, messageBody)
            if (matchedCondition != null) {
                val conditionName = matchedCondition.name.ifBlank { "بدون نام" }

                logManager.addLog(
                    LogEntry(
                        type = LogEntry.LogType.CONDITION_MATCHED,
                        message = "شرط «$conditionName» تطابق داشت — پیام برای فوروارد آماده شد",
                        sender = sender,
                        smsBody = messageBody,
                        conditionName = conditionName
                    )
                )

                forwardMessage(context, messageBody, sender, conditionName, logManager)
            }
        }
    }

    private fun matchesAnyCondition(context: Context, sender: String, messageBody: String): Condition? {
        val settingsManager = SettingsManager(context)
        val conditions = settingsManager.getConditions()

        if (conditions.isEmpty()) {
            return null
        }

        for (condition in conditions) {
            if (condition.matches(sender, messageBody)) {
                return condition
            }
        }

        return null
    }

    private fun forwardMessage(
        context: Context,
        messageBody: String,
        sender: String,
        conditionName: String,
        logManager: LogManager
    ) {
        val settingsManager = SettingsManager(context)
        val url = settingsManager.getWebhookUrl()
        val httpMethod = settingsManager.getHttpMethod()
        val payloadTemplate = settingsManager.getPayloadTemplate()
        val textToSend = PayloadBuilder.prepareText(
            messageBody,
            settingsManager.isMarkdownCodeBlock()
        )
        val payload = PayloadBuilder.build(payloadTemplate, textToSend, sender, System.currentTimeMillis())

        if (url.isBlank()) {
            logManager.addLog(
                LogEntry(
                    type = LogEntry.LogType.ERROR,
                    message = "فوروارد متوقف شد: آدرس وب‌هوک تنظیم نشده",
                    sender = sender,
                    smsBody = messageBody,
                    conditionName = conditionName,
                    httpMethod = httpMethod,
                    payload = payload
                )
            )
            return
        }

        logManager.addLog(
            LogEntry(
                type = LogEntry.LogType.FORWARDING,
                message = "در حال ارسال $httpMethod به وب‌هوک",
                sender = sender,
                smsBody = messageBody,
                webhookUrl = url,
                conditionName = conditionName,
                httpMethod = httpMethod,
                payload = payload
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val httpService = HttpService()
                val customHeaders = settingsManager.getCustomHeaders()
                val result = httpService.sendRequest(
                    url = url,
                    method = httpMethod,
                    message = textToSend,
                    sender = sender,
                    payloadTemplate = payloadTemplate,
                    customHeadersJson = customHeaders
                )

                if (result.success) {
                    Log.d("SmsReceiver", "HTTP $httpMethod sent successfully (${result.responseCode})")
                    logManager.addLog(
                        LogEntry(
                            type = LogEntry.LogType.HTTP_REQUEST_SENT,
                            message = "فوروارد موفق — پاسخ ${result.responseCode}",
                            sender = sender,
                            smsBody = messageBody,
                            webhookUrl = url,
                            conditionName = conditionName,
                            httpMethod = httpMethod,
                            payload = payload,
                            responseCode = result.responseCode,
                            success = true
                        )
                    )
                } else {
                    Log.e("SmsReceiver", "Failed to send HTTP $httpMethod: ${result.errorMessage}")
                    logManager.addLog(
                        LogEntry(
                            type = LogEntry.LogType.HTTP_REQUEST_FAILED,
                            message = "فوروارد ناموفق — ${result.errorMessage}",
                            sender = sender,
                            smsBody = messageBody,
                            webhookUrl = url,
                            conditionName = conditionName,
                            httpMethod = httpMethod,
                            payload = payload,
                            responseCode = result.responseCode,
                            success = false
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Error sending HTTP $httpMethod", e)
                logManager.addLog(
                    LogEntry(
                        type = LogEntry.LogType.ERROR,
                        message = "خطا در فوروارد: ${e.message}",
                        sender = sender,
                        smsBody = messageBody,
                        webhookUrl = url,
                        conditionName = conditionName,
                        httpMethod = httpMethod,
                        payload = payload,
                        success = false
                    )
                )
            }
        }
    }
}
