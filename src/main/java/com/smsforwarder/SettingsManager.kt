package com.smsforwarder

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "sms_filter_prefs"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_WEBHOOK_URL = "webhook_url"
        private const val KEY_CUSTOM_HEADERS = "custom_headers"
        private const val KEY_CONDITIONS = "conditions"
    }
    
    fun isEnabled(): Boolean {
        return prefs.getBoolean(KEY_ENABLED, false)
    }
    
    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }
    
    fun getWebhookUrl(): String {
        return prefs.getString(KEY_WEBHOOK_URL, "") ?: ""
    }
    
    fun setWebhookUrl(url: String) {
        prefs.edit().putString(KEY_WEBHOOK_URL, url).apply()
    }
    
    fun getCustomHeaders(): String {
        return prefs.getString(KEY_CUSTOM_HEADERS, "") ?: ""
    }
    
    fun setCustomHeaders(headers: String) {
        prefs.edit().putString(KEY_CUSTOM_HEADERS, headers).apply()
    }
    
    fun getConditions(): List<Condition> {
        val json = prefs.getString(KEY_CONDITIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<Condition>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun saveConditions(conditions: List<Condition>) {
        val json = gson.toJson(conditions)
        prefs.edit().putString(KEY_CONDITIONS, json).apply()
    }
    
    fun addCondition(condition: Condition) {
        val conditions = getConditions().toMutableList()
        conditions.add(condition)
        saveConditions(conditions)
    }
    
    fun updateCondition(condition: Condition) {
        val conditions = getConditions().toMutableList()
        val index = conditions.indexOfFirst { it.id == condition.id }
        if (index >= 0) {
            conditions[index] = condition
            saveConditions(conditions)
        }
    }
    
    fun deleteCondition(conditionId: String) {
        val conditions = getConditions().toMutableList()
        conditions.removeAll { it.id == conditionId }
        saveConditions(conditions)
    }
}
