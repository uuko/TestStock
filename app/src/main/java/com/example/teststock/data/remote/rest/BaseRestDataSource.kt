package com.example.teststock.data.remote.rest

import com.google.gson.Gson
import retrofit2.Response

/**
 * 基礎 REST 資料來源類別
 * 提供通用的 API 調用方法
 */
abstract class BaseRestDataSource(
    protected val gson: Gson
) {
    
    /**
     * 通用的 API 調用方法，自動判斷是否為 Unit 類型
     * @param apiFunction API 調用函數
     */
    protected inline fun <reified T> callApi(apiFunction: () -> Response<T>): ApiResult<T> {
        return try {
            val response = apiFunction()
            if (response.isSuccessful) {
                response.body()?.let { ApiResult.Success(it) }
                    ?: if (T::class == Unit::class) {
                        // 當 T 是 Unit 時，即使 response.body() 是 null 也返回成功
                        // 因為 Unit 本身就代表無回傳值
                        ApiResult.Success(Unit as T)
                    } else {
                        // 當 T 不是 Unit 時，body 為 null 表示空結果
                        ApiResult.Empty(response.code())
                    }
            } else {
                val errorBody = response.errorBody()
                val errorResponse = errorBody?.let {
                    gson.fromJson(it.charStream(), ApiResponse::class.java)
                }
                ApiResult.Failure(
                    error = errorResponse,
                    code = response.code(),
                    message = response.message(),
                    exception = Exception("API failed with code ${response.code()}: ${errorResponse?.message}"),
                    errorBody = errorBody
                )
            }
        } catch (e: Exception) {
            ApiResult.ErrorException(e)
        }
    }
}
