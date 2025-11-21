package com.example.douyinclone.adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.douyinclone.data.model.VideoItem
import com.example.douyinclone.ui.feed.FeedContentFragment

class FeedPagerAdapter(
    fragment: Fragment,
    private val tabCount: Int,
    private val onVideoClick: (VideoItem, Int, View) -> Unit
) : FragmentStateAdapter(fragment) {
    
    override fun getItemCount(): Int = tabCount
    
    override fun createFragment(position: Int): Fragment {
        return FeedContentFragment.newInstance(position, onVideoClick)
    }
}
