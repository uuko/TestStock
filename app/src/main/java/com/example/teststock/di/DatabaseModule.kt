package com.example.teststock.di

import android.content.Context
import com.example.teststock.data.local.LocalDataSource
import com.example.teststock.data.local.dao.StockDao
import com.example.teststock.data.local.database.StockDatabase
import com.example.teststock.data.local.datastore.UserPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 資料庫相關依賴注入模組
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    @Provides
    @Singleton
    fun provideStockDatabase(@ApplicationContext context: Context): StockDatabase {
        return StockDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideStockDao(database: StockDatabase): StockDao {
        return database.stockDao()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(dao: StockDao, gson: Gson): LocalDataSource {
        return LocalDataSource(dao, gson)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferences(dataStore)
    }
}
