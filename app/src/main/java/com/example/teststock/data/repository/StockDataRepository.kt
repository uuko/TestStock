package com.example.teststock.data.repository

import com.example.teststock.data.remote.dto.WebSocketResponse
import com.example.teststock.domain.model.Stock
import com.example.teststock.domain.model.StockTrade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 股票資料 Repository - 管理股票資料的狀態
 */
@Singleton
class StockDataRepository @Inject constructor() {
    
    private val _stockTrades = MutableStateFlow<Map<String, StockTrade>>(emptyMap())
    val stockTrades: StateFlow<Map<String, StockTrade>> = _stockTrades.asStateFlow()
    
    private val _stockAggregates = MutableStateFlow<Map<String, com.example.teststock.domain.model.StockAggregate>>(emptyMap())
    val stockAggregates: StateFlow<Map<String, com.example.teststock.domain.model.StockAggregate>> = _stockAggregates.asStateFlow()
    
    private val _stockList = MutableStateFlow<List<Stock>>(emptyList())
    val stockList: StateFlow<List<Stock>> = _stockList.asStateFlow()
    
    /**
     * 更新股票交易資料
     */
    fun updateStockTrade(trade: StockTrade) {
        android.util.Log.d("StockDataRepository", "更新股票交易資料: ${trade.symbol} - 價格: ${trade.price}")
        val currentTrades = _stockTrades.value.toMutableMap()
        currentTrades[trade.symbol] = trade
        _stockTrades.value = currentTrades
        
        // 更新股票列表
        updateStockList()
    }
    
    /**
     * 更新股票聚合資料
     */
    fun updateStockAggregate(aggregate: com.example.teststock.domain.model.StockAggregate) {
        val currentAggregates = _stockAggregates.value.toMutableMap()
        currentAggregates[aggregate.symbol] = aggregate
        _stockAggregates.value = currentAggregates
        
        // 更新股票列表
        updateStockList()
    }
    
    /**
     * 更新股票列表
     */
    private fun updateStockList() {
        val trades = _stockTrades.value
        val aggregates = _stockAggregates.value
        
        val stockList = trades.keys.union(aggregates.keys).map { symbol ->
            val trade = trades[symbol]
            val aggregate = aggregates[symbol]
            
            Stock(
                symbol = symbol,
                name = getStockName(symbol),
                price = trade?.price ?: aggregate?.price ?: 0.0,
                change = aggregate?.change ?: 0.0,
                changePercent = aggregate?.changePercent ?: 0.0,
                volume = trade?.volume ?: aggregate?.volume ?: 0L,
                lastUpdateTime = trade?.time ?: aggregate?.time ?: ""
            )
        }.sortedBy { it.symbol }
        
        android.util.Log.d("StockDataRepository", "更新股票列表: ${stockList.size} 筆")
        stockList.forEach { stock ->
            android.util.Log.d("StockDataRepository", "股票: ${stock.symbol} - ${stock.name} - 價格: ${stock.price}")
        }
        
        _stockList.value = stockList
    }
    
    /**
     * 取得股票名稱
     */
    private fun getStockName(symbol: String): String {
        return when (symbol) {
            "2330" -> "台積電"
            "2317" -> "鴻海"
            "2454" -> "聯發科"
            else -> symbol
        }
    }
    
    /**
     * 清除所有資料
     */
    fun clearAllData() {
        _stockTrades.value = emptyMap()
        _stockAggregates.value = emptyMap()
        _stockList.value = emptyList()
    }
}
