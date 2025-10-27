package com.example.teststock.data.remote.rest

import com.google.gson.annotations.SerializedName

/**
 * 歷史交易資料回應
 */
data class TradesResponse(
    @SerializedName("date")
    val date: String,
    
    @SerializedName("symbol")
    val symbol: String,
    
    @SerializedName("data")
    val data: List<TradeData>
)

/**
 * 單筆交易資料
 */
data class TradeData(
    @SerializedName("time")
    val time: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("size")
    val size: Long,
    
    @SerializedName("serial")
    val serial: Long
)
