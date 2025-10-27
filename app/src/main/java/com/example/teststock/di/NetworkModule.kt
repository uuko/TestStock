package com.example.teststock.di

import android.content.Context
import com.example.teststock.data.network.NetworkMonitor
import com.example.teststock.data.remote.RemoteDataSource
import com.example.teststock.data.remote.rest.FugleRestService
import com.example.teststock.data.remote.rest.RestDataSource
import com.example.teststock.data.remote.rest.FugleRestDataSource
import com.example.teststock.data.remote.interceptor.ApiKeyInterceptor
import com.example.teststock.data.remote.websocket.FugleWebSocketService
import com.example.teststock.data.remote.constants.ApiConstants
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * 純網路相關依賴注入模組
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideFugleWebSocketService(
        gson: Gson
    ): FugleWebSocketService {
        return FugleWebSocketService(
            apiKey = ApiConstants.API_KEY,
            gson = gson
        )
    }

    @Provides
    @Singleton
    @FugleRemoteDataSource
    fun provideFugleRemoteDataSource(
        webSocketService: FugleWebSocketService
    ): RemoteDataSource {
        return com.example.teststock.data.remote.FugleRemoteDataSource(webSocketService)
    }

    @Provides
    @Singleton
    @MockRemoteDataSource
    fun provideMockRemoteDataSource(): RemoteDataSource {
        return com.example.teststock.data.remote.MockRemoteDataSource()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .connectTimeout(ApiConstants.CONNECT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(ApiConstants.READ_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(ApiConstants.WRITE_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.FUGLE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideFugleRestService(retrofit: Retrofit): FugleRestService {
        return retrofit.create(FugleRestService::class.java)
    }

    @Provides
    @Singleton
    fun provideRestDataSource(
        fugleRestService: FugleRestService,
        gson: Gson
    ): RestDataSource {
        return FugleRestDataSource(fugleRestService, gson)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FugleRemoteDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockRemoteDataSource
