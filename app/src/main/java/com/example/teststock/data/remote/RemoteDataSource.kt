package com.example.teststock.data.remote

import com.example.teststock.data.remote.dto.WebSocketResponse
import com.example.teststock.domain.model.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 遠端資料來源介面
 */
interface RemoteDataSource {
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
     * 取消訂閱股票交易資料
     */
    suspend fun unsubscribeTrades(symbols: List<String>)
    
    /**
     * 取消訂閱股票聚合資料
     */
    suspend fun unsubscribeAggregates(symbols: List<String>)
    
    /**
     * 取消訂閱
     */
    suspend fun unsubscribeAll()
    
    /**
     * 取消多個訂閱
     */
    suspend fun unsubscribeMultiple(channelIds: List<String>)
    
    /**
     * 發送心跳
     */
    suspend fun sendHeartbeat()
    
    /**
     * 斷開連線
     */
    suspend fun disconnect()
}

/**
 * 富果 WebSocket 遠端資料來源
 */
@Singleton
class FugleRemoteDataSource @Inject constructor(
    private val webSocketService: com.example.teststock.data.remote.websocket.FugleWebSocketService
) : RemoteDataSource {
    
    override fun connect(): Flow<WebSocketResponse> {
        return webSocketService.connect().map { message ->
            when (message) {
                is com.example.teststock.data.remote.dto.WebSocketMessage.Authenticated -> 
                    WebSocketResponse.Authenticated(message.data.message)
                is com.example.teststock.data.remote.dto.WebSocketMessage.Error -> 
                    WebSocketResponse.Error(message.data.message)
                is com.example.teststock.data.remote.dto.WebSocketMessage.Heartbeat -> 
                    WebSocketResponse.Heartbeat(message.data.time)
                is com.example.teststock.data.remote.dto.WebSocketMessage.Pong -> 
                    WebSocketResponse.Pong(message.data.time, message.data.state)
                is com.example.teststock.data.remote.dto.WebSocketMessage.Subscribed -> 
                    WebSocketResponse.Subscribed(message.data)
                is com.example.teststock.data.remote.dto.WebSocketMessage.Unsubscribed -> 
                    WebSocketResponse.Unsubscribed(message.data)
                is com.example.teststock.data.remote.dto.WebSocketMessage.Subscriptions -> {
                    val subscriptions = message.data as List<com.example.teststock.data.remote.dto.WebSocketMessage.SubscriptionInfo>
                    val responseSubscriptions = subscriptions.map { sub ->
                        com.example.teststock.data.remote.dto.SubscriptionInfo(
                            id = sub.id,
                            channel = sub.channel,
                            symbol = sub.symbol
                        )
                    }
                    WebSocketResponse.Subscriptions(responseSubscriptions)
                }
                is com.example.teststock.data.remote.dto.WebSocketMessage.TradeData -> {
                    val trade = com.example.teststock.domain.model.StockTrade(
                        symbol = message.data.symbol,
                        price = message.data.price,
                        volume = message.data.volume,
                        time = message.data.time,
                        side = message.data.side
                    )
                    WebSocketResponse.Trade(trade)
                }
                is com.example.teststock.data.remote.dto.WebSocketMessage.SnapshotData -> {
                    val snapshot = com.example.teststock.domain.model.StockSnapshot(
                        symbol = message.data.symbol,
                        type = message.data.type,
                        exchange = message.data.exchange,
                        market = message.data.market,
                        price = message.data.price,
                        size = message.data.size,
                        bid = message.data.bid,
                        ask = message.data.ask,
                        volume = message.data.volume,
                        isContinuous = message.data.isContinuous,
                        time = message.data.time,
                        serial = message.data.serial
                    )
                    WebSocketResponse.Snapshot(snapshot)
                }
                is com.example.teststock.data.remote.dto.WebSocketMessage.AggregateData -> {
                    val aggregate = com.example.teststock.domain.model.StockAggregate(
                        symbol = message.data.symbol,
                        price = message.data.lastPrice,
                        volume = message.data.total.tradeVolume,
                        change = message.data.change,
                        changePercent = message.data.changePercent,
                        time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                            .format(java.util.Date(message.data.lastUpdated / 1000000)),
                        name = message.data.name
                    )
                    WebSocketResponse.Aggregate(aggregate)
                }
                else -> WebSocketResponse.Error("未知的訊息類型")
            }
        }
    }
    
    override suspend fun subscribeTrades(symbols: List<String>) {
        webSocketService.subscribeMultiple("trades", symbols)
    }
    
    override suspend fun subscribeAggregates(symbols: List<String>) {
        webSocketService.subscribeMultiple("aggregates", symbols)
    }
    
    override suspend fun unsubscribeTrades(symbols: List<String>) {
        // 這個方法應該由 StockRepositoryImpl 通過 unsubscribeMultiple 來處理
        // 因為需要真正的訂閱 ID，而不是 symbols
        // TODO: 實現或移除這個方法
    }
    
    override suspend fun unsubscribeAggregates(symbols: List<String>) {
        // 這個方法應該由 StockRepositoryImpl 通過 unsubscribeMultiple 來處理
        // 因為需要真正的訂閱 ID，而不是 symbols
        // TODO: 實現或移除這個方法
    }
    
    override suspend fun unsubscribeAll() {
        // 先取得所有訂閱，然後取消
//        webSocketService.getSubscriptions()
        // 注意：這需要 WebSocket 服務返回訂閱列表，目前暫時不實現
        // TODO: 實現取得訂閱列表並取消所有訂閱的邏輯
    }
    
    override suspend fun unsubscribeMultiple(channelIds: List<String>) {
        webSocketService.unsubscribeMultiple(channelIds)
    }
    
    override suspend fun sendHeartbeat() {
        webSocketService.ping("heartbeat")
    }
    
    override suspend fun disconnect() {
        webSocketService.disconnect()
    }
}

/**
 * 模擬遠端資料來源（用於測試或離線模式）
 */
@Singleton
class MockRemoteDataSource @Inject constructor() : RemoteDataSource {
    
    override fun connect(): Flow<WebSocketResponse> {
        return kotlinx.coroutines.flow.flowOf(
            WebSocketResponse.Authenticated("模擬認證成功")
        )
    }
    
    override suspend fun subscribeTrades(symbols: List<String>) {
        // 模擬訂閱
    }
    
    override suspend fun subscribeAggregates(symbols: List<String>) {
        // 模擬訂閱
    }
    
    override suspend fun unsubscribeTrades(symbols: List<String>) {
        // 模擬取消訂閱
    }
    
    override suspend fun unsubscribeAggregates(symbols: List<String>) {
        // 模擬取消訂閱
    }
    
    override suspend fun unsubscribeAll() {
        // 模擬取消訂閱
    }
    
    override suspend fun unsubscribeMultiple(channelIds: List<String>) {
        // 模擬取消多個訂閱
    }
    
    override suspend fun sendHeartbeat() {
        // 模擬心跳
    }
    
    override suspend fun disconnect() {
        // 模擬斷開連線
    }
}

