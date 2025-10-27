package com.example.teststock.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * WebSocket 訊息基礎類別
 */
sealed class WebSocketMessage {
    data class Auth(
        @SerializedName("event") val event: String = "auth",
        @SerializedName("data") val data: AuthData
    ) : WebSocketMessage()

    data class AuthData(
        @SerializedName("apikey") val apikey: String
    )

    data class Authenticated(
        @SerializedName("event") val event: String = "authenticated",
        @SerializedName("data") val data: AuthenticatedData
    ) : WebSocketMessage()

    data class AuthenticatedData(
        @SerializedName("message") val message: String
    )

    data class Error(
        @SerializedName("event") val event: String = "error",
        @SerializedName("data") val data: ErrorData
    ) : WebSocketMessage()

    data class ErrorData(
        @SerializedName("message") val message: String
    )

    data class Heartbeat(
        @SerializedName("event") val event: String = "heartbeat",
        @SerializedName("data") val data: HeartbeatData
    ) : WebSocketMessage()

    data class HeartbeatData(
        @SerializedName("time") val time: String
    )

    data class Ping(
        @SerializedName("event") val event: String = "ping",
        @SerializedName("data") val data: PingData
    ) : WebSocketMessage()

    data class PingData(
        @SerializedName("state") val state: String? = null
    )

    data class Pong(
        @SerializedName("event") val event: String = "pong",
        @SerializedName("data") val data: PongData
    ) : WebSocketMessage()

    data class PongData(
        @SerializedName("time") val time: String,
        @SerializedName("state") val state: String? = null
    )

    data class Subscribe(
        @SerializedName("event") val event: String = "subscribe",
        @SerializedName("data") val data: SubscribeData
    ) : WebSocketMessage()

    data class SubscribeData(
        @SerializedName("channel") val channel: String,
        @SerializedName("symbol") val symbol: String? = null,
        @SerializedName("symbols") val symbols: List<String>? = null,
        @SerializedName("intradayOddLot") val intradayOddLot: Boolean? = null
    )

    data class Subscribed(
        @SerializedName("event") val event: String = "subscribed",
        @SerializedName("data") val data: Any // 可能是單一物件或陣列
    ) : WebSocketMessage()

    data class Unsubscribe(
        @SerializedName("event") val event: String = "unsubscribe",
        @SerializedName("data") val data: UnsubscribeData
    ) : WebSocketMessage()

    data class UnsubscribeData(
        @SerializedName("id") val id: String? = null,
        @SerializedName("ids") val ids: List<String>? = null
    )

    data class Unsubscribed(
        @SerializedName("event") val event: String = "unsubscribed",
        @SerializedName("data") val data: Any // 可能是單一物件或陣列
    ) : WebSocketMessage()

    data class Subscriptions(
        @SerializedName("event") val event: String = "subscriptions",
        @SerializedName("data") val data: List<SubscriptionInfo>
    ) : WebSocketMessage()

    data class SubscriptionInfo(
        @SerializedName("id") val id: String,
        @SerializedName("channel") val channel: String,
        @SerializedName("symbol") val symbol: String
    )

    // 股票資料訊息
    data class TradeData(
        @SerializedName("data") val data: com.example.teststock.data.remote.dto.TradeData
    ) : WebSocketMessage()

    data class CandleData(
        @SerializedName("data") val data: com.example.teststock.data.remote.dto.CandleData
    ) : WebSocketMessage()

    data class BookData(
        @SerializedName("data") val data: com.example.teststock.data.remote.dto.BookData
    ) : WebSocketMessage()

    data class AggregateData(
        @SerializedName("data") val data: com.example.teststock.data.remote.dto.AggregateData
    ) : WebSocketMessage()

    data class IndexData(
        @SerializedName("data") val data: com.example.teststock.data.remote.dto.IndexData
    ) : WebSocketMessage()

    data class SnapshotData(
        @SerializedName("data") val data: com.example.teststock.data.remote.dto.SnapshotData
    ) : WebSocketMessage()
}
