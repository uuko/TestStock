package com.example.teststock.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teststock.R
import com.example.teststock.domain.model.Stock
import com.example.teststock.presentation.utils.UIUtil
import java.text.DecimalFormat

/**
 * 股票列表 Adapter
 */
class StockAdapter(
    private val onItemClick: (Stock) -> Unit = {},
    private val onItemLongClick: (Stock) -> Unit = {},
    private val onFavoriteClick: (Stock) -> Unit = {}
) : ListAdapter<Stock, StockAdapter.StockViewHolder>(StockDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onItemLongClick, onFavoriteClick)
    }

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSymbol: TextView = itemView.findViewById(R.id.tvSymbol)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvChange: TextView = itemView.findViewById(R.id.tvChange)
        private val tvChangePercent: TextView = itemView.findViewById(R.id.tvChangePercent)
        private val tvVolume: TextView = itemView.findViewById(R.id.tvVolume)
        private val tvLastUpdate: TextView = itemView.findViewById(R.id.tvLastUpdate)
        private val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)

        fun bind(stock: Stock, onItemClick: (Stock) -> Unit, onItemLongClick: (Stock) -> Unit, onFavoriteClick: (Stock) -> Unit) {
            tvSymbol.text = stock.symbol
            tvName.text = stock.name ?: stock.symbol
            tvPrice.text = UIUtil.formatPrice(stock.price)
            
            // 設定漲跌顏色
            val changeText = UIUtil.formatChange(stock.change)
            tvChange.text = changeText
            tvChange.setTextColor(UIUtil.getChangeColor(stock.change, itemView.context))
            
            val changePercentText = UIUtil.formatChangePercent(stock.changePercent)
            tvChangePercent.text = changePercentText
            tvChangePercent.setTextColor(UIUtil.getChangeColor(stock.changePercent, itemView.context))
            
            tvVolume.text = UIUtil.formatVolume(stock.volume)
            tvLastUpdate.text = stock.lastUpdateTime
            
            // 設定愛心圖示狀態
            updateFavoriteIcon(stock.isUserSelected)
            
            // 設定點擊事件
            itemView.setOnClickListener {
                onItemClick(stock)
            }
            
            // 設定長按事件
            itemView.setOnLongClickListener {
                onItemLongClick(stock)
                true
            }
            
            // 設定愛心點擊事件
            ivFavorite.setOnClickListener {
                onFavoriteClick(stock)
            }
        }
        
        /**
         * 更新愛心圖示狀態
         */
        private fun updateFavoriteIcon(isFavorite: Boolean) {
            if (isFavorite) {
                ivFavorite.setImageResource(R.drawable.ic_favorite)
                ivFavorite.setColorFilter(itemView.context.getColor(R.color.favorite_heart))
            } else {
                ivFavorite.setImageResource(R.drawable.ic_favorite_border)
                ivFavorite.setColorFilter(itemView.context.getColor(R.color.favorite_heart_unselected))
            }
        }

    }
}

/**
 * 股票資料 DiffCallback
 */
class StockDiffCallback : DiffUtil.ItemCallback<Stock>() {
    override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem.symbol == newItem.symbol
    }

    override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
        return oldItem == newItem
    }
}
