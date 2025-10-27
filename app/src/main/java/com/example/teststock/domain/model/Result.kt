package com.example.teststock.domain.model

/**
 * Domain Layer 的結果封裝
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data class Empty(val message: String = "無資料") : Result<Nothing>()
}

