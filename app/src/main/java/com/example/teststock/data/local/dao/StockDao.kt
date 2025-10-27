package com.example.teststock.data.local.dao

import androidx.room.*
import com.example.teststock.data.local.entity.StockEntity
import kotlinx.coroutines.flow.Flow

/**
 * 股票資料存取物件
 */
@Dao
interface StockDao {
    
    // Stock 相關操作
    @Query("SELECT * FROM stocks ORDER BY symbol ASC")
    fun getAllStocks(): Flow<List<StockEntity>>
    
    @Query("SELECT * FROM stocks WHERE symbol = :symbol")
    suspend fun getStockBySymbol(symbol: String): StockEntity?
    
    @Query("SELECT * FROM stocks WHERE isUserSelected = 1 ORDER BY symbol ASC")
    fun getUserSelectedStocks(): Flow<List<StockEntity>>
    
    @Query("SELECT * FROM stocks WHERE isDefault = 1 ORDER BY symbol ASC")
    fun getDefaultStocks(): Flow<List<StockEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(stocks: List<StockEntity>)
    
    @Update
    suspend fun updateStock(stock: StockEntity)
    
    @Delete
    suspend fun deleteStock(stock: StockEntity)
    
    @Query("DELETE FROM stocks WHERE symbol = :symbol")
    suspend fun deleteStockBySymbol(symbol: String)
    
    @Query("DELETE FROM stocks")
    suspend fun deleteAllStocks()
    
    @Query("UPDATE stocks SET isUserSelected = 1 WHERE symbol = :symbol")
    suspend fun markAsUserSelected(symbol: String)
    
    @Query("UPDATE stocks SET isUserSelected = 0 WHERE symbol = :symbol")
    suspend fun unmarkAsUserSelected(symbol: String)
}

