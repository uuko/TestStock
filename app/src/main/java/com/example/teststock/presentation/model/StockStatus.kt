package com.example.teststock.presentation.model

import com.example.teststock.domain.model.Stock

/**
 * 股票狀態
 * 用於表示股票資料的不同狀態
 */
sealed class StockStatus {
    /**
     * 載入中
     */
    object Loading : StockStatus()
    
    /**
     * 載入成功
     * @param stocks 股票列表
     */
    data class Success(val stocks: List<Stock>) : StockStatus()
    
    /**
     * 載入失敗
     * @param message 錯誤訊息
     */
    data class Error(val message: String) : StockStatus()
    
    /**
     * 空狀態（沒有股票資料）
     */
    object Empty : StockStatus()
}
