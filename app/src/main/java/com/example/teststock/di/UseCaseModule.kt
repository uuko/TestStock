package com.example.teststock.di

import com.example.teststock.data.local.LocalDataSource
import com.example.teststock.data.local.datastore.UserPreferences
import com.example.teststock.domain.repository.StockRepository
import com.example.teststock.domain.usecase.GetUserStocksUseCase
import com.example.teststock.domain.usecase.ConnectWebSocketUseCase
import com.example.teststock.domain.usecase.StockBusinessUseCase
import com.example.teststock.domain.usecase.ToggleFavoriteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * UseCase 相關依賴注入模組
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetUserStocksUseCase(
        stockRepository: StockRepository
    ): GetUserStocksUseCase {
        return GetUserStocksUseCase(stockRepository)
    }

    @Provides
    @Singleton
    fun provideConnectWebSocketUseCase(
        stockRepository: StockRepository
    ): ConnectWebSocketUseCase {
        return ConnectWebSocketUseCase(stockRepository)
    }

    @Provides
    @Singleton
    fun provideStockBusinessUseCase(): StockBusinessUseCase {
        return StockBusinessUseCase()
    }

    @Provides
    @Singleton
    fun provideToggleFavoriteUseCase(
        userPreferences: UserPreferences,
        localDataSource: LocalDataSource
    ): ToggleFavoriteUseCase {
        return ToggleFavoriteUseCase(userPreferences, localDataSource)
    }
}
