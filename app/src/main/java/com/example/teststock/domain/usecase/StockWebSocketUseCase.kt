package com.example.teststock.domain.usecase

import com.example.teststock.domain.repository.StockRepository
import com.example.teststock.data.remote.dto.WebSocketResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 股票 WebSocket 連線 UseCase
 * 負責 WebSocket 相關操作
 */
class StockWebSocketUseCase @Inject constructor(
    private val stockRepository: StockRepository
) {
    
    /**
     * 建立 WebSocket 連線
     */
    fun connect(): Flow<WebSocketResponse> {
        return stockRepository.connect()
    }
    
    /**
     * 訂閱股票聚合資料
     */
    suspend fun subscribeAggregates(symbols: List<String>) {
        stockRepository.subscribeAggregates(symbols)
    }
    
    /**
     * 取消訂閱股票聚合資料
     */
    suspend fun unsubscribeAggregates(symbols: List<String>) {
        stockRepository.unsubscribeAggregates(symbols)
    }
    
    /**
     * 取消所有訂閱
     */
    suspend fun unsubscribeAll() {
        stockRepository.unsubscribeAll()
    }
    
    /**
     * 發送心跳
     */
    suspend fun sendHeartbeat() {
        stockRepository.sendHeartbeat()
    }
    
    /**
     * 斷開連線
     */
    suspend fun disconnect() {
        stockRepository.disconnect()
    }
    
    /**
     * 取得連線狀態
     */
    fun getConnectionStatus(): StateFlow<com.example.teststock.data.repository.ConnectionStatus> {
        return stockRepository.getConnectionStatus()
    }
    
    /**
     * 取得 WebSocket 訊息
     */
    fun getWebSocketMessage(): SharedFlow<WebSocketResponse> {
        return stockRepository.getWebSocketMessage()
    }
}
