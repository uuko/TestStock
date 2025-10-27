package com.example.teststock.domain.usecase

import com.example.teststock.data.remote.dto.WebSocketResponse
import com.example.teststock.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * WebSocket 連線 UseCase
 * 包含業務邏輯：連線重試、錯誤處理、狀態管理
 */
class ConnectWebSocketUseCase @Inject constructor(
    private val stockRepository: StockRepository
) {
    
    /**
     * 建立 WebSocket 連線，包含業務邏輯
     */
    fun execute(): Flow<WebSocketResponse> {
        return stockRepository.connect()
            .onEach { response ->
                // 業務邏輯：記錄連線狀態
                when (response) {
                    is WebSocketResponse.Authenticated -> {
                        // 業務邏輯：認證成功後自動訂閱預設股票
                        // 這裡可以加入自動訂閱邏輯
                    }
                    is WebSocketResponse.Error -> {
                        // 業務邏輯：錯誤處理和重試邏輯
                        // 這裡可以加入重試機制
                    }
                    else -> {
                        // 其他業務邏輯
                    }
                }
            }
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
    fun getConnectionStatus(): kotlinx.coroutines.flow.StateFlow<com.example.teststock.data.repository.ConnectionStatus> {
        return stockRepository.getConnectionStatus()
    }
    
    /**
     * 取得 WebSocket 訊息
     */
    fun getWebSocketMessage(): kotlinx.coroutines.flow.SharedFlow<WebSocketResponse> {
        return stockRepository.getWebSocketMessage()
    }
}
