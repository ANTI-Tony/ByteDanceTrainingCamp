package com.example.douyinclone

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.douyinclone.databinding.ActivityMainBinding
import com.example.douyinclone.ui.ai.AiChatDialogFragment
import com.example.douyinclone.ui.ai.DraggableFloatingView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var floatingBall: DraggableFloatingView? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        setupFloatingBall()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
    
    private fun setupFloatingBall() {
        // 创建悬浮球
        floatingBall = DraggableFloatingView(this).apply {
            val size = resources.getDimensionPixelSize(R.dimen.floating_ball_size)
            layoutParams = FrameLayout.LayoutParams(size, size).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                marginEnd = 20
                bottomMargin = 200
            }
            
            background = resources.getDrawable(R.drawable.bg_floating_ball, null)
            elevation = 8f
            
            // 添加AI图标
            val iconView = ImageView(context).apply {
                setImageResource(R.drawable.ic_ai)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                val padding = 14
                setPadding(padding, padding, padding, padding)
            }
            addView(iconView)
            
            // 点击事件
            onClickListener = {
                showAiChatDialog()
            }
        }
        
        // 添加到根布局
        binding.root.addView(floatingBall)
    }
    
    private fun showAiChatDialog() {
        AiChatDialogFragment.newInstance()
            .show(supportFragmentManager, AiChatDialogFragment.TAG)
    }
}
