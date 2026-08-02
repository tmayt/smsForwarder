package com.smsforwarder

/**
 * نحوهٔ آماده‌سازی متن پیامک قبل از ارسال به وب‌هوک مارک‌داون‌دار.
 */
enum class MarkdownMode(val prefsValue: String, val labelFa: String) {
    NONE("none", "بدون تغییر"),
    CODE_BLOCK("code_block", "بلاک کد (```)"),
    ESCAPE("escape", "Escape مارک‌داون (نمایش تحت‌اللفظی)");

    companion object {
        fun fromPrefs(value: String?): MarkdownMode {
            return entries.firstOrNull { it.prefsValue == value } ?: NONE
        }

        val labels: List<String> = entries.map { it.labelFa }
    }
}
