package com.smsforwarder

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class HttpService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun sendRequest(
        url: String,
        method: String,
        message: String,
        sender: String,
        payloadTemplate: String,
        customHeadersJson: String? = null
    ): HttpResult {
        return try {
            val timestamp = System.currentTimeMillis()
            val payload = PayloadBuilder.build(
                template = payloadTemplate.ifBlank { PayloadBuilder.DEFAULT_TEMPLATE },
                text = message,
                from = sender,
                timestamp = timestamp
            )

            val httpMethod = method.uppercase()
            val requestBuilder = Request.Builder().url(url)

            when (httpMethod) {
                "GET", "DELETE" -> {
                    requestBuilder.method(httpMethod, null)
                }
                "POST", "PUT", "PATCH" -> {
                    val requestBody = payload.toRequestBody(jsonMediaType)
                    requestBuilder.method(httpMethod, requestBody)
                    requestBuilder.addHeader("Content-Type", "application/json")
                }
                else -> {
                    return HttpResult(
                        success = false,
                        errorMessage = "متد HTTP نامعتبر: $method"
                    )
                }
            }

            applyCustomHeaders(requestBuilder, customHeadersJson)

            val response = client.newCall(requestBuilder.build()).execute()
            val responseBody = response.body?.string() ?: ""
            val success = response.isSuccessful
            val result = HttpResult(
                success = success,
                responseCode = response.code,
                responseBody = responseBody.take(500),
                errorMessage = if (success) "" else "HTTP ${response.code}"
            )
            response.close()
            result
        } catch (e: Exception) {
            android.util.Log.e("HttpService", "Error sending $method request", e)
            HttpResult(success = false, errorMessage = e.message ?: "خطای ناشناخته")
        }
    }

    private fun applyCustomHeaders(
        requestBuilder: Request.Builder,
        customHeadersJson: String?
    ) {
        if (customHeadersJson.isNullOrBlank()) return

        try {
            val headersJson = JSONObject(customHeadersJson)
            val keys = headersJson.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                requestBuilder.addHeader(key, headersJson.getString(key))
            }
        } catch (e: Exception) {
            android.util.Log.e("HttpService", "Error parsing custom headers", e)
        }
    }
}
