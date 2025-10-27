package com.example.teststock.domain.usecase

import com.example.teststock.domain.model.Stock
import javax.inject.Inject

/**
 * 股票業務邏輯 UseCase
 * 負責純業務邏輯計算
 */
class StockBusinessUseCase @Inject constructor() {
    
    /**
     * 業務邏輯：檢查股票是否為熱門股票
     */
    fun isHotStock(stock: Stock): Boolean {
        return stock.volume > 1000000L && stock.changePercent > 5.0
    }
    
  
    
    /**
     * 業務邏輯：過濾用戶關注的股票
     */
    fun filterUserInterestedStocks(stocks: List<Stock>): List<Stock> {
        return stocks.filter { stock ->
            stock.isUserSelected || isHotStock(stock)
        }
    }
    
    /**
     * 業務邏輯：計算股票評分
     */
    fun calculateStockScore(stock: Stock): Int {
        var score = 0
        
        // 成交量評分
        when {
            stock.volume > 5000000L -> score += 30
            stock.volume > 1000000L -> score += 20
            stock.volume > 100000L -> score += 10
        }
        
        // 漲跌幅評分
        when {
            stock.changePercent > 5.0 -> score += 20
            stock.changePercent > 2.0 -> score += 15
            stock.changePercent > 0.0 -> score += 10
            stock.changePercent > -2.0 -> score += 5
        }
        
        // 用戶選擇加分
        if (stock.isUserSelected) {
            score += 25
        }
        
        return score.coerceIn(0, 100)
    }
}
