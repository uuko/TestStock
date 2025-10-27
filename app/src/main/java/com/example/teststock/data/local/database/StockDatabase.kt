package com.example.teststock.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.teststock.data.local.dao.StockDao
import com.example.teststock.data.local.entity.StockEntity
import com.example.teststock.data.local.converter.DateConverter

/**
 * 股票資料庫
 */
@Database(
    entities = [
        StockEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class StockDatabase : RoomDatabase() {
    
    abstract fun stockDao(): StockDao
    
    companion object {
        @Volatile
        private var INSTANCE: StockDatabase? = null
        
        // Migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 刪除不需要的表格
                database.execSQL("DROP TABLE IF EXISTS stock_trades")
                database.execSQL("DROP TABLE IF EXISTS stock_aggregates")
                
                // 重新創建 stocks 表格以匹配新的結構
                database.execSQL("DROP TABLE IF EXISTS stocks")
                database.execSQL("""
                    CREATE TABLE stocks (
                        symbol TEXT NOT NULL PRIMARY KEY,
                        data TEXT NOT NULL,
                        isUserSelected INTEGER NOT NULL DEFAULT 0,
                        isDefault INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """)
            }
        }
        
        fun getDatabase(context: Context): StockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockDatabase::class.java,
                    "stock_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // 如果 migration 失敗，重建資料庫
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

