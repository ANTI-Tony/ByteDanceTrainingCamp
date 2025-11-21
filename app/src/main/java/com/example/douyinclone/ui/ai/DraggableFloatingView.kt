package com.example.douyinclone.ui.ai

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.math.abs

class DraggableFloatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    
    private var lastX = 0f
    private var lastY = 0f
    private var isDragging = false
    private var downX = 0f
    private var downY = 0f
    
    private val touchSlop = 10f
    
    var onClickListener: (() -> Unit)? = null
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                downX = event.rawX
                downY = event.rawY
                isDragging = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - lastX
                val deltaY = event.rawY - lastY
                
                // 判断是否为拖动
                if (!isDragging && (abs(event.rawX - downX) > touchSlop || abs(event.rawY - downY) > touchSlop)) {
                    isDragging = true
                }
                
                if (isDragging) {
                    val newX = x + deltaX
                    val newY = y + deltaY
                    
                    // 边界检查
                    val parent = parent as? ViewGroup
                    if (parent != null) {
                        val maxX = parent.width - width
                        val maxY = parent.height - height
                        
                        x = newX.coerceIn(0f, maxX.toFloat())
                        y = newY.coerceIn(0f, maxY.toFloat())
                    }
                    
                    lastX = event.rawX
                    lastY = event.rawY
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    // 这是点击事件
                    onClickListener?.invoke()
                    performClick()
                } else {
                    // 拖动结束，吸附到边缘
                    snapToEdge()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
    
    private fun snapToEdge() {
        val parent = parent as? ViewGroup ?: return
        val parentWidth = parent.width
        val centerX = x + width / 2
        
        val targetX = if (centerX < parentWidth / 2) {
            16f // 吸附到左边
        } else {
            (parentWidth - width - 16).toFloat() // 吸附到右边
        }
        
        animate()
            .x(targetX)
            .setDuration(200)
            .start()
    }
}
