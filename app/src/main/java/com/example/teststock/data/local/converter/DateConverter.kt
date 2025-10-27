package com.example.teststock.data.local.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * 日期轉換器
 */
class DateConverter {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

