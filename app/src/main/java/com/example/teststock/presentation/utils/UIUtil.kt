package com.example.teststock.presentation.utils

import android.content.Context
import com.example.teststock.R

/**
 * UI 工具類
 */
object UIUtil {
    
    /**
     * 取得漲跌顏色
     * @param value 漲跌值
     * @param context 上下文
     * @return 顏色資源 ID
     */
    fun getChangeColor(value: Double, context: Context): Int {
        return when {
            value > 0 -> context.getColor(R.color.red)  // 上漲用紅色
            value < 0 -> context.getColor(R.color.green) // 下跌用綠色
            else -> context.getColor(R.color.black)     // 平盤用黑色
        }
    }
    
    /**
     * 格式化價格
     * @param price 價格
     * @return 格式化後的價格字串
     */
    fun formatPrice(price: Double): String {
        return String.format("%.2f", price)
    }
    
    /**
     * 格式化漲跌
     * @param change 漲跌值
     * @return 格式化後的漲跌字串
     */
    fun formatChange(change: Double): String {
        val prefix = if (change >= 0) "+" else ""
        return "$prefix${String.format("%.2f", change)}"
    }
    
    /**
     * 格式化漲跌幅
     * @param changePercent 漲跌幅
     * @return 格式化後的漲跌幅字串
     */
    fun formatChangePercent(changePercent: Double): String {
        val prefix = if (changePercent >= 0) "+" else ""
        return "$prefix${String.format("%.2f", changePercent)}%"
    }
    
    /**
     * 格式化成交量
     * @param volume 成交量
     * @return 格式化後的成交量字串
     */
    fun formatVolume(volume: Long): String {
        return when {
            volume >= 1000000000 -> "${volume / 1000000000}億"
            volume >= 10000 -> "${volume / 10000}萬"
            else -> volume.toString()
        }
    }
    
    /**
     * 格式化大數字（用於顯示金額）
     * @param value 數值
     * @return 格式化後的字串
     */
    fun formatLargeNumber(value: Long): String {
        return when {
            value >= 1000000000 -> "${String.format("%.1f", value / 1000000000.0)}億"
            value >= 10000 -> "${String.format("%.1f", value / 10000.0)}萬"
            else -> String.format("%,d", value)
        }
    }
}
