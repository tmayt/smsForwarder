package com.smsforwarder

import org.json.JSONObject

object PayloadBuilder {
    const val DEFAULT_TEMPLATE = """{"text":"{{text}}","timestamp":{{timestamp}},"from":"{{from}}"}"""

    /** اگر روشن باشد متن داخل بلاک کد مارک‌داون قرار می‌گیرد تا گیرنده به‌صورت monospace/raw ببیند. */
    fun prepareText(text: String, markdownCodeBlock: Boolean): String {
        if (!markdownCodeBlock) return text
        return "```\n$text\n```"
    }

    fun build(template: String, text: String, from: String, timestamp: Long): String {
        val escapedText = jsonEscape(text)
        val escapedFrom = jsonEscape(from)

        return template
            .replace("{{timestamp}}", timestamp.toString())
            .replace("{{text}}", escapedText)
            .replace("{{message}}", escapedText)
            .replace("{{from}}", escapedFrom)
    }

    private fun jsonEscape(value: String): String {
        return JSONObject.quote(value).removeSurrounding("\"")
    }
}
