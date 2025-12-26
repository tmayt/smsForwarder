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
    
    fun sendPost(url: String, message: String, sender: String, customHeadersJson: String? = null): Boolean {
        return try {
            val json = JSONObject()
            json.put("text", message)
            json.put("timestamp", System.currentTimeMillis())
            json.put("from", sender)
            
            val requestBody = json.toString().toRequestBody(jsonMediaType)
            
            val requestBuilder = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
            
            // افزودن هدرهای سفارشی
            if (!customHeadersJson.isNullOrBlank()) {
                try {
                    val headersJson = JSONObject(customHeadersJson)
                    val keys = headersJson.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = headersJson.getString(key)
                        requestBuilder.addHeader(key, value)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("HttpService", "Error parsing custom headers", e)
                    // در صورت خطا در parse، هدرهای سفارشی نادیده گرفته می‌شوند
                }
            }
            
            val request = requestBuilder.build()
            
            val response = client.newCall(request).execute()
            val success = response.isSuccessful
            response.close()
            
            success
        } catch (e: Exception) {
            android.util.Log.e("HttpService", "Error sending POST request", e)
            false
        }
    }
}

