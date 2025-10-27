package com.example.teststock.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 股票交易資料
 */
data class TradeData(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("price") val price: Double,
    @SerializedName("volume") val volume: Long,
    @SerializedName("time") val time: String,
    @SerializedName("side") val side: String? = null
)

/**
 * K線資料
 */
data class CandleData(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("open") val open: Double,
    @SerializedName("high") val high: Double,
    @SerializedName("low") val low: Double,
    @SerializedName("close") val close: Double,
    @SerializedName("volume") val volume: Long,
    @SerializedName("time") val time: String
)

/**
 * 最佳五檔資料
 */
data class BookData(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("bids") val bids: List<BookLevel>,
    @SerializedName("asks") val asks: List<BookLevel>,
    @SerializedName("time") val time: String
)

data class BookLevel(
    @SerializedName("price") val price: Double,
    @SerializedName("volume") val volume: Long
)

/**
 * 聚合資料
 */
data class AggregateData(
    @SerializedName("date") val date: String,
    @SerializedName("type") val type: String,
    @SerializedName("exchange") val exchange: String,
    @SerializedName("market") val market: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("referencePrice") val referencePrice: Double?,
    @SerializedName("previousClose") val previousClose: Double?,
    @SerializedName("openPrice") val openPrice: Double?,
    @SerializedName("openTime") val openTime: Long?,
    @SerializedName("highPrice") val highPrice: Double?,
    @SerializedName("highTime") val highTime: Long?,
    @SerializedName("lowPrice") val lowPrice: Double?,
    @SerializedName("lowTime") val lowTime: Long?,
    @SerializedName("closePrice") val closePrice: Double?,
    @SerializedName("closeTime") val closeTime: Long?,
    @SerializedName("avgPrice") val avgPrice: Double?,
    @SerializedName("change") val change: Double,
    @SerializedName("changePercent") val changePercent: Double,
    @SerializedName("amplitude") val amplitude: Double?,
    @SerializedName("lastPrice") val lastPrice: Double,
    @SerializedName("lastSize") val lastSize: Long?,
    @SerializedName("bids") val bids: List<BookLevel>,
    @SerializedName("asks") val asks: List<BookLevel>,
    @SerializedName("total") val total: TotalData,
    @SerializedName("lastTrade") val lastTrade: LastTradeData?,
    @SerializedName("lastTrial") val lastTrial: LastTrialData?,
    @SerializedName("isLimitDownPrice") val isLimitDownPrice: Boolean?,
    @SerializedName("isLimitUpPrice") val isLimitUpPrice: Boolean?,
    @SerializedName("isLimitDownBid") val isLimitDownBid: Boolean?,
    @SerializedName("isLimitUpBid") val isLimitUpBid: Boolean?,
    @SerializedName("isLimitDownAsk") val isLimitDownAsk: Boolean?,
    @SerializedName("isLimitUpAsk") val isLimitUpAsk: Boolean?,
    @SerializedName("isLimitDownHalt") val isLimitDownHalt: Boolean?,
    @SerializedName("isLimitUpHalt") val isLimitUpHalt: Boolean?,
    @SerializedName("isTrial") val isTrial: Boolean?,
    @SerializedName("isDelayedOpen") val isDelayedOpen: Boolean?,
    @SerializedName("isDelayedClose") val isDelayedClose: Boolean?,
    @SerializedName("isContinuous") val isContinuous: Boolean?,
    @SerializedName("isOpen") val isOpen: Boolean?,
    @SerializedName("isClose") val isClose: Boolean?,
    @SerializedName("serial") val serial: Long?,
    @SerializedName("lastUpdated") val lastUpdated: Long
)

data class TotalData(
    @SerializedName("tradeValue") val tradeValue: Long,
    @SerializedName("tradeVolume") val tradeVolume: Long,
    @SerializedName("tradeVolumeAtBid") val tradeVolumeAtBid: Long,
    @SerializedName("tradeVolumeAtAsk") val tradeVolumeAtAsk: Long,
    @SerializedName("transaction") val transaction: Long,
    @SerializedName("time") val time: Long
)

data class LastTradeData(
    @SerializedName("bid") val bid: Double,
    @SerializedName("ask") val ask: Double,
    @SerializedName("price") val price: Double,
    @SerializedName("size") val size: Long,
    @SerializedName("time") val time: Long,
    @SerializedName("serial") val serial: Long
)

data class LastTrialData(
    @SerializedName("bid") val bid: Double,
    @SerializedName("ask") val ask: Double,
    @SerializedName("price") val price: Double,
    @SerializedName("size") val size: Long,
    @SerializedName("time") val time: Long,
    @SerializedName("serial") val serial: Long
)

/**
 * 指數資料
 */
data class IndexData(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("price") val price: Double,
    @SerializedName("change") val change: Double,
    @SerializedName("changePercent") val changePercent: Double,
    @SerializedName("time") val time: String
)

/**
 * 快照資料（初始資料）
 */
data class SnapshotData(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("type") val type: String,
    @SerializedName("exchange") val exchange: String,
    @SerializedName("market") val market: String,
    @SerializedName("price") val price: Double,
    @SerializedName("size") val size: Long,
    @SerializedName("bid") val bid: Double,
    @SerializedName("ask") val ask: Double,
    @SerializedName("volume") val volume: Long,
    @SerializedName("isContinuous") val isContinuous: Boolean,
    @SerializedName("time") val time: Long,
    @SerializedName("serial") val serial: Long
)
