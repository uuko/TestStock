package com.example.teststock.domain.usecase

import com.example.teststock.domain.repository.StockRepository
import com.example.teststock.domain.model.Stock
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 股票資料存取 UseCase
 * 負責與 Repository 的資料存取操作
 */
class StockDataUseCase @Inject constructor(
    private val stockRepository: StockRepository
) {
    
    /**
     * 取得所有股票
     */
    fun getAllStocks(): Flow<List<Stock>> {
        return stockRepository.getAllStocks()
    }
    
    /**
     * 取得用戶選擇的股票
     */
    fun getUserSelectedStocks(): Flow<List<Stock>> {
        return stockRepository.getUserSelectedStocks()
    }
    
    /**
     * 取得股票即時報價
     */
    suspend fun getStockTicker(symbol: String): com.example.teststock.data.remote.rest.ApiResult<Stock> {
        return stockRepository.getTicker(symbol)
    }
}
