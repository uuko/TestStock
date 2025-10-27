package com.example.teststock.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 股票領域模型
 */
data class Stock(
    val symbol: String,
    val name: String? = null,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,
    val lastUpdateTime: String,
    val isUserSelected: Boolean = false
):Serializable

/**
 * 股票交易記錄
 */
data class StockTrade(
    val symbol: String,
    val price: Double,
    val volume: Long,
    val time: String,
    val side: String? = null
)

/**
 * 股票K線
 */
data class StockCandle(
    val symbol: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long,
    val time: String
)

/**
 * 股票委託簿
 */
data class StockBook(
    val symbol: String,
    val bids: List<BookLevel>,
    val asks: List<BookLevel>,
    val time: String
)

data class BookLevel(
    val price: Double,
    val volume: Long
)

/**
 * 股票聚合資料
 */
data class StockAggregate(
    val symbol: String,
    val price: Double,
    val volume: Long,
    val change: Double,
    val changePercent: Double,
    val time: String,
    val name: String
)

/**
 * 指數資料
 */
data class StockIndex(
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val time: String
)

/**
 * 股票快照資料
 */
data class StockSnapshot(
    val symbol: String,
    val type: String,
    val exchange: String,
    val market: String,
    val price: Double,
    val size: Long,
    val bid: Double,
    val ask: Double,
    val volume: Long,
    val isContinuous: Boolean,
    val time: Long,
    val serial: Long
)
