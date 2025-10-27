package com.example.teststock.di

import com.example.teststock.data.mapper.QuoteMapper
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 資料轉換器相關依賴注入模組
 */
@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideQuoteMapper(): QuoteMapper {
        return QuoteMapper()
    }
}
