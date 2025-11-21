package com.example.douyinclone.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.douyinclone.R
import com.example.douyinclone.adapter.FeedAdapter
import com.example.douyinclone.adapter.FeedPagerAdapter
import com.example.douyinclone.adapter.TopTabAdapter
import com.example.douyinclone.databinding.FragmentFeedBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment() {
    
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: FeedViewModel by viewModels()
    
    private lateinit var feedPagerAdapter: FeedPagerAdapter
    
    private val tabTitles = listOf("购", "经验", "同城", "关注", "商城", "推荐")
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewPagerWithTabs()
        setupBottomNav()
        observeViewModel()
    }
    
    private fun setupViewPagerWithTabs() {
        // 创建ViewPager2适配器
        feedPagerAdapter = FeedPagerAdapter(this, tabTitles.size) { video, position, sharedView ->
            // 点击进入视频内流页面
            val bundle = bundleOf(
                "videoListJson" to com.google.gson.Gson().toJson(viewModel.videos.value),
                "startPosition" to position
            )
            
            val extras = FragmentNavigatorExtras(
                sharedView to "video_cover_transition"
            )
            
            findNavController().navigate(
                R.id.action_feed_to_video_detail,
                bundle,
                null,
                extras
            )
        }
        
        binding.viewPager.adapter = feedPagerAdapter
        binding.viewPager.offscreenPageLimit = 1
        
        // 设置TabLayout与ViewPager2联动
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
        
        // 默认选中"推荐"tab（索引5）
        binding.viewPager.setCurrentItem(5, false)
        
        // 监听页面变化
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.selectTopTab(position)
            }
        })
    }
    
    private fun setupBottomNav() {
        binding.apply {
            llTabHome.setOnClickListener {
                viewModel.selectBottomTab(0)
            }
            llTabFriend.setOnClickListener {
                viewModel.selectBottomTab(1)
            }
            ivTabAdd.setOnClickListener {
                // 点击加号按钮的处理
            }
            llTabMessage.setOnClickListener {
                viewModel.selectBottomTab(3)
            }
            llTabMe.setOnClickListener {
                viewModel.selectBottomTab(4)
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.selectedBottomTab.observe(viewLifecycleOwner) { position ->
            updateBottomTabUI(position)
        }
    }
    
    private fun updateBottomTabUI(selectedPosition: Int) {
        binding.apply {
            // 重置所有tab状态
            ivTabHome.setImageResource(if (selectedPosition == 0) R.drawable.ic_home_selected else R.drawable.ic_home)
            tvTabHome.setTextColor(resources.getColor(if (selectedPosition == 0) R.color.text_primary else R.color.text_secondary, null))
            
            ivTabFriend.setImageResource(if (selectedPosition == 1) R.drawable.ic_friend_selected else R.drawable.ic_friend)
            tvTabFriend.setTextColor(resources.getColor(if (selectedPosition == 1) R.color.text_primary else R.color.text_secondary, null))
            
            ivTabMessage.setImageResource(if (selectedPosition == 3) R.drawable.ic_message_selected else R.drawable.ic_message)
            tvTabMessage.setTextColor(resources.getColor(if (selectedPosition == 3) R.color.text_primary else R.color.text_secondary, null))
            
            ivTabMe.setImageResource(if (selectedPosition == 4) R.drawable.ic_me_selected else R.drawable.ic_me)
            tvTabMe.setTextColor(resources.getColor(if (selectedPosition == 4) R.color.text_primary else R.color.text_secondary, null))
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
