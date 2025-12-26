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
    
    fun sendPost(url: String, message: String): Boolean {
        return try {
            val json = JSONObject()
            json.put("message", message)
            json.put("timestamp", System.currentTimeMillis())
            
            val requestBody = json.toString().toRequestBody(jsonMediaType)
            
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build()
            
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

