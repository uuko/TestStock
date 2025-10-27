package com.example.teststock.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 股票資料實體 - 統一使用 JSON 格式存儲
 */
@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey
    val symbol: String,
    val data: String, // JSON 格式的股票數據
    val isUserSelected: Boolean = false, // 是否為用戶選擇的股票
    val isDefault: Boolean = false, // 是否為預設股票
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

