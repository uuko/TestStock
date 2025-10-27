package com.example.teststock.data.remote.constants

/**
 * WebSocket 相關常數
 */
object WebSocketConstants {
    /**
     * 富果行情 WebSocket 連線 URL
     */
    const val FUGLE_WEBSOCKET_URL = "wss://api.fugle.tw/marketdata/v1.0/stock/streaming"
    
    /**
     * WebSocket Ping 間隔時間（秒）
     */
    const val PING_INTERVAL_SECONDS = 30L
    
    /**
     * WebSocket 正常關閉代碼
     */
    const val NORMAL_CLOSE_CODE = 1000
    
    /**
     * WebSocket 正常關閉原因
     */
    const val NORMAL_CLOSE_REASON = "正常關閉"
    
    /**
     * 客戶端 Ping 間隔時間（秒）
     * 建議 60-120 秒，避免與 OkHttp 底層 Ping 衝突
     */
    const val CLIENT_PING_INTERVAL_SECONDS = 60L
}
