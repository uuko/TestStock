package com.example.teststock.data.local

import android.util.Log
import com.example.teststock.data.local.dao.StockDao
import com.example.teststock.data.local.entity.StockEntity
import com.example.teststock.domain.model.Stock
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 本地資料來源
 */
@Singleton
class LocalDataSource @Inject constructor(
    private val stockDao: StockDao,
    private val gson: Gson
) {
    
    /**
     * 取得所有股票資料
     */
    fun getAllStocks(): Flow<List<Stock>> {
        return stockDao.getAllStocks().map { entities ->
            entities.map { entity -> 
                entity.toDomainModel(gson)
            }
        }
    }
    
    /**
     * 取得用戶選擇的股票
     */
    fun getUserSelectedStocks(): Flow<List<Stock>> {
        return stockDao.getUserSelectedStocks().map { entities ->
            entities.map { entity -> 
                entity.toDomainModel(gson)
            }
        }
    }
    
    /**
     * 取得預設股票
     */
    fun getDefaultStocks(): Flow<List<Stock>> {
        return stockDao.getDefaultStocks().map { entities ->
            entities.map { entity -> 
                entity.toDomainModel(gson)
            }
        }
    }
    
    /**
     * 根據代號取得股票
     */
    suspend fun getStockBySymbol(symbol: String): Stock? {
        return stockDao.getStockBySymbol(symbol)?.toDomainModel(gson)
    }
    
    /**
     * 儲存股票資料
     */
    suspend fun saveStock(stock: Stock, isUserSelected: Boolean = false, isDefault: Boolean = false) {
        val entity = StockEntity(
            symbol = stock.symbol,
            data = gson.toJson(stock),
            isUserSelected = isUserSelected,
            isDefault = isDefault
        )
        stockDao.insertStock(entity)
    }
    
    /**
     * 更新股票資料（保持 isUserSelected 和 isDefault 狀態）
     */
    suspend fun updateStockData(stock: Stock) {
        // 先取得現有的股票記錄
        val existingEntity = stockDao.getStockBySymbol(stock.symbol)
        if (existingEntity != null) {
            // 保持現有的 isUserSelected 和 isDefault 狀態
            val updatedEntity = existingEntity.copy(
                data = gson.toJson(stock),
                updatedAt = java.util.Date()
            )
            stockDao.updateStock(updatedEntity)
        } else {
            // 如果不存在，則創建新記錄
            saveStock(stock, isUserSelected = false, isDefault = false)
        }
    }
    
    /**
     * 標記為用戶選擇的股票
     */
    suspend fun markAsUserSelected(symbol: String) {
        stockDao.markAsUserSelected(symbol)
    }
    
    /**
     * 取消標記為用戶選擇的股票
     */
    suspend fun unmarkAsUserSelected(symbol: String) {
        stockDao.unmarkAsUserSelected(symbol)
    }
    
    /**
     * 刪除股票
     */
    suspend fun deleteStockBySymbol(symbol: String) {
        stockDao.deleteStockBySymbol(symbol)
    }
    
    /**
     * 清空所有股票資料
     */
    suspend fun clearAllStocks() {
        stockDao.deleteAllStocks()
    }
    

}

/**
 * 將 StockEntity 轉換為 Stock 領域模型
 */
private fun StockEntity.toDomainModel(gson: Gson): Stock {
    return try {
        val stock = gson.fromJson(data, Stock::class.java)
        // 使用 StockEntity 中的 isUserSelected 欄位覆蓋 JSON 中的值
        stock.copy(isUserSelected = this.isUserSelected)
    } catch (e: Exception) {
        // 記錄錯誤並重新拋出異常，讓上層處理
        Log.e("LocalDataSource", "Failed to parse stock data for symbol: $symbol", e)
        throw RuntimeException("Failed to parse stock data for symbol: $symbol", e)
    }
}






