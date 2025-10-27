package com.example.teststock.domain.usecase

import com.example.teststock.domain.model.Stock
import com.example.teststock.domain.repository.StockRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class StockUseCaseTest {

    @Mock
    private lateinit var stockRepository: StockRepository

    private lateinit var stockUseCase: StockUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        stockUseCase = StockUseCase(stockRepository)
    }

    @Test
    fun `getAllStocks should return stocks from repository`() = runTest {
        // Given
        val expectedStocks = listOf(
            Stock(
                symbol = "2330",
                name = "台積電",
                price = 500.0,
                change = 10.0,
                changePercent = 2.0,
                volume = 1000L,
                lastUpdateTime = "2024-01-01"
            )
        )
        whenever(stockRepository.getAllStocks()).thenReturn(flowOf(expectedStocks))

        // When
        val result = stockUseCase.getAllStocks()

        // Then
        result.collect { stocks ->
            assert(stocks == expectedStocks)
        }
    }
}

