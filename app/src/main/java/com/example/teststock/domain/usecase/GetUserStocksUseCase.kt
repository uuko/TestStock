package com.example.teststock.domain.usecase

import com.example.teststock.domain.model.Stock
import com.example.teststock.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 取得用戶股票 UseCase
 * 包含業務邏輯：過濾、排序、驗證
 */
class GetUserStocksUseCase @Inject constructor(
    private val stockRepository: StockRepository
) {
    
    /**
     * 取得用戶選擇的股票，並應用業務邏輯
     */
    fun execute(): Flow<List<Stock>> {
        return stockRepository.getUserSelectedStocks()
            .map { stocks ->
                // 業務邏輯：過濾掉無效的股票
                stocks.filter { stock ->
                    stock.symbol.isNotBlank()
                }
                // 業務邏輯：按價格排序
                .sortedByDescending { it.price }
            }
    }
}

