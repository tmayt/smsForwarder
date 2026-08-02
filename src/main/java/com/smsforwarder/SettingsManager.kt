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
        private const val KEY_MARKDOWN_MODE = "markdown_mode"
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

    fun getMarkdownMode(): MarkdownMode {
        val stored = prefs.getString(KEY_MARKDOWN_MODE, null)
        if (stored != null) {
            return MarkdownMode.fromPrefs(stored)
        }
        // سازگاری با تنظیم قدیمی (سوییچ بلاک کد)
        return if (prefs.getBoolean(KEY_MARKDOWN_CODE_BLOCK, false)) {
            MarkdownMode.CODE_BLOCK
        } else {
            MarkdownMode.NONE
        }
    }

    fun setMarkdownMode(mode: MarkdownMode) {
        prefs.edit()
            .putString(KEY_MARKDOWN_MODE, mode.prefsValue)
            .putBoolean(KEY_MARKDOWN_CODE_BLOCK, mode == MarkdownMode.CODE_BLOCK)
            .apply()
    }

    /** سازگاری با کد/بکاپ قدیمی. */
    fun isMarkdownCodeBlock(): Boolean {
        return getMarkdownMode() == MarkdownMode.CODE_BLOCK
    }

    fun setMarkdownCodeBlock(enabled: Boolean) {
        setMarkdownMode(if (enabled) MarkdownMode.CODE_BLOCK else MarkdownMode.NONE)
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

    /** خروجی JSON از تنظیمات وب‌هوک + شرط‌ها (لاگ‌ها شامل نمی‌شود). */
    fun exportBackupJson(): String {
        val backup = SettingsBackup(
            version = SettingsBackup.CURRENT_BACKUP_VERSION,
            enabled = isEnabled(),
            webhookUrl = getWebhookUrl(),
            customHeaders = getCustomHeaders(),
            httpMethod = getHttpMethod(),
            payloadTemplate = getPayloadTemplate(),
            markdownMode = getMarkdownMode().prefsValue,
            markdownCodeBlock = getMarkdownMode() == MarkdownMode.CODE_BLOCK,
            conditions = getConditions()
        )
        return gson.toJson(backup)
    }

    /**
     * ایمپورت از JSON. در صورت موفقیت true برمی‌گرداند.
     * کلیدهای SharedPreferences و مدل Condition عوض نمی‌شوند تا داده‌های فعلی دستگاه سالم بمانند.
     */
    fun importBackupJson(json: String): Boolean {
        return try {
            val backup = gson.fromJson(json, SettingsBackup::class.java)
                ?: return false

            val method = backup.httpMethod.uppercase().ifBlank { "POST" }
            val safeMethod = if (method in HTTP_METHODS) method else "POST"
            val template = backup.payloadTemplate.ifBlank { PayloadBuilder.DEFAULT_TEMPLATE }
            val conditions = backup.conditions.map { condition ->
                condition.copy(
                    id = condition.id.ifBlank { System.currentTimeMillis().toString() },
                    name = condition.name,
                    sender = condition.sender,
                    exactSender = condition.exactSender,
                    message = condition.message
                )
            }

            val markdownMode = when {
                backup.markdownMode.isNotBlank() -> MarkdownMode.fromPrefs(backup.markdownMode)
                backup.markdownCodeBlock -> MarkdownMode.CODE_BLOCK
                else -> MarkdownMode.NONE
            }

            prefs.edit()
                .putBoolean(KEY_ENABLED, backup.enabled)
                .putString(KEY_WEBHOOK_URL, backup.webhookUrl)
                .putString(KEY_CUSTOM_HEADERS, backup.customHeaders)
                .putString(KEY_HTTP_METHOD, safeMethod)
                .putString(KEY_PAYLOAD_TEMPLATE, template)
                .putString(KEY_MARKDOWN_MODE, markdownMode.prefsValue)
                .putBoolean(KEY_MARKDOWN_CODE_BLOCK, markdownMode == MarkdownMode.CODE_BLOCK)
                .putString(KEY_CONDITIONS, gson.toJson(conditions))
                .apply()

            true
        } catch (e: Exception) {
            Log.e("SettingsManager", "Failed to import backup", e)
            false
        }
    }
}
