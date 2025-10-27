package com.example.teststock.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.teststock.databinding.FragmentUserStocksBinding
import com.example.teststock.domain.model.Stock
import com.example.teststock.presentation.adapter.StockAdapter
import com.example.teststock.presentation.base.BaseFragment
import com.example.teststock.presentation.base.Status
import com.example.teststock.presentation.viewmodel.UserStocksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * 用戶選擇的股票頁面
 */
@AndroidEntryPoint
class UserStocksFragment : BaseFragment<FragmentUserStocksBinding>() {
    
    private val viewModel: UserStocksViewModel by viewModels()
    private lateinit var stockAdapter: StockAdapter
    
    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserStocksBinding {
        return FragmentUserStocksBinding.inflate(inflater, container, false)
    }
    
    override fun onViewCreated(binding: FragmentUserStocksBinding, savedInstanceState: Bundle?) {
        super.onViewCreated(binding, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        stockAdapter = StockAdapter(
            onItemClick = { stock ->
                navigateToStockDetail(stock.symbol)
            },
            onFavoriteClick = { stock ->
                // 切換最愛狀態
                viewModel.toggleFavorite(stock.symbol)
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stockAdapter
        }
        
        // 設定滑動刪除功能
        setupSwipeToDelete()
    }
    
    /**
     * 設定滑動刪除功能
     */
    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                target: androidx.recyclerview.widget.RecyclerView.ViewHolder
            ): Boolean {
                return false // 不支援拖拽移動
            }
            
            override fun onSwiped(
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.adapterPosition
                val stock = stockAdapter.currentList[position]
                
                // 從用戶選擇列表中移除
                viewModel.removeStock(stock.symbol)
                
                Toast.makeText(
                    context,
                    "已移除 ${stock.symbol}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }
    
    private fun observeViewModel() {
        // 觀察 loading 狀態
        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.isLoading.collect { isLoading ->
//                if (isLoading) {
//                    showLoading()
//                } else {
//                    hideLoading()
//                }
//            }
        }
        
        // 觀察導航狀態
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigationStatus.collect { status ->
                when (status) {
                    is Status.Loading -> {
                        // 導航載入中，可以顯示載入指示器
                    }
                    is Status.Success -> {
                        status.data?.let { stock ->

                        }
                    }
                    is Status.Error -> {
                        Toast.makeText(context, "導航失敗: ${status.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // 其他狀態不需要處理
                    }
                }
            }
        }
        
        // 觀察操作狀態
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.operationStatus.collect { status ->
                when (status) {
                    is Status.Success -> {
                        Toast.makeText(context, status.data ?: "操作成功", Toast.LENGTH_SHORT).show()
                    }
                    is Status.Error -> {
                        Toast.makeText(context, "操作失敗: ${status.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // 其他狀態不需要處理
                    }
                }
            }
        }
        
        // 觀察股票載入狀態
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stockLoadingStatus.collect { status ->
                when (status) {
                    is Status.Loading -> {
                        // 載入中狀態由 isLoading 處理
                    }
                    is Status.Success -> {
                        val stocks = status.data ?: emptyList()
                        stockAdapter.submitList(stocks)
                        
                        if (stocks.isEmpty()) {
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            binding.tvEmptyMessage.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                    }
                    is Status.Error -> {
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.tvEmptyMessage.text = "載入失敗: ${status.message}"
                    }
                    is Status.Empty -> {
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.tvEmptyMessage.text = "暫無股票資料"
                    }
                    is Status.Idle -> {
                        // 初始狀態
                    }
                }
            }
        }
        
    }
    
    /**
     * 導航到股票詳細頁面
     */
    private fun navigateToStockDetail(symbol: String) {
        viewModel.navigateToStockDetail(symbol)
    }

}
