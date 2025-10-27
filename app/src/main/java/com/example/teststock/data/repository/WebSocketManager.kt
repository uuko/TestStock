package com.example.teststock.data.repository

import com.example.teststock.data.remote.RemoteDataSource
import com.example.teststock.data.model.SubscriptionInfo
import com.example.teststock.data.remote.dto.WebSocketResponse
import com.example.teststock.data.local.LocalDataSource
import com.example.teststock.util.StockConverter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WebSocket 連線管理器
 * 負責 WebSocket 連線狀態和訂閱管理
 */
@Singleton
class WebSocketManager @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {
    
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    private val _webSocketMessage = MutableSharedFlow<WebSocketResponse>()
    
    // 訂閱資訊快取，key: "${channel}_${symbol}"
    private val subscriptions = mutableMapOf<String, SubscriptionInfo>()
    
    // 使用 SupervisorJob 來管理 coroutine，避免一個失敗影響其他
    private val supervisorJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + supervisorJob)
    
    /**
     * 取得連線狀態
     */
    fun getConnectionStatus(): StateFlow<ConnectionStatus> {
        return _connectionStatus.asStateFlow()
    }
    
    /**
     * 取得 WebSocket 訊息
     */
    fun getWebSocketMessage(): SharedFlow<WebSocketResponse> {
        return _webSocketMessage.asSharedFlow()
    }
    
    /**
     * 建立 WebSocket 連線
     */
    fun connect(): kotlinx.coroutines.flow.Flow<WebSocketResponse> {
        return remoteDataSource.connect()
            .onEach { response ->
                scope.launch {
                    _webSocketMessage.emit(response)
                    handleWebSocketResponse(response)
                }
            }
            .onStart {
                _connectionStatus.value = ConnectionStatus.CONNECTING
            }
            .catch { error ->
                _connectionStatus.value = ConnectionStatus.ERROR
                android.util.Log.e("WebSocketManager", "WebSocket 連接錯誤: ${error.message}")
                throw error
            }
    }
    
    /**
     * 訂閱股票交易資料
     */
    suspend fun subscribeTrades(symbols: List<String>) {
        // 記錄要訂閱的股票和頻道資訊
        symbols.forEach { symbol ->
            val key = "trades_$symbol"
            // 如果已經有訂閱，先取消舊的
            subscriptions[key]?.let { oldSubscription ->
                remoteDataSource.unsubscribeMultiple(listOf(oldSubscription.id))
            }
        }
        
        remoteDataSource.subscribeTrades(symbols)
    }
    
    /**
     * 訂閱股票聚合資料
     */
    suspend fun subscribeAggregates(symbols: List<String>) {
        // 記錄要訂閱的股票和頻道資訊
        symbols.forEach { symbol ->
            val key = "aggregates_$symbol"
            // 如果已經有訂閱，先取消舊的
            subscriptions[key]?.let { oldSubscription ->
                remoteDataSource.unsubscribeMultiple(listOf(oldSubscription.id))
            }
        }
        
        remoteDataSource.subscribeAggregates(symbols)
    }
    
    /**
     * 取消訂閱股票交易資料
     */
    suspend fun unsubscribeTrades(symbols: List<String>) {
        symbols.forEach { symbol ->
            val key = "trades_$symbol"
            subscriptions[key]?.let { oldSubscription ->
                remoteDataSource.unsubscribeMultiple(listOf(oldSubscription.id))
                subscriptions.remove(key)
            }
        }
    }
    
    /**
     * 取消訂閱股票聚合資料
     */
    suspend fun unsubscribeAggregates(symbols: List<String>) {
        symbols.forEach { symbol ->
            val key = "aggregates_$symbol"
            subscriptions[key]?.let { oldSubscription ->
                remoteDataSource.unsubscribeMultiple(listOf(oldSubscription.id))
                subscriptions.remove(key)
            }
        }
    }
    
    /**
     * 取消所有訂閱
     */
    suspend fun unsubscribeAll() {
        if (subscriptions.isNotEmpty()) {
            val channelIds = subscriptions.values.map { it.id }
            remoteDataSource.unsubscribeMultiple(channelIds)
            subscriptions.clear()
            _connectionStatus.value = ConnectionStatus.AUTHENTICATED
        }
    }
    
    /**
     * 發送心跳
     */
    suspend fun sendHeartbeat() {
        remoteDataSource.sendHeartbeat()
    }
    
    /**
     * 斷開連線
     */
    suspend fun disconnect() {
        remoteDataSource.disconnect()
    }
    
    /**
     * 清理資源
     */
    fun cleanup() {
        supervisorJob.cancel()
    }
    
    /**
     * 處理 WebSocket 回應
     */
    private suspend fun handleWebSocketResponse(response: WebSocketResponse) {
        when (response) {
            is WebSocketResponse.Authenticated -> {
                _connectionStatus.value = ConnectionStatus.AUTHENTICATED
            }
            is WebSocketResponse.Subscribed -> {
                // 記錄訂閱資訊
                try {
                    val dataList = when (response.data) {
                        is List<*> -> response.data
                        is Map<*, *> -> listOf(response.data)
                        else -> emptyList()
                    }
                    
                    dataList.forEach { item ->
                        if (item is Map<*, *>) {
                            val id = item["id"] as? String
                            val channel = item["channel"] as? String
                            val symbol = item["symbol"] as? String
                            
                            if (id != null && channel != null && symbol != null) {
                                val subscriptionInfo = SubscriptionInfo(id, channel, symbol)
                                val key = "${channel}_${symbol}"
                                subscriptions[key] = subscriptionInfo
                            }
                        }
                    }
                    
                    if (dataList.isNotEmpty()) {
                        _connectionStatus.value = ConnectionStatus.SUBSCRIBED
                    }
                } catch (e: Exception) {
                    android.util.Log.e("WebSocketManager", "解析訂閱回應失敗: ${e.message}")
                }
            }
            is WebSocketResponse.Error -> {
                _connectionStatus.value = ConnectionStatus.ERROR
            }
            is WebSocketResponse.Trade -> {
                val stock = StockConverter.convertTradeToStock(response.trade)
                localDataSource.updateStockData(stock)
            }
            is WebSocketResponse.Snapshot -> {
                val stock = StockConverter.convertSnapshotToStock(response.snapshot)
                localDataSource.updateStockData(stock)
            }
            is WebSocketResponse.Aggregate -> {
                val stock = StockConverter.convertAggregateToStock(response.aggregate)
                localDataSource.updateStockData(stock)
            }
            else -> {
                // 其他回應類型
            }
        }
    }
}
