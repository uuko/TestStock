package com.example.teststock.data.remote.websocket

import android.util.Log
import com.example.teststock.data.remote.constants.WebSocketConstants
import com.example.teststock.data.remote.dto.WebSocketMessage
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import okhttp3.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 富果行情 WebSocket 服務
 */
@Singleton
class FugleWebSocketService @Inject constructor(
    private val apiKey: String,
    private val gson: Gson
) {
    private val client = OkHttpClient.Builder()
        .pingInterval(WebSocketConstants.PING_INTERVAL_SECONDS, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val messageChannel = Channel<WebSocketMessage>(Channel.UNLIMITED)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var pingJob: Job? = null

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "WebSocket 連線已建立")
            authenticate()
            startAutoPing()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "收到訊息: $text")
            try {
                val message = parseMessage(text)
                scope.launch {
                    messageChannel.send(message)
                }
            } catch (e: Exception) {
                scope.launch {
                    messageChannel.send(WebSocketMessage.Error(
                        data = WebSocketMessage.ErrorData("解析失敗: ${e.message}")
                    ))
                }
                // 設置類的成員變量為 null，這樣才能重新連線
                this@FugleWebSocketService.webSocket = null
                Log.e(TAG, "解析訊息失敗: ${e.message}")
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket 正在關閉: $code - $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket 已關閉: $code - $reason")
            // 設置類的成員變量為 null，這樣才能重新連線
            this@FugleWebSocketService.webSocket = null
            stopAutoPing()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket 連線失敗: ${t.message}")
            scope.launch {
                messageChannel.send(WebSocketMessage.Error(
                    data = WebSocketMessage.ErrorData("連線失敗: ${t.message}")
                ))
            }
            // 設置類的成員變量為 null，這樣才能重新連線
            this@FugleWebSocketService.webSocket = null
            stopAutoPing()
        }
    }

    /**
     * 建立 WebSocket 連線
     */
    fun connect(): Flow<WebSocketMessage> {
        // 如果已有連線且狀態正常，直接返回現有的 Flow
        if (webSocket != null) {
            return messageChannel.receiveAsFlow()
        }
        
        val request = Request.Builder()
            .url(WebSocketConstants.FUGLE_WEBSOCKET_URL)
            .build()
        
        webSocket = client.newWebSocket(request, webSocketListener)
        return messageChannel.receiveAsFlow()
    }

    /**
     * 身份驗證
     */
    private fun authenticate() {
        val authMessage = WebSocketMessage.Auth(
            data = WebSocketMessage.AuthData(apiKey)
        )
        sendMessage(authMessage)
    }

    /**
     * 訂閱頻道
     */
    fun subscribe(channel: String, symbol: String, intradayOddLot: Boolean = false) {
        val subscribeMessage = WebSocketMessage.Subscribe(
            data = WebSocketMessage.SubscribeData(
                channel = channel,
                symbol = symbol,
                intradayOddLot = if (intradayOddLot) true else null
            )
        )
        sendMessage(subscribeMessage)
    }

    /**
     * 訂閱多個股票
     */
    fun subscribeMultiple(channel: String, symbols: List<String>) {
        val subscribeMessage = WebSocketMessage.Subscribe(
            data = WebSocketMessage.SubscribeData(
                channel = channel,
                symbols = symbols
            )
        )
        sendMessage(subscribeMessage)
    }

    /**
     * 取消訂閱
     */
    fun unsubscribe(channelId: String) {
        val unsubscribeMessage = WebSocketMessage.Unsubscribe(
            data = WebSocketMessage.UnsubscribeData(id = channelId)
        )
        sendMessage(unsubscribeMessage)
    }

    /**
     * 取消多個訂閱
     */
    fun unsubscribeMultiple(channelIds: List<String>) {
        val unsubscribeMessage = WebSocketMessage.Unsubscribe(
            data = WebSocketMessage.UnsubscribeData(ids = channelIds)
        )
        sendMessage(unsubscribeMessage)
    }


    /**
     * 取得訂閱資訊
     */
    fun getSubscriptions() {
        val subscriptionsMessage = WebSocketMessage.Subscriptions(
            data = emptyList()
        )
        sendMessage(subscriptionsMessage)
    }

    /**
     * 發送 Ping
     */
    fun ping(state: String? = null) {
        val pingMessage = WebSocketMessage.Ping(
            data = WebSocketMessage.PingData(state)
        )
        sendMessage(pingMessage)
    }
    
    /**
     * 啟動自動 Ping
     */
    private fun startAutoPing() {
        stopAutoPing() // 先停止現有的 Ping
        pingJob = scope.launch {
            while (isActive && webSocket != null) {
                delay(WebSocketConstants.CLIENT_PING_INTERVAL_SECONDS * 1000)
                if (webSocket != null) {
                    Log.d(TAG, "發送自動 Ping")
                    ping("auto_ping")
                }
            }
        }
    }
    
    /**
     * 停止自動 Ping
     */
    private fun stopAutoPing() {
        pingJob?.cancel()
        pingJob = null
    }

    /**
     * 發送訊息
     */
    private fun sendMessage(message: WebSocketMessage) {
        try {
            val json = gson.toJson(message)
            webSocket?.send(json)
            Log.d(TAG, "發送訊息: $json")
        } catch (e: Exception) {
            Log.e(TAG, "發送訊息失敗: ${e.message}")
        }
    }

    /**
     * 解析訊息
     */
    private fun parseMessage(text: String): WebSocketMessage {
        return try {
            val jsonObject = gson.fromJson(text, com.google.gson.JsonObject::class.java)
            val event = jsonObject.get("event")?.asString ?: ""

            when (event) {
                "authenticated" -> gson.fromJson(text, WebSocketMessage.Authenticated::class.java)
                "error" -> gson.fromJson(text, WebSocketMessage.Error::class.java)
                "heartbeat" -> gson.fromJson(text, WebSocketMessage.Heartbeat::class.java)
                "pong" -> gson.fromJson(text, WebSocketMessage.Pong::class.java)
                "subscribed" -> gson.fromJson(text, WebSocketMessage.Subscribed::class.java)
                "unsubscribed" -> gson.fromJson(text, WebSocketMessage.Unsubscribed::class.java)
                "subscriptions" -> gson.fromJson(text, WebSocketMessage.Subscriptions::class.java)
                "data" -> {
                    // 處理資料事件，根據 channel 判斷類型
                    val channel = jsonObject.get("channel")?.asString ?: ""
                    when (channel) {
                        "trades" -> {
                            // 交易資料實際上是快照格式
                            val dataObject = jsonObject.getAsJsonObject("data")
                            val snapshotData = gson.fromJson(dataObject, com.example.teststock.data.remote.dto.SnapshotData::class.java)
                            WebSocketMessage.SnapshotData(snapshotData)
                        }
                        "candles" -> {
                            val candleData = gson.fromJson(text, com.example.teststock.data.remote.dto.CandleData::class.java)
                            WebSocketMessage.CandleData(candleData)
                        }
                        "books" -> {
                            val bookData = gson.fromJson(text, com.example.teststock.data.remote.dto.BookData::class.java)
                            WebSocketMessage.BookData(bookData)
                        }
                        "aggregates" -> {
                            val dataObject = jsonObject.getAsJsonObject("data")
                            val aggregateData = gson.fromJson(dataObject, com.example.teststock.data.remote.dto.AggregateData::class.java)
                            WebSocketMessage.AggregateData(aggregateData)
                        }
                        "indices" -> {
                            val indexData = gson.fromJson(text, com.example.teststock.data.remote.dto.IndexData::class.java)
                            WebSocketMessage.IndexData(indexData)
                        }
                        else -> {
                            // 預設處理為快照資料
                            val snapshotData = gson.fromJson(text, com.example.teststock.data.remote.dto.SnapshotData::class.java)
                            WebSocketMessage.SnapshotData(snapshotData)
                        }
                    }
                }
                "trades" -> {
                    // 處理交易資料
                    val tradeData = gson.fromJson(text, com.example.teststock.data.remote.dto.TradeData::class.java)
                    WebSocketMessage.TradeData(tradeData)
                }
                "snapshot" -> {
                    // 處理快照資料（初始資料）- 使用 AggregateData 格式
                    val snapshotJsonObject = gson.fromJson(text, com.google.gson.JsonObject::class.java)
                    val dataObject = snapshotJsonObject.getAsJsonObject("data")
                    val aggregateData = gson.fromJson(dataObject, com.example.teststock.data.remote.dto.AggregateData::class.java)
                    WebSocketMessage.AggregateData(aggregateData)
                }
                "candles" -> {
                    // 處理K線資料
                    val candleData = gson.fromJson(text, com.example.teststock.data.remote.dto.CandleData::class.java)
                    WebSocketMessage.CandleData(candleData)
                }
                "books" -> {
                    // 處理委託簿資料
                    val bookData = gson.fromJson(text, com.example.teststock.data.remote.dto.BookData::class.java)
                    WebSocketMessage.BookData(bookData)
                }
                "aggregates" -> {
                    // 處理聚合資料
                    val aggregateData = gson.fromJson(text, com.example.teststock.data.remote.dto.AggregateData::class.java)
                    WebSocketMessage.AggregateData(aggregateData)
                }
                "indices" -> {
                    // 處理指數資料
                    val indexData = gson.fromJson(text, com.example.teststock.data.remote.dto.IndexData::class.java)
                    WebSocketMessage.IndexData(indexData)
                }
                else -> {
                    // 處理其他類型的訊息
                    WebSocketMessage.Error(
                        data = WebSocketMessage.ErrorData("未知的訊息類型: $event")
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析訊息失敗: ${e.message}")
            WebSocketMessage.Error(
                data = WebSocketMessage.ErrorData("解析失敗: ${e.message}")
            )
        }
    }

    /**
     * 關閉連線
     */
    fun disconnect() {
        stopAutoPing()
        webSocket?.close(WebSocketConstants.NORMAL_CLOSE_CODE, WebSocketConstants.NORMAL_CLOSE_REASON)
        webSocket = null
        scope.cancel()
    }

    companion object {
        private const val TAG = "FugleWebSocketService"
    }
}
