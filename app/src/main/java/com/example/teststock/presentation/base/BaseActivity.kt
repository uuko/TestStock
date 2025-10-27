package com.example.teststock.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 基礎 Activity
 * 提供通用的 loading 和 dialog 功能
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    
    private var _binding: VB? = null
    protected val binding get() = _binding!!
    
    private var loadingDialog: AlertDialog? = null
    private var isShowingLoading = false
    
    /**
     * 創建 binding
     */
    protected abstract fun createBinding(inflater: LayoutInflater): VB
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = createBinding(layoutInflater)
        setContentView(binding.root)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        hideLoading()
    }
    
    /**
     * 顯示 loading dialog
     */
    protected fun showLoading(message: String = "載入中...") {
        if (!isShowingLoading) {
            isShowingLoading = true
            loadingDialog = MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .setCancelable(false)
                .show()
        }
    }
    
    /**
     * 隱藏 loading dialog
     */
    protected fun hideLoading() {
        if (isShowingLoading) {
            isShowingLoading = false
            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }
    
    /**
     * 顯示 Alert Dialog
     */
    protected fun showAlertDialog(
        title: String,
        message: String,
        positiveButtonText: String = "確定",
        onPositiveClick: (() -> Unit)? = null,
        negativeButtonText: String? = null,
        onNegativeClick: (() -> Unit)? = null
    ) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ ->
                onPositiveClick?.invoke()
            }
            .apply {
                if (negativeButtonText != null) {
                    setNegativeButton(negativeButtonText) { _, _ ->
                        onNegativeClick?.invoke()
                    }
                }
            }
            .show()
    }
    
    /**
     * 顯示錯誤 Dialog（簡化版本）
     */
    protected fun showErrorDialog(message: String) {
        showAlertDialog(
            title = "錯誤",
            message = message,
            positiveButtonText = "確定"
        )
    }
}
