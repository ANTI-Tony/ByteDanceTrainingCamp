package com.bytedance.trainingcamp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bytedance.trainingcamp.adapter.ViewPagerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabRecommend: TextView
    private lateinit var tabFollowing: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupViewPager()
        setupTabs()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        tabRecommend = findViewById(R.id.tabRecommend)
        tabFollowing = findViewById(R.id.tabFollowing)
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // 监听ViewPager页面切换
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTabSelection(position)
            }
        })
    }

    private fun setupTabs() {
        tabRecommend.setOnClickListener {
            viewPager.currentItem = 0
        }

        tabFollowing.setOnClickListener {
            viewPager.currentItem = 1
        }
    }

    private fun updateTabSelection(position: Int) {
        when (position) {
            0 -> {
                tabRecommend.apply {
                    setTextColor(getColor(R.color.black))
                    textSize = 16f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                tabFollowing.apply {
                    setTextColor(getColor(R.color.gray))
                    textSize = 16f
                    typeface = android.graphics.Typeface.DEFAULT
                }
            }
            1 -> {
                tabRecommend.apply {
                    setTextColor(getColor(R.color.gray))
                    textSize = 16f
                    typeface = android.graphics.Typeface.DEFAULT
                }
                tabFollowing.apply {
                    setTextColor(getColor(R.color.black))
                    textSize = 16f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            }
        }
    }
}
