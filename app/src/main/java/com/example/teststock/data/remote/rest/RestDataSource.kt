package com.example.teststock.data.remote.rest

import com.example.teststock.domain.model.Stock
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REST API 資料來源介面
 */
interface RestDataSource {
    suspend fun getTicker(symbol: String): ApiResult<QuoteResponse>
    suspend fun getTrades(symbol: String, date: String? = null, limit: Int = 100): ApiResult<TradesResponse>
}

/**
 * Fugle REST API 資料來源實作
 */
@Singleton
class FugleRestDataSource @Inject constructor(
    private val fugleRestService: FugleRestService,
    gson: Gson
) : BaseRestDataSource(gson), RestDataSource {
    
    override suspend fun getTicker(symbol: String): ApiResult<QuoteResponse> {
        return callApi {
            fugleRestService.getQuote(symbol)
        }
    }
    
    override suspend fun getTrades(symbol: String, date: String?, limit: Int): ApiResult<TradesResponse> {
        return callApi {
            fugleRestService.getTrades(symbol, date, limit)
        }
    }
}
