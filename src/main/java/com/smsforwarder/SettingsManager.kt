package com.smsforwarder

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        // نام prefs و کلیدها را تغییر ندهید تا تنظیمات بعد از آپدیت حفظ شوند
        private const val PREFS_NAME = "sms_filter_prefs"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_WEBHOOK_URL = "webhook_url"
        private const val KEY_CUSTOM_HEADERS = "custom_headers"
        private const val KEY_CONDITIONS = "conditions"
        private const val KEY_HTTP_METHOD = "http_method"
        private const val KEY_PAYLOAD_TEMPLATE = "payload_template"
        private const val KEY_MARKDOWN_CODE_BLOCK = "markdown_code_block"
        private const val KEY_PREFS_VERSION = "prefs_version"

        /** نسخه اسکیمای تنظیمات؛ فقط وقتی ساختار داده عوض شد افزایش دهید و مهاجرت بنویسید. */
        private const val CURRENT_PREFS_VERSION = 1

        val HTTP_METHODS = listOf("POST", "GET", "PUT", "PATCH", "DELETE")
    }

    init {
        migrateIfNeeded()
    }

    private fun migrateIfNeeded() {
        val stored = prefs.getInt(KEY_PREFS_VERSION, 0)
        if (stored >= CURRENT_PREFS_VERSION) return

        // مهاجرت‌های آینده اینجا اضافه شوند (مثلاً rename فیلدها).
        // فعلاً فقط نسخه را ثبت می‌کنیم تا تنظیمات قبلی دست‌نخورده بمانند.
        prefs.edit().putInt(KEY_PREFS_VERSION, CURRENT_PREFS_VERSION).apply()
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

    fun getHttpMethod(): String {
        return prefs.getString(KEY_HTTP_METHOD, "POST") ?: "POST"
    }

    fun setHttpMethod(method: String) {
        prefs.edit().putString(KEY_HTTP_METHOD, method.uppercase()).apply()
    }

    fun getPayloadTemplate(): String {
        return prefs.getString(KEY_PAYLOAD_TEMPLATE, PayloadBuilder.DEFAULT_TEMPLATE)
            ?: PayloadBuilder.DEFAULT_TEMPLATE
    }

    fun setPayloadTemplate(template: String) {
        prefs.edit().putString(KEY_PAYLOAD_TEMPLATE, template).apply()
    }

    fun isMarkdownCodeBlock(): Boolean {
        return prefs.getBoolean(KEY_MARKDOWN_CODE_BLOCK, false)
    }

    fun setMarkdownCodeBlock(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MARKDOWN_CODE_BLOCK, enabled).apply()
    }

    fun getConditions(): List<Condition> {
        val json = prefs.getString(KEY_CONDITIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<Condition>>() {}.type
        return try {
            gson.fromJson<List<Condition>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            // در صورت خطای پارس، دادهٔ قبلی را پاک نکن — فقط لیست خالی برگردان
            Log.e("SettingsManager", "Failed to parse conditions; keeping raw prefs", e)
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
