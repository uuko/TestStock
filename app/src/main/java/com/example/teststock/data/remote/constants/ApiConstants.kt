package com.example.teststock.data.remote.constants

/**
 * API 相關常數
 */
object ApiConstants {
    
    /**
     * Fugle API 基礎 URL
     */
    const val FUGLE_BASE_URL = "https://api.fugle.tw/marketdata/v1.0/stock/"
    
    /**
     * API Key (實際專案中應該從環境變數或配置檔案讀取)
     */
    const val API_KEY = "MjBiNTc5ZjctNzU2ZS00MjdiLWJkMjktMjU3MzVhMDkyNjRjIGMwMzNkMWNhLTFmM2QtNDM5ZS1hNzdlLTE2Mjk2ZDUyNzg2OA=="
    
    /**
     * WebSocket URL
     */
    const val WEBSOCKET_URL = "wss://api.fugle.tw/realtime/v1.0/intraday/quote"
    
    /**
     * 請求超時時間 (秒)
     */
    const val REQUEST_TIMEOUT = 30L
    
    /**
     * 連線超時時間 (秒)
     */
    const val CONNECT_TIMEOUT = 10L
    
    /**
     * 讀取超時時間 (秒)
     */
    const val READ_TIMEOUT = 30L
    
    /**
     * 寫入超時時間 (秒)
     */
    const val WRITE_TIMEOUT = 30L
    
    /**
     * 重試次數
     */
    const val RETRY_COUNT = 3
    
    /**
     * 心跳間隔 (毫秒)
     */
    const val HEARTBEAT_INTERVAL = 30000L
    
    /**
     * 最大重連次數
     */
    const val MAX_RECONNECT_ATTEMPTS = 5
}
