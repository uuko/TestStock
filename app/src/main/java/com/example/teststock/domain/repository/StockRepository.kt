package com.example.teststock.domain.repository

import com.example.teststock.data.remote.dto.WebSocketResponse
import com.example.teststock.domain.model.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 股票資料 Repository 介面
 */
interface StockRepository {
    
    /**
     * 取得所有股票資料
     */
    fun getAllStocks(): Flow<List<Stock>>
    
    /**
     * 取得用戶選擇的股票
     */
    fun getUserSelectedStocks(): Flow<List<Stock>>
    
    /**
     * 取得預設股票
     */
    fun getDefaultStocks(): Flow<List<Stock>>
    
    /**
     * 根據代號取得股票
     */
    suspend fun getStockBySymbol(symbol: String): Stock?
    
    /**
     * 取得股票即時報價
     */
    suspend fun getTicker(symbol: String): com.example.teststock.data.remote.rest.ApiResult<Stock>
    
    /**
     * 建立 WebSocket 連線
     */
    fun connect(): Flow<WebSocketResponse>
    
    /**
     * 訂閱股票交易資料
     */
    suspend fun subscribeTrades(symbols: List<String>)
    
    /**
     * 訂閱股票聚合資料
     */
    suspend fun subscribeAggregates(symbols: List<String>)

    /**
     * 取消訂閱股票聚合資料
     */
    suspend fun unsubscribeAggregates(symbols: List<String>)
    
    /**
     * 取消所有訂閱
     */
    suspend fun unsubscribeAll()
    
    /**
     * 發送心跳
     */
    suspend fun sendHeartbeat()
    
    /**
     * 斷開連線
     */
    suspend fun disconnect()
    
    /**
     * 取得連線狀態
     */
    fun getConnectionStatus(): StateFlow<com.example.teststock.data.repository.ConnectionStatus>
    
    /**
     * 取得 WebSocket 訊息
     */
    fun getWebSocketMessage(): SharedFlow<WebSocketResponse>
}

