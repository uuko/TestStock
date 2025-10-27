package com.example.teststock.data.remote.rest

/**
 * API 結果封裝類別
 */
sealed class ApiResult<T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Empty<T>(val code: Int) : ApiResult<T>()
    data class Failure<T>(
        val error: ApiResponse?,
        val code: Int,
        val message: String?,
        val exception: Exception? = null,
        val errorBody: okhttp3.ResponseBody? = null,
    ) : ApiResult<T>()
    data class ErrorException<T>(val exception: Exception) : ApiResult<T>()
    data class NoPermission<T>(val scope: String) : ApiResult<T>()

    // 輔助方法，複製 Failure 或 ErrorException 的狀態
    fun <R> copy(): ApiResult<R> = when (this) {
        is Success -> throw IllegalStateException("Cannot copy Success")
        is Empty -> Empty(code)
        is Failure -> Failure(error, code, message, exception, errorBody)
        is ErrorException -> ErrorException(exception)
        is NoPermission -> NoPermission(scope)
    }
}

/**
 * API 錯誤回應
 */
data class ApiResponse(
    val message: String?,
    val code: Int? = null
)
