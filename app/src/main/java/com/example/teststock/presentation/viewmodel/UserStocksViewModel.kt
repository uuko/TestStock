package com.example.teststock.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.teststock.data.local.datastore.UserPreferences
import com.example.teststock.data.remote.rest.ApiResult
import com.example.teststock.domain.model.Stock
import com.example.teststock.data.local.LocalDataSource
import com.example.teststock.domain.repository.StockRepository
import com.example.teststock.domain.usecase.ToggleFavoriteUseCase
import com.example.teststock.presentation.base.BaseViewModel
import com.example.teststock.presentation.base.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * 用戶股票頁面的 ViewModel
 */
@HiltViewModel
class UserStocksViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val stockRepository: StockRepository,
    private val localDataSource: LocalDataSource,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : BaseViewModel() {
    
    private val _stockLoadingStatus = MutableStateFlow<Status<List<Stock>>>(Status.Idle)
    val stockLoadingStatus: StateFlow<Status<List<Stock>>> = _stockLoadingStatus.asStateFlow()
    
    private val _navigationStatus = MutableSharedFlow<Status<Stock>>()
    val navigationStatus: SharedFlow<Status<Stock>> = _navigationStatus.asSharedFlow()
    
    private val _operationStatus = MutableSharedFlow<Status<String>>()
    val operationStatus: SharedFlow<Status<String>> = _operationStatus.asSharedFlow()
    
    private val _defaultStocks = MutableStateFlow<List<Stock>>(emptyList())
    val defaultStocks: StateFlow<List<Stock>> = _defaultStocks.asStateFlow()

    
    init {
        initializeDefaultStocks()
    }
    
    /**
     * 初始化預設股票
     */
    private fun initializeDefaultStocks() {
        // 預設的20個股票代號列表
        val defaultSymbols = listOf(
            "2330", "2317", "2454", "6505", "2308", "2881", "2882", "2886", "2891", "2892",
            "1101", "1102", "1216", "1301", "1303", "1326", "1402", "1476", "1504", "1605"
        )
        
        // 呼叫 loadStockDetails 來載入預設股票
        loadStockDetails(defaultSymbols)
    }
    
    
    /**
     * 載入股票詳細資訊
     */
    private fun loadStockDetails(symbols: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            _stockLoadingStatus.value = Status.Loading
            
            try {
                val stocks = mutableListOf<Stock>()
                var hasError = false
                var errorMessage = ""
                
                // 取得用戶已選擇的股票代號
                val selectedSymbols = userPreferences.selectedStocks.first()
                
                // 使用 async 並行執行所有 API 調用
                val results = symbols.map { symbol ->
                    async {
                        symbol to stockRepository.getTicker(symbol)
                    }
                }
                
                // 等待所有結果並處理
                results.forEach { deferred ->
                    val (symbol, result) = deferred.await()
                    when (result) {
                        is ApiResult.Success -> {
                            // 檢查是否為用戶收藏的股票
                            val isFavorite = selectedSymbols.contains(symbol)
                            val stockWithFavorite = result.data.copy(isUserSelected = isFavorite)
                            stocks.add(stockWithFavorite)
                        }
                        is ApiResult.Empty -> {
                            android.util.Log.w("UserStocksViewModel", "股票 $symbol 無資料")
                        }
                        is ApiResult.Failure -> {
                            android.util.Log.e("UserStocksViewModel", "載入股票 $symbol 失敗: ${result.message}")
                            hasError = true
                            errorMessage = "載入股票 $symbol 失敗: ${result.message}"
                        }
                        is ApiResult.ErrorException -> {
                            android.util.Log.e("UserStocksViewModel", "載入股票 $symbol 異常: ${result.exception.message}")
                            hasError = true
                            errorMessage = "載入股票 $symbol 異常: ${result.exception.message}"
                        }
                        is ApiResult.NoPermission -> {
                            android.util.Log.e("UserStocksViewModel", "載入股票 $symbol 權限不足: ${result.scope}")
                            hasError = true
                            errorMessage = "載入股票 $symbol 權限不足: ${result.scope}"
                        }
                    }
                }
                
                // 更新預設股票列表
                _defaultStocks.value = stocks
                
                // 將股票資料存到資料庫中，並標記用戶選擇的股票
                stocks.forEach { stock ->
                    val isUserSelected = selectedSymbols.contains(stock.symbol)
                    // 保存股票資料到資料庫
                    localDataSource.saveStock(stock, isUserSelected = isUserSelected, isDefault = true)
                }
                
                // 根據結果設置狀態
                _stockLoadingStatus.value = when {
                    hasError -> Status.Error(errorMessage)
                    stocks.isNotEmpty() -> Status.Success(stocks)
                    else -> Status.Empty
                }
            } catch (e: Exception) {
                android.util.Log.e("UserStocksViewModel", "載入股票詳細資訊失敗: ${e.message}")
                _stockLoadingStatus.value = Status.Error("載入股票詳細資訊失敗: ${e.message}")
            } finally {
                stopLoading()
            }
        }
    }
    
    /**
     * 移除股票
     */
    fun removeStock(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            try {
                userPreferences.removeStock(symbol)
                _operationStatus.emit(Status.Success("已移除 $symbol"))
            } catch (e: Exception) {
                android.util.Log.e("UserStocksViewModel", "移除股票失敗: ${e.message}")
                _operationStatus.emit(Status.Error("移除股票失敗: ${e.message}", e))
            } finally {
                stopLoading()
            }
        }
    }
    
    /**
     * 清空所有股票
     */
    fun clearAllStocks() {
        viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            try {
                userPreferences.clearSelectedStocks()
                _operationStatus.emit(Status.Success("已清空所有股票"))
            } catch (e: Exception) {
                android.util.Log.e("UserStocksViewModel", "清空股票失敗: ${e.message}")
                _operationStatus.emit(Status.Error("清空股票失敗: ${e.message}", e))
            } finally {
                stopLoading()
            }
        }
    }
    
    /**
     * 切換最愛狀態
     */
    fun toggleFavorite(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // 使用 UseCase 處理最愛狀態切換
            toggleFavoriteUseCase.toggleFavorite(symbol)
            
            // 更新現有股票列表的最愛狀態
            updateStocksFavoriteStatus()
        }
    }
    
    /**
     * 更新股票列表的最愛狀態
     */
    private fun updateStocksFavoriteStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val selectedSymbols = userPreferences.selectedStocks.first()
                val updatedStocks = _defaultStocks.value.map { stock ->
                    val isFavorite = selectedSymbols.contains(stock.symbol)
                    // 只更新記憶體中的狀態，不更新資料庫
                    // 資料庫的更新由 toggleFavorite 方法處理
                    stock.copy(isUserSelected = isFavorite)
                }
                _defaultStocks.value = updatedStocks
                _stockLoadingStatus.value = Status.Success(updatedStocks)
            } catch (e: Exception) {
                android.util.Log.e("UserStocksViewModel", "更新最愛狀態失敗: ${e.message}")
            }
        }
    }
    
    /**
     * 導航到股票詳細頁面
     */
    fun navigateToStockDetail(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            _navigationStatus.emit(Status.Loading)
            
            try {
                when (val result = stockRepository.getTicker(symbol)) {
                    is ApiResult.Success -> {
                        // 導航到詳細頁面
                        _navigationStatus.emit(Status.Success(result.data))
                    }
                    is ApiResult.Empty -> {
                        _navigationStatus.emit(Status.Error("股票 $symbol 無資料"))
                    }
                    is ApiResult.Failure -> {
                        _navigationStatus.emit(Status.Error("載入失敗：${result.message}", result.exception))
                    }
                    is ApiResult.ErrorException -> {
                        _navigationStatus.emit(Status.Error("載入異常：${result.exception.message}", result.exception))
                    }
                    is ApiResult.NoPermission -> {
                        _navigationStatus.emit(Status.Error("權限不足：${result.scope}"))
                    }
                }
            } catch (error: Exception) {
                _navigationStatus.emit(Status.Error("載入失敗：${error.message}", error))
            } finally {
                stopLoading()
            }
        }
    }
}
