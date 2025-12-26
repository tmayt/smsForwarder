package com.smsforwarder

data class Condition(
    val id: String = System.currentTimeMillis().toString(),
    val name: String = "",
    val sender: String = "",  // شماره فرستنده (شامل)
    val exactSender: String = "",  // شماره فرستنده (دقیق)
    val message: String = ""  // متن پیام (شامل)
) {
    fun matches(sender: String, messageBody: String): Boolean {
        // اگر هیچ شرطی پر نشده، false برگردان
        if (sender.isBlank() && exactSender.isBlank() && message.isBlank()) {
            return false
        }
        
        var matches = true
        
        // بررسی شماره فرستنده (شامل)
        if (this.sender.isNotBlank()) {
            matches = matches && sender.contains(this.sender, ignoreCase = true)
        }
        
        // بررسی شماره فرستنده (دقیق)
        if (this.exactSender.isNotBlank()) {
            matches = matches && sender == this.exactSender
        }
        
        // بررسی متن پیام
        if (this.message.isNotBlank()) {
            matches = matches && messageBody.contains(this.message, ignoreCase = true)
        }
        
        return matches
    }
    
    fun isEmpty(): Boolean {
        return sender.isBlank() && exactSender.isBlank() && message.isBlank()
    }
}

