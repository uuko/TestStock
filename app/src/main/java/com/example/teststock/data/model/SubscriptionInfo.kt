package com.example.teststock.data.model

/**
 * 訂閱資訊資料類別
 */
data class SubscriptionInfo(
    val id: String,           // channel_id
    val channel: String,      // "trades", "candles", "books"
    val symbol: String        // "2330", "2317", "2454"
)
