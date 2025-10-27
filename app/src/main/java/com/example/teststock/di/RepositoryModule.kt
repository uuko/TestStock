package com.example.teststock.di

import com.example.teststock.data.local.LocalDataSource
import com.example.teststock.data.network.NetworkMonitor
import com.example.teststock.data.remote.RemoteDataSource
import com.example.teststock.data.remote.rest.RestDataSource
import com.example.teststock.data.mapper.QuoteMapper
import com.example.teststock.data.repository.StockRepositoryImpl
import com.example.teststock.data.repository.WebSocketManager
import com.example.teststock.domain.repository.StockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository 相關依賴注入模組
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWebSocketManager(
        @FugleRemoteDataSource remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource
    ): WebSocketManager {
        return WebSocketManager(remoteDataSource, localDataSource)
    }

    @Provides
    @Singleton
    fun provideStockRepository(
        localDataSource: LocalDataSource,
        webSocketManager: WebSocketManager,
        restDataSource: RestDataSource,
        quoteMapper: QuoteMapper,
        networkMonitor: NetworkMonitor
    ): StockRepository {
        return StockRepositoryImpl(localDataSource, webSocketManager, restDataSource, quoteMapper, networkMonitor)
    }
}
