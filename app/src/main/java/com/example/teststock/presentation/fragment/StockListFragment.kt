package com.example.teststock.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teststock.databinding.FragmentStockListBinding
import com.example.teststock.presentation.adapter.StockAdapter
import com.example.teststock.presentation.base.BaseFragment
import com.example.teststock.presentation.base.Status
import com.example.teststock.presentation.viewmodel.StockViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * 股票列表 Fragment
 */
@AndroidEntryPoint
class StockListFragment : BaseFragment<FragmentStockListBinding>() {

    private val viewModel: StockViewModel by viewModels()
    private lateinit var stockAdapter: StockAdapter
    
    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentStockListBinding {
        return FragmentStockListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentStockListBinding, savedInstanceState: Bundle?) {
        super.onViewCreated(binding, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // 初始化 RecyclerView
        stockAdapter = StockAdapter(
            onItemClick = { _ ->
                // 點擊時不做任何事
            },
            onItemLongClick = { stock ->

            },
            onFavoriteClick = { stock ->
                // 切換最愛狀態
                viewModel.toggleFavorite(stock.symbol)
            }
        )
        binding.recyclerViewStocks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stockAdapter
        }

        binding.btnConnect.setOnClickListener {
            viewModel.reconnect()
        }


        binding.btnSendHeartbeat.setOnClickListener {
            viewModel.sendHeartbeat()
        }
    }

    private fun observeViewModel() {
        // 觀察連線狀態
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.connectionStatus.collect { status ->
                val statusText = when (status) {
                    com.example.teststock.data.repository.ConnectionStatus.CONNECTING -> "連線中..."
                    com.example.teststock.data.repository.ConnectionStatus.AUTHENTICATED -> "已認證"
                    com.example.teststock.data.repository.ConnectionStatus.SUBSCRIBED -> "已訂閱"
                    com.example.teststock.data.repository.ConnectionStatus.ERROR -> "連線錯誤"
                    com.example.teststock.data.repository.ConnectionStatus.DISCONNECTED -> "已斷線"
                }
                binding.tvConnectionStatus.text = "連線狀態: $statusText"
            }
        }

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

        // 觀察股票狀態
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stockStatus.collect { status ->
                when (status) {
                    is Status.Success -> {
                        stockAdapter.submitList(status.data)
                    }
                    is Status.Error -> {
                        showErrorDialog(status.message)
                    }
                    is Status.Empty -> {
                        stockAdapter.submitList(emptyList())
                    }
                    is Status.Loading -> {
                        // Loading 狀態由 isLoading 處理，這裡不需要處理
                    }

                    Status.Idle -> {

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 使用 setUserVisibleHint 來控制，這裡不處理
    }

    override fun onStop() {
        super.onStop()
        // 使用 setUserVisibleHint 來控制，這裡不處理
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isAdded) {
            if (isVisibleToUser) {
                // Fragment 對用戶可見時重新訂閱
                viewModel.reSubscribe()
            } else {
                // Fragment 對用戶不可見時取消訂閱
                viewModel.unsubscribe()
            }
        }
    }




}
