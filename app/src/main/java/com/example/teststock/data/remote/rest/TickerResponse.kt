package com.example.teststock.data.remote.rest

import com.google.gson.annotations.SerializedName

/**
 * Ticker API 回應資料
 */
data class TickerResponse(
    @SerializedName("date") val date: String,
    @SerializedName("type") val type: String,
    @SerializedName("exchange") val exchange: String,
    @SerializedName("market") val market: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("industry") val industry: String?,
    @SerializedName("securityType") val securityType: String,
    @SerializedName("previousClose") val previousClose: Double,
    @SerializedName("referencePrice") val referencePrice: Double,
    @SerializedName("limitUpPrice") val limitUpPrice: Double?,
    @SerializedName("limitDownPrice") val limitDownPrice: Double?,
    @SerializedName("canDayTrade") val canDayTrade: Boolean?,
    @SerializedName("canBuyDayTrade") val canBuyDayTrade: Boolean?,
    @SerializedName("canBelowFlatMarginShortSell") val canBelowFlatMarginShortSell: Boolean?,
    @SerializedName("canBelowFlatSBLShortSell") val canBelowFlatSBLShortSell: Boolean?,
    @SerializedName("isAttention") val isAttention: Boolean,
    @SerializedName("isDisposition") val isDisposition: Boolean,
    @SerializedName("isUnusuallyRecommended") val isUnusuallyRecommended: Boolean,
    @SerializedName("isSpecificAbnormally") val isSpecificAbnormally: Boolean,
    @SerializedName("matchingInterval") val matchingInterval: Int,
    @SerializedName("securityStatus") val securityStatus: String,
    @SerializedName("boardLot") val boardLot: Int,
    @SerializedName("tradingCurrency") val tradingCurrency: String
)
