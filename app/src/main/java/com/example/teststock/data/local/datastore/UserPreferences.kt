package com.example.teststock.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用戶偏好設定管理
 */
@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    companion object {
        private val SELECTED_STOCKS_KEY = stringSetPreferencesKey("selected_stocks")
    }
    
    /**
     * 取得用戶選擇的股票代碼列表
     */
    val selectedStocks: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[SELECTED_STOCKS_KEY] ?: getDefaultStocks()
    }
    
    /**
     * 取得預設股票列表
     */
    private fun getDefaultStocks(): Set<String> {
        return setOf()
    }
    
    /**
     * 更新選擇的股票代碼列表
     */
    suspend fun updateSelectedStocks(stocks: Set<String>) {
        dataStore.edit { preferences ->
            preferences[SELECTED_STOCKS_KEY] = stocks
        }
    }
    
    /**
     * 添加股票到選擇列表
     */
    suspend fun addStock(symbol: String): Boolean {
        var added = false
        dataStore.edit { preferences ->
            val currentStocks = preferences[SELECTED_STOCKS_KEY] ?: emptySet()
            if (currentStocks.size < 10 && !currentStocks.contains(symbol)) { // 限制最多10隻股票且不重複
                preferences[SELECTED_STOCKS_KEY] = currentStocks + symbol
                added = true
            }
        }
        return added
    }
    
    /**
     * 從選擇列表移除股票
     */
    suspend fun removeStock(symbol: String) {
        dataStore.edit { preferences ->
            val currentStocks = preferences[SELECTED_STOCKS_KEY] ?: emptySet()
            preferences[SELECTED_STOCKS_KEY] = currentStocks - symbol
        }
    }
    
    /**
     * 清空選擇的股票列表
     */
    suspend fun clearSelectedStocks() {
        dataStore.edit { preferences ->
            preferences.remove(SELECTED_STOCKS_KEY)
        }
    }
    
    /**
     * 初始化預設股票（如果還沒有設定的話）
     */
    suspend fun initializeDefaultStocks() {
        dataStore.edit { preferences ->
            if (preferences[SELECTED_STOCKS_KEY] == null) {
                preferences[SELECTED_STOCKS_KEY] = getDefaultStocks()
            }
        }
    }
}
