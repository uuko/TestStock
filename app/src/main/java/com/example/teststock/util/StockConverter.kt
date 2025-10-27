package com.example.teststock.util

import com.example.teststock.domain.model.Stock
import com.example.teststock.domain.model.StockAggregate
import com.example.teststock.domain.model.StockTrade
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 股票資料轉換工具類
 */
object StockConverter {
    
    /**
     * 將交易資料轉換為股票資料
     */
    fun convertTradeToStock(trade: StockTrade): Stock {
        return Stock(
            symbol = trade.symbol,
            name = trade.symbol, // 直接使用代號作為名稱
            price = trade.price,
            change = 0.0, // 交易資料沒有漲跌資訊
            changePercent = 0.0,
            volume = trade.volume,
            lastUpdateTime = trade.time,
        )
    }
    
    /**
     * 將快照資料轉換為股票資料
     */
    fun convertSnapshotToStock(snapshot: com.example.teststock.domain.model.StockSnapshot): Stock {
        return Stock(
            symbol = snapshot.symbol,
            name = snapshot.symbol, // 直接使用代號作為名稱
            price = snapshot.price,
            change = 0.0, // 快照資料沒有漲跌資訊
            changePercent = 0.0,
            volume = snapshot.size,
            lastUpdateTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(java.util.Date(snapshot.time / 1000000))
        )
    }
    
    /**
     * 將聚合資料轉換為股票資料
     */
    fun convertAggregateToStock(aggregate: StockAggregate): Stock {
        android.util.Log.d("StockConverter", "處理聚合資料: ${aggregate.symbol} - 價格: ${aggregate.price}")
        
        return Stock(
            symbol = aggregate.symbol,
            name = aggregate.name, // 直接使用代號作為名稱
            price = aggregate.price,
            change = aggregate.change, // 直接使用聚合資料的漲跌點數
            changePercent = aggregate.changePercent, // 直接使用聚合資料的漲跌幅
            volume = aggregate.volume,
            lastUpdateTime = aggregate.time
        )
    }
}
