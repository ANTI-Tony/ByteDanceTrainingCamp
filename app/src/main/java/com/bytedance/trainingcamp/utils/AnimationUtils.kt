package com.bytedance.trainingcamp.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.OvershootInterpolator

object AnimationUtils {

    /**
     * 点赞动画
     */
    fun likeAnimation(view: View, isLiked: Boolean, onEnd: (() -> Unit)? = null) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.duration = 300
        animatorSet.interpolator = OvershootInterpolator()
        animatorSet.playTogether(scaleX, scaleY)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onEnd?.invoke()
            }
        })

        animatorSet.start()
    }

    /**
     * 点击缩放动画
     */
    fun clickScaleAnimation(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.duration = 150
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()
    }

    /**
     * 淡入动画
     */
    fun fadeIn(view: View, duration: Long = 300) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()
    }

    /**
     * 淡出动画
     */
    fun fadeOut(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
                onEnd?.invoke()
            }
            .start()
    }

    /**
     * 从底部滑入
     */
    fun slideInFromBottom(view: View, duration: Long = 300) {
        view.translationY = view.height.toFloat()
        view.visibility = View.VISIBLE
        view.animate()
            .translationY(0f)
            .setDuration(duration)
            .start()
    }

    /**
     * 滑出到底部
     */
    fun slideOutToBottom(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        view.animate()
            .translationY(view.height.toFloat())
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
                onEnd?.invoke()
            }
            .start()
    }
}
