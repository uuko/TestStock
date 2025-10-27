package com.example.teststock.data.remote.rest

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Fugle REST API 服務
 */
interface FugleRestService {
    
    /**
     * 取得股票即時報價
     * GET /intraday/quote/{symbol}
     */
    @GET("intraday/quote/{symbol}")
    suspend fun getQuote(
        @Path("symbol") symbol: String
    ): Response<QuoteResponse>
    
    /**
     * 取得股票歷史交易資料
     * GET /intraday/trades/{symbol}
     */
    @GET("intraday/trades/{symbol}")
    suspend fun getTrades(
        @Path("symbol") symbol: String,
        @Query("date") date: String? = null,
        @Query("limit") limit: Int = 100
    ): Response<TradesResponse>
}
