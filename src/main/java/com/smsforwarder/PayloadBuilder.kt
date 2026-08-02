package com.smsforwarder

import org.json.JSONObject

object PayloadBuilder {
    const val DEFAULT_TEMPLATE = """{"text":"{{text}}","timestamp":{{timestamp}},"from":"{{from}}"}"""

    /**
     * آماده‌سازی متن برای گیرندهٔ مارک‌داون‌دار:
     * - NONE: بدون تغییر
     * - CODE_BLOCK: داخل ``` تا به‌صورت کد دیده شود
     * - ESCAPE: کاراکترهای مارک‌داون escape می‌شوند تا مثلاً #hello عیناً چاپ شود
     */
    fun prepareText(text: String, mode: MarkdownMode): String {
        return when (mode) {
            MarkdownMode.NONE -> text
            MarkdownMode.CODE_BLOCK -> "```\n$text\n```"
            MarkdownMode.ESCAPE -> escapeMarkdown(text)
        }
    }

    /** برای سازگاری با کد قدیمی. */
    fun prepareText(text: String, markdownCodeBlock: Boolean): String {
        return prepareText(
            text,
            if (markdownCodeBlock) MarkdownMode.CODE_BLOCK else MarkdownMode.NONE
        )
    }

    /**
     * Escape کاراکترهای رایج مارک‌داون تا به‌صورت متن خام رندر شوند.
     * مثال: #hello → \#hello
     */
    fun escapeMarkdown(text: String): String {
        val special = setOf(
            '\\', '`', '*', '_', '{', '}', '[', ']', '(', ')',
            '#', '+', '-', '.', '!', '|', '>', '~'
        )
        val result = StringBuilder(text.length * 2)
        for (c in text) {
            if (c in special) {
                result.append('\\')
            }
            result.append(c)
        }
        return result.toString()
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
