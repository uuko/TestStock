package com.example.teststock.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.teststock.databinding.ActivityMainBinding
import com.example.teststock.presentation.fragment.StockListFragment
import com.example.teststock.presentation.fragment.UserStocksFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主 Activity，包含 ViewPager 和 TabLayout
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        setupTabs()
    }
    
    private fun setupViewPager() {
        binding.viewPager.adapter = StockPagerAdapter(this)
        // 設定 offscreenPageLimit 為 1，保持相鄰 Fragment 在記憶體中
        // 這樣切換 tab 時不會觸發 onStop/onResume
        binding.viewPager.offscreenPageLimit = 1
        
        // 監聽頁面切換，手動管理 Fragment 的可見性
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }
    
    private fun setupTabs() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "股票列表"
                1 -> "我的股票"
                else -> ""
            }
        }.attach()
    }
    
    /**
     * ViewPager 適配器
     */
    private class StockPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        
        override fun getItemCount(): Int = 2
        
        override fun createFragment(position: Int) = when (position) {
            0 -> StockListFragment()
            1 -> UserStocksFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
