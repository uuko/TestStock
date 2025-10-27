package com.example.teststock.domain.usecase

import com.example.teststock.data.local.datastore.UserPreferences
import com.example.teststock.data.local.LocalDataSource
import com.example.teststock.presentation.base.Status
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 切換最愛狀態的 UseCase
 */
@Singleton
class ToggleFavoriteUseCase @Inject constructor(
    private val userPreferences: UserPreferences,
    private val localDataSource: LocalDataSource
) {
    
    private val _operationStatus = MutableSharedFlow<Status<String>>()
    val operationStatus: SharedFlow<Status<String>> = _operationStatus.asSharedFlow()
    
    /**
     * 切換最愛狀態
     */
    suspend fun toggleFavorite(symbol: String) {
        try {
            val selectedSymbols = userPreferences.selectedStocks.first()
            if (selectedSymbols.contains(symbol)) {
                // 如果已收藏，則移除
                userPreferences.removeStock(symbol)
                // 更新資料庫中的 isUserSelected 欄位
                localDataSource.unmarkAsUserSelected(symbol)
                _operationStatus.emit(Status.Success("已移除 $symbol 從最愛"))
            } else {
                // 如果未收藏，則加入
                userPreferences.addStock(symbol)
                // 更新資料庫中的 isUserSelected 欄位
                localDataSource.markAsUserSelected(symbol)
                _operationStatus.emit(Status.Success("已加入 $symbol 到最愛"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ToggleFavoriteUseCase", "切換最愛狀態失敗: ${e.message}")
            _operationStatus.emit(Status.Error("切換最愛狀態失敗: ${e.message}", e))
        }
    }
}
