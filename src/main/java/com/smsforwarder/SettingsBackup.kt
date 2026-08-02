package com.smsforwarder

/**
 * قالب فایل پشتیبان JSON.
 * فیلدها با مقدار پیش‌فرض هستند تا ایمپورت نسخه‌های قدیمی‌تر هم کار کند.
 */
data class SettingsBackup(
    val version: Int = CURRENT_BACKUP_VERSION,
    val enabled: Boolean = false,
    val webhookUrl: String = "",
    val customHeaders: String = "",
    val httpMethod: String = "POST",
    val payloadTemplate: String = PayloadBuilder.DEFAULT_TEMPLATE,
    /** none | code_block | escape — ترجیح با این فیلد است. */
    val markdownMode: String = MarkdownMode.NONE.prefsValue,
    /** سازگاری با بکاپ‌های قدیمی. */
    val markdownCodeBlock: Boolean = false,
    val conditions: List<Condition> = emptyList()
) {
    companion object {
        const val CURRENT_BACKUP_VERSION = 1
    }
}
