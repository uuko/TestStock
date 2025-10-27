package com.example.teststock.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator

/**
 * 基礎 Fragment
 * 提供通用的 loading 和 dialog 功能
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    
    private var _binding: VB? = null
    protected val binding get() = _binding!!
    
    private var loadingDialog: AlertDialog? = null
    private var isShowingLoading = false
    
    /**
     * 創建 binding
     */
    protected abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = createBinding(inflater, container)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(binding, savedInstanceState)
    }
    
    /**
     * 子類實現此方法來處理 view 創建後的邏輯
     */
    protected open fun onViewCreated(binding: VB, savedInstanceState: Bundle?) {
        // 子類可以重寫此方法
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        hideLoading()
    }
    
    /**
     * 顯示 loading dialog
     */
    protected fun showLoading(message: String = "載入中...") {
            isShowingLoading = true
            loadingDialog = MaterialAlertDialogBuilder(requireContext())
                .setMessage(message)
                .setCancelable(false)
                .show()

    }
    
    /**
     * 隱藏 loading dialog
     */
    protected fun hideLoading() {
            loadingDialog?.dismiss()
            loadingDialog = null

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
        MaterialAlertDialogBuilder(requireContext())
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
