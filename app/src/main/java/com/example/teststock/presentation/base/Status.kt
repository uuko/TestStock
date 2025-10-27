package com.example.teststock.presentation.base

/**
 * 通用狀態類別
 */
sealed class Status<out T> {
    object Loading : Status<Nothing>()
    data class Success<T>(val data: T? = null) : Status<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Status<Nothing>()
    object Empty : Status<Nothing>()
    object Idle : Status<Nothing>()
}
