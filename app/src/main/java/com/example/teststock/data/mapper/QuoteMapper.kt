package com.example.teststock.data.mapper

import com.example.teststock.data.remote.rest.QuoteResponse
import com.example.teststock.domain.model.Stock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Quote 資料轉換器
 * 負責將 REST API 的 QuoteResponse 轉換為 Domain 的 Stock
 */
@Singleton
class QuoteMapper @Inject constructor() {
    
    /**
     * 將 QuoteResponse 轉換為 Stock
     */
    fun mapToStock(quoteResponse: QuoteResponse): Stock {
        return Stock(
            symbol = quoteResponse.symbol,
            name = quoteResponse.name,
            price = quoteResponse.lastPrice ?: quoteResponse.closePrice ?: quoteResponse.referencePrice,
            change = quoteResponse.change,
            changePercent = quoteResponse.changePercent,
            volume = quoteResponse.total?.tradeVolume ?: 0L,
            lastUpdateTime = quoteResponse.date
        )
    }
}
