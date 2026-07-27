package com.smsforwarder

data class Condition(
    val id: String = System.currentTimeMillis().toString(),
    val name: String = "",
    val sender: String = "",  // فرستنده شامل (شماره یا نام سازمانی)
    val exactSender: String = "",  // فرستنده دقیق (شماره یا نام سازمانی)
    val message: String = ""  // متن پیام (شامل)
) {
    fun matches(incomingSender: String, messageBody: String): Boolean {
        if (isEmpty()) {
            return false
        }

        var matches = true

        // بررسی فرستنده (شامل) — شماره یا نام سازمانی مثل Bank / 1000
        if (sender.isNotBlank()) {
            matches = matches && incomingSender.contains(sender, ignoreCase = true)
        }

        // بررسی فرستنده (دقیق) — بدون حساسیت به حروف بزرگ/کوچک
        if (exactSender.isNotBlank()) {
            matches = matches && incomingSender.equals(exactSender, ignoreCase = true)
        }

        // بررسی متن پیام
        if (message.isNotBlank()) {
            matches = matches && messageBody.contains(message, ignoreCase = true)
        }

        return matches
    }

    fun isEmpty(): Boolean {
        return sender.isBlank() && exactSender.isBlank() && message.isBlank()
    }
}
