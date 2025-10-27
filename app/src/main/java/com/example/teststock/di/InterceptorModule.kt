package com.example.teststock.di

import com.example.teststock.data.remote.interceptor.ApiKeyInterceptor
import com.example.teststock.data.remote.constants.ApiConstants
import javax.inject.Named
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 攔截器相關依賴注入模組
 */
@Module
@InstallIn(SingletonComponent::class)
object InterceptorModule {

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): ApiKeyInterceptor {
        return ApiKeyInterceptor(ApiConstants.API_KEY)
    }
}
