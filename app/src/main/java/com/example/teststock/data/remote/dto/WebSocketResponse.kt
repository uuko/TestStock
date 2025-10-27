package com.example.teststock.data.remote.dto

import com.example.teststock.domain.model.*

/**
 * WebSocket 回應訊息
 */
sealed class WebSocketResponse {
    data class Authenticated(val message: String) : WebSocketResponse()
    data class Error(val message: String) : WebSocketResponse()
    data class Heartbeat(val time: String) : WebSocketResponse()
    data class Pong(val time: String, val state: String? = null) : WebSocketResponse()
    data class Subscribed(val data: Any) : WebSocketResponse()
    data class Unsubscribed(val data: Any) : WebSocketResponse()
    data class Subscriptions(val subscriptions: List<SubscriptionInfo>) : WebSocketResponse()
    
    // 股票資料訊息
    data class Trade(val trade: StockTrade) : WebSocketResponse()
    data class Candle(val candle: StockCandle) : WebSocketResponse()
    data class Book(val book: StockBook) : WebSocketResponse()
    data class Aggregate(val aggregate: StockAggregate) : WebSocketResponse()
    data class Index(val index: StockIndex) : WebSocketResponse()
    data class Snapshot(val snapshot: com.example.teststock.domain.model.StockSnapshot) : WebSocketResponse()
}

data class SubscriptionInfo(
    val id: String,
    val channel: String,
    val symbol: String
)
