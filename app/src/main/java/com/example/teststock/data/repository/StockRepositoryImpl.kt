package com.example.teststock.data.repository

import com.example.teststock.data.local.LocalDataSource
import com.example.teststock.data.mapper.QuoteMapper
import com.example.teststock.data.network.NetworkMonitor
import com.example.teststock.data.remote.RemoteDataSource
import com.example.teststock.data.remote.dto.SubscriptionInfo
import com.example.teststock.data.remote.dto.WebSocketResponse
import com.example.teststock.data.remote.rest.RestDataSource
import com.example.teststock.domain.model.Stock
import com.example.teststock.domain.model.StockAggregate
import com.example.teststock.domain.model.StockTrade
import com.example.teststock.util.StockConverter
import com.example.teststock.domain.repository.StockRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 股票資料 Repository 實現
 * 支援多資料來源和離線模式
 */
@Singleton
class StockRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val webSocketManager: WebSocketManager,
    private val restDataSource: RestDataSource,
    private val quoteMapper: QuoteMapper,
    private val networkMonitor: NetworkMonitor
) : StockRepository {
    
    /**
     * 取得所有股票資料
     * 優先從本地資料庫取得，網路可用時同步遠端資料
     */
    override fun getAllStocks(): Flow<List<Stock>> = combine(
        localDataSource.getAllStocks(),
        networkMonitor.isOnline()
    ) { localStocks, isOnline ->
        if (isOnline && localStocks.isEmpty()) {
            // 如果線上且本地無資料，觸發遠端同步
            syncRemoteData()
        }
        localStocks
    }.distinctUntilChanged()
    
    /**
     * 取得用戶選擇的股票
     */
    override fun getUserSelectedStocks(): Flow<List<Stock>> = combine(
        localDataSource.getUserSelectedStocks(),
        networkMonitor.isOnline()
    ) { localStocks, isOnline ->
        if (isOnline && localStocks.isEmpty()) {
            // 如果線上且本地無資料，觸發遠端同步
            syncRemoteData()
        }
        localStocks
    }.distinctUntilChanged()
    
    /**
     * 取得預設股票
     */
    override fun getDefaultStocks(): Flow<List<Stock>> {
        return localDataSource.getDefaultStocks()
    }
    
    /**
     * 根據代號取得股票
     */
    override suspend fun getStockBySymbol(symbol: String): Stock? {
        return localDataSource.getStockBySymbol(symbol)
    }
    
    /**
     * 取得股票即時報價
     */
    override suspend fun getTicker(symbol: String): com.example.teststock.data.remote.rest.ApiResult<Stock> {
        return restDataSource.getTicker(symbol).let { result ->
            when (result) {
                is com.example.teststock.data.remote.rest.ApiResult.Success -> {
                    val stock = quoteMapper.mapToStock(result.data)
                    com.example.teststock.data.remote.rest.ApiResult.Success(stock)
                }
                is com.example.teststock.data.remote.rest.ApiResult.Empty -> {
                    com.example.teststock.data.remote.rest.ApiResult.Empty(result.code)
                }
                is com.example.teststock.data.remote.rest.ApiResult.Failure -> {
                    com.example.teststock.data.remote.rest.ApiResult.Failure(
                        error = result.error,
                        code = result.code,
                        message = result.message,
                        exception = result.exception,
                        errorBody = result.errorBody
                    )
                }
                is com.example.teststock.data.remote.rest.ApiResult.ErrorException -> {
                    com.example.teststock.data.remote.rest.ApiResult.ErrorException(result.exception)
                }
                is com.example.teststock.data.remote.rest.ApiResult.NoPermission -> {
                    com.example.teststock.data.remote.rest.ApiResult.NoPermission(result.scope)
                }
            }
        }
    }
    
    /**
     * 建立 WebSocket 連線
     */
    override fun connect(): Flow<WebSocketResponse> {
        return webSocketManager.connect()
    }
    
    /**
     * 訂閱股票交易資料
     */
    override suspend fun subscribeTrades(symbols: List<String>) {
        if (networkMonitor.isCurrentlyOnline()) {
            webSocketManager.subscribeTrades(symbols)
        }
    }
    
    /**
     * 訂閱股票聚合資料
     */
    override suspend fun subscribeAggregates(symbols: List<String>) {
        if (networkMonitor.isCurrentlyOnline()) {
            webSocketManager.subscribeAggregates(symbols)
        }
    }

    /**
     * 取消訂閱股票聚合資料
     */
    override suspend fun unsubscribeAggregates(symbols: List<String>) {
        if (networkMonitor.isCurrentlyOnline()) {
            webSocketManager.unsubscribeAggregates(symbols)
        }
    }

    /**
     * 取消所有訂閱
     */
    override suspend fun unsubscribeAll() {
        webSocketManager.unsubscribeAll()
    }
    
    /**
     * 發送心跳
     */
    override suspend fun sendHeartbeat() {
        webSocketManager.sendHeartbeat()
    }
    
    /**
     * 斷開連線
     */
    override suspend fun disconnect() {
        webSocketManager.disconnect()
    }
    
    /**
     * 取得連線狀態
     */
    override fun getConnectionStatus(): StateFlow<ConnectionStatus> {
        return webSocketManager.getConnectionStatus()
    }
    
    /**
     * 取得 WebSocket 訊息
     */
    override fun getWebSocketMessage(): SharedFlow<WebSocketResponse> {
        return webSocketManager.getWebSocketMessage()
    }
    
    
    /**
     * 同步遠端資料
     */
    private suspend fun syncRemoteData() {
        // 這裡可以實現從遠端 API 同步資料的邏輯
        // 例如：從 REST API 取得股票基本資料
    }
}

/**
 * 連線狀態
 */
enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    AUTHENTICATED,
    SUBSCRIBED,
    ERROR
}
