package com.example.teststock.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.teststock.data.local.datastore.UserPreferences
import com.example.teststock.data.remote.dto.WebSocketResponse
import com.example.teststock.domain.model.Stock
import com.example.teststock.domain.usecase.GetUserStocksUseCase
import com.example.teststock.domain.usecase.ConnectWebSocketUseCase
import com.example.teststock.domain.usecase.StockBusinessUseCase
import com.example.teststock.domain.usecase.ToggleFavoriteUseCase
import com.example.teststock.presentation.base.BaseViewModel
import com.example.teststock.presentation.base.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 股票 ViewModel
 */
@HiltViewModel
class StockViewModel @Inject constructor(
    private val getUserStocksUseCase: GetUserStocksUseCase,
    private val connectWebSocketUseCase: ConnectWebSocketUseCase,
    private val stockBusinessUseCase: StockBusinessUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val userPreferences: UserPreferences
) : BaseViewModel() {

    // 股票狀態
    private val _stockStatus = MutableStateFlow<Status<List<Stock>>>(Status.Loading)
    val stockStatus: StateFlow<Status<List<Stock>>> = _stockStatus.asStateFlow()

    // WebSocket 狀態
    private val _webSocketStatus = MutableStateFlow<Status<String>>(Status.Idle)
    val webSocketStatus: StateFlow<Status<String>> = _webSocketStatus.asStateFlow()

    // 連線狀態
    val connectionStatus = connectWebSocketUseCase.getConnectionStatus()

    // WebSocket 訊息
    val webSocketMessage = connectWebSocketUseCase.getWebSocketMessage()


    // 追蹤是否已經自動訂閱過
    private var hasAutoSubscribed = false
    
    init {
        // 應用啟動時自動連線
        connect()
        
        // 監聽用戶選擇的股票資料變化
        viewModelScope.launch {
            getUserStocksUseCase.execute().collect { stocks ->
                _stockStatus.value = if (stocks.isEmpty()) {
                    Status.Empty
                } else {
                    Status.Success(stocks)
                }
            }
        }
        
        // 監聽連線狀態變化
        viewModelScope.launch {
            connectionStatus.collect { status ->
                handleConnectionStatusChange(status)
            }
        }
    }

    
    /**
     * 建立 WebSocket 連線
     */
    private fun connect() {
        executeFlowWithLoading(
            operation = { connectWebSocketUseCase.execute() },
            onSuccess = { message ->
                handleWebSocketMessage(message)
            },
            onError = { error ->
                _webSocketStatus.value = Status.Error("連線錯誤: ${error.message}", error)
            }
        )
    }

    /**
     * 處理連線狀態變化
     */
    private fun handleConnectionStatusChange(status: com.example.teststock.data.repository.ConnectionStatus) {
        when (status) {
            com.example.teststock.data.repository.ConnectionStatus.AUTHENTICATED -> {
                // 認證成功後自動訂閱股票（只在第一次連線時）
                if (!hasAutoSubscribed) {
                    subscribeStocks()
                    hasAutoSubscribed = true
                }
            }
            com.example.teststock.data.repository.ConnectionStatus.ERROR ->{
                // 如果斷線，重置自動訂閱標記
                hasAutoSubscribed = false
            }
            com.example.teststock.data.repository.ConnectionStatus.DISCONNECTED  -> {
                // 如果斷線，重置自動訂閱標記
                hasAutoSubscribed = false
            }
            else -> {
                // 其他狀態不需要處理
            }
        }
    }
    
    /**
     * 處理 WebSocket 訊息
     */
    private fun handleWebSocketMessage(message: WebSocketResponse) {
        when (message) {
            is WebSocketResponse.Error -> {
                _webSocketStatus.value = Status.Error(message.message)
            }
            is WebSocketResponse.Authenticated -> {
                _webSocketStatus.value = Status.Success("認證成功")
            }
            is WebSocketResponse.Subscribed -> {
                _webSocketStatus.value = Status.Success("訂閱成功")
            }
            else -> {
                // 其他訊息由 Repository 處理
            }
        }
    }

    /**
     * 訂閱股票（從 UserPreferences 獲取用戶選擇的股票）
     */
    private fun subscribeStocks() {
        executeWithLoading(
            operation = {
                // 從 UserPreferences 獲取用戶選擇的股票
                val selectedStocks = userPreferences.selectedStocks.first()
                val symbols = selectedStocks.toList()
                
                android.util.Log.d("StockViewModel", "訂閱股票: $symbols")
                if (symbols.isNotEmpty()) {
                    connectWebSocketUseCase.subscribeAggregates(symbols)
                }
                symbols
            },
            onSuccess = { symbols ->
                android.util.Log.d("StockViewModel", "成功訂閱股票: $symbols")
            },
            onError = { error ->
                _stockStatus.value = Status.Error("載入用戶選擇股票錯誤: ${error.message}", error)
            }
        )
    }

    /**
     * 發送心跳檢測
     */
    fun sendHeartbeat() {
        executeWithOutLoading(
            operation = { connectWebSocketUseCase.sendHeartbeat() },
            onSuccess = { android.util.Log.d("StockViewModel", "心跳發送成功") },
            onError = { error -> _webSocketStatus.value = Status.Error("心跳發送失敗: ${error.message}", error) }
        )
    }

    /**
     * 重新連線
     */
    fun reconnect() {
        executeWithLoading(
            operation = {
                connectWebSocketUseCase.execute()
            },
            onSuccess = { android.util.Log.d("StockViewModel", "重新連線成功") },
            onError = { error -> _webSocketStatus.value = Status.Error("重新連線失敗: ${error.message}", error) }
        )
    }

    /**
     * 取消所有訂閱
     */
    fun unsubscribeAll() {
        executeWithLoading(
            operation = { connectWebSocketUseCase.unsubscribeAll() },
            onSuccess = { android.util.Log.d("StockViewModel", "取消所有訂閱成功") },
            onError = { error -> _webSocketStatus.value = Status.Error("取消訂閱失敗: ${error.message}", error) }
        )
    }
    
    /**
     * 恢復訂閱三檔股票（連線保持不斷）
     */
    fun reSubscribe() {
        executeWithLoading(
            operation = {
                // 如果已連線，則重新訂閱（不管之前是否訂閱過）
                val currentStatus = connectionStatus.value
                if (currentStatus == com.example.teststock.data.repository.ConnectionStatus.AUTHENTICATED ||
                    currentStatus == com.example.teststock.data.repository.ConnectionStatus.SUBSCRIBED) {
                    subscribeStocks()
                }
            },
            onSuccess = { android.util.Log.d("StockViewModel", "reSubscribe 處理成功") },
            onError = { error -> _webSocketStatus.value = Status.Error("reSubscribe 處理失敗: ${error.message}", error) }
        )
    }
    
    /**
     * Fragment 生命週期：onStop
     * 取消所有訂閱以節省資源（保持 WebSocket 連線）
     */
    fun unsubscribe() {
        executeWithLoading(
            operation = {
                // 從 UserPreferences 獲取當前訂閱的股票
                val selectedStocks = userPreferences.selectedStocks.first()
                val symbols =
                    // 使用用戶選擇的股票
                    selectedStocks.toList()

                
                android.util.Log.d("StockViewModel", "取消訂閱股票: $symbols")
                connectWebSocketUseCase.unsubscribeAggregates(symbols)
                // 不重置 hasAutoSubscribed，因為這只是暫時取消訂閱
                symbols
            },
            onSuccess = { symbols ->
                android.util.Log.d("StockViewModel", "成功取消訂閱股票: $symbols")
            },
            onError = { error ->
                _webSocketStatus.value = Status.Error("載入用戶選擇股票錯誤: ${error.message}", error)
            }
        )
    }

    /**
     * 斷開連線
     */
    fun disconnect() {
        executeWithLoading(
            operation = { connectWebSocketUseCase.disconnect() },
            onSuccess = { android.util.Log.d("StockViewModel", "斷開連線成功") },
            onError = { error -> _webSocketStatus.value = Status.Error("斷開連線失敗: ${error.message}", error) }
        )
    }

    /**
     * 切換最愛狀態
     */
    fun toggleFavorite(symbol: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase.toggleFavorite(symbol)
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
