package com.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val settingsManager = SettingsManager(context)
            val logManager = LogManager(context)
            
            // بررسی فعال بودن فیلتر
            if (!settingsManager.isEnabled()) {
                return
            }
            
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (smsMessage in smsMessages) {
                val sender = smsMessage.originatingAddress ?: ""
                val messageBody = smsMessage.messageBody ?: ""
                
                Log.d("SmsReceiver", "SMS received from: $sender, body: $messageBody")
                
                // ثبت لاگ دریافت SMS
                logManager.addLog(LogEntry(
                    type = LogEntry.LogType.SMS_RECEIVED,
                    message = "SMS دریافت شد",
                    sender = sender,
                    smsBody = messageBody
                ))
                
                // بررسی شرایط (OR logic - اگر هر کدام match کرد)
                val matchedCondition = matchesAnyCondition(context, sender, messageBody)
                if (matchedCondition != null) {
                    // ثبت لاگ تطابق شرط
                    logManager.addLog(LogEntry(
                        type = LogEntry.LogType.CONDITION_MATCHED,
                        message = "شرط تطابق داشت: ${matchedCondition.name}",
                        sender = sender,
                        smsBody = messageBody
                    ))
                    
                    // ارسال درخواست HTTP POST
                    sendHttpPost(context, messageBody, sender, logManager)
                }
            }
        }
    }
    
    private fun matchesAnyCondition(context: Context, sender: String, messageBody: String): Condition? {
        val settingsManager = SettingsManager(context)
        val conditions = settingsManager.getConditions()
        
        if (conditions.isEmpty()) {
            return null
        }
        
        // بررسی هر شرط - اگر هر کدام match کرد، برگردان (OR logic)
        for (condition in conditions) {
            if (condition.matches(sender, messageBody)) {
                return condition
            }
        }
        
        return null
    }
    
    private fun sendHttpPost(context: Context, messageBody: String, sender: String, logManager: LogManager) {
        val settingsManager = SettingsManager(context)
        val url = settingsManager.getWebhookUrl()
        
        if (url.isBlank()) {
            logManager.addLog(LogEntry(
                type = LogEntry.LogType.ERROR,
                message = "آدرس وب‌هوک تنظیم نشده است",
                sender = sender,
                smsBody = messageBody
            ))
            return
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val httpService = HttpService()
                val success = httpService.sendPost(url, messageBody)
                
                if (success) {
                    Log.d("SmsReceiver", "HTTP POST sent successfully")
                    logManager.addLog(LogEntry(
                        type = LogEntry.LogType.HTTP_POST_SENT,
                        message = "درخواست POST با موفقیت ارسال شد",
                        sender = sender,
                        smsBody = messageBody,
                        webhookUrl = url,
                        success = true
                    ))
                } else {
                    Log.e("SmsReceiver", "Failed to send HTTP POST")
                    logManager.addLog(LogEntry(
                        type = LogEntry.LogType.HTTP_POST_FAILED,
                        message = "ارسال درخواست POST ناموفق بود",
                        sender = sender,
                        smsBody = messageBody,
                        webhookUrl = url,
                        success = false
                    ))
                }
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Error sending HTTP POST", e)
                logManager.addLog(LogEntry(
                    type = LogEntry.LogType.ERROR,
                    message = "خطا در ارسال POST: ${e.message}",
                    sender = sender,
                    smsBody = messageBody,
                    webhookUrl = url,
                    success = false
                ))
            }
        }
    }
}
