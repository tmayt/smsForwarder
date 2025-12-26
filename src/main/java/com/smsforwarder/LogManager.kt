package com.smsforwarder

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LogManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(LOG_PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val LOG_PREFS_NAME = "sms_forwarder_logs"
        private const val KEY_LOGS = "logs"
        private const val MAX_LOGS = 500  // حداکثر تعداد لاگ
    }
    
    fun addLog(logEntry: LogEntry) {
        val logs = getLogs().toMutableList()
        logs.add(0, logEntry)  // اضافه کردن به ابتدای لیست
        
        // محدود کردن تعداد لاگ‌ها
        if (logs.size > MAX_LOGS) {
            logs.removeAt(logs.size - 1)
        }
        
        val json = gson.toJson(logs)
        prefs.edit().putString(KEY_LOGS, json).apply()
    }
    
    fun getLogs(): List<LogEntry> {
        val json = prefs.getString(KEY_LOGS, null) ?: return emptyList()
        val type = object : TypeToken<List<LogEntry>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun clearLogs() {
        prefs.edit().remove(KEY_LOGS).apply()
    }
    
    fun getLogsByType(type: LogEntry.LogType): List<LogEntry> {
        return getLogs().filter { it.type == type }
    }
}

