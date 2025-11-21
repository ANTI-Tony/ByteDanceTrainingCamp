package com.bytedance.trainingcamp.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 自定义瀑布流布局管理器
 * 修复滚动时item跳动的问题
 */
class CustomStaggeredGridLayoutManager(
    spanCount: Int,
    orientation: Int
) : StaggeredGridLayoutManager(spanCount, orientation) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}
