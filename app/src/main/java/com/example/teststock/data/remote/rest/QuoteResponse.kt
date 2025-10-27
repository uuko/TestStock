package com.example.teststock.data.remote.rest

import com.google.gson.annotations.SerializedName

/**
 * Quote API 回應資料
 */
data class QuoteResponse(
    @SerializedName("date") val date: String,
    @SerializedName("type") val type: String,
    @SerializedName("exchange") val exchange: String,
    @SerializedName("market") val market: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("referencePrice") val referencePrice: Double,
    @SerializedName("previousClose") val previousClose: Double,
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
    @SerializedName("lastPrice") val lastPrice: Double?,
    @SerializedName("lastSize") val lastSize: Long?,
    @SerializedName("bids") val bids: List<BidAskLevel>?,
    @SerializedName("asks") val asks: List<BidAskLevel>?,
    @SerializedName("total") val total: TotalInfo?,
    @SerializedName("lastTrade") val lastTrade: TradeInfo?,
    @SerializedName("lastTrial") val lastTrial: TradeInfo?,
    @SerializedName("tradingHalt") val tradingHalt: TradingHalt?,
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
    @SerializedName("lastUpdated") val lastUpdated: Long?,
    @SerializedName("serial") val serial: Long?
)

data class BidAskLevel(
    @SerializedName("price") val price: Double,
    @SerializedName("size") val size: Long
)

data class TotalInfo(
    @SerializedName("tradeValue") val tradeValue: Long,
    @SerializedName("tradeVolume") val tradeVolume: Long,
    @SerializedName("tradeVolumeAtBid") val tradeVolumeAtBid: Long,
    @SerializedName("tradeVolumeAtAsk") val tradeVolumeAtAsk: Long,
    @SerializedName("transaction") val transaction: Long,
    @SerializedName("time") val time: Long
)

data class TradeInfo(
    @SerializedName("bid") val bid: Double?,
    @SerializedName("ask") val ask: Double?,
    @SerializedName("price") val price: Double,
    @SerializedName("size") val size: Long,
    @SerializedName("time") val time: Long,
    @SerializedName("serial") val serial: Long?
)

data class TradingHalt(
    @SerializedName("isHalted") val isHalted: Boolean,
    @SerializedName("time") val time: Long
)
