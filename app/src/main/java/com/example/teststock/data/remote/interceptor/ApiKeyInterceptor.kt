package com.example.teststock.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

/**
 * API Key 攔截器
 * 自動為所有請求添加 X-API-KEY header
 */
class ApiKeyInterceptor @Inject constructor(
    @Named("api_key") private val apiKey: String
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader("X-API-KEY", apiKey)
            .build()
        return chain.proceed(newRequest)
    }
}
