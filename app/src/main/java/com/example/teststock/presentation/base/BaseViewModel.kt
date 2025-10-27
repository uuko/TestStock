package com.example.teststock.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

/**
 * 基礎 ViewModel
 * 提供通用的 loading 計數器和狀態管理
 */
abstract class BaseViewModel : ViewModel() {
    
    // Loading 計數器（線程安全）
    private val _loadingCount = AtomicInteger(0)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    

    
    /**
     * 開始 loading
     */
    protected fun startLoading() {
        val newCount = _loadingCount.incrementAndGet()
        _isLoading.value = newCount > 0
    }
    
    /**
     * 結束 loading
     */
    protected fun stopLoading() {
        val newCount = _loadingCount.decrementAndGet()
        if (newCount < 0) {
            // 防止計數器變成負數
            _loadingCount.set(0)
            _isLoading.value = false
        } else {
            _isLoading.value = newCount > 0
        }
    }
    
    /**
     * 執行帶有 loading 狀態的操作
     */
    protected fun <T> executeWithLoading(
        operation: suspend () -> T,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                startLoading()
                val result = operation()
                onSuccess(result)
            } catch (e: Exception) {
                onError(e)
            } finally {
                stopLoading()
            }
        }
    }

    /**
     * 執行帶有 loading 狀態的操作
     */
    protected fun <T> executeWithOutLoading(
        operation: suspend () -> T,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val result = operation()
                onSuccess(result)
            } catch (e: Exception) {
                onError(e)
            } finally {
            }
        }
    }
    
    /**
     * 執行帶有 loading 狀態的 Flow 操作
     */
    protected fun <T> executeFlowWithLoading(
        operation: suspend () -> kotlinx.coroutines.flow.Flow<T>,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                startLoading()
                operation().collect { result ->
                    onSuccess(result)
                }
            } catch (e: Exception) {
                onError(e)
            } finally {
                stopLoading()
            }
        }
    }
    

    
    override fun onCleared() {
        super.onCleared()
        // 清理資源
        _loadingCount.set(0)
    }
}
