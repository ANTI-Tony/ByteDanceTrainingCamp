package com.bytedance.trainingcamp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bytedance.trainingcamp.ui.VideoListFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> VideoListFragment.newInstance(VideoListFragment.TYPE_RECOMMEND)
            1 -> VideoListFragment.newInstance(VideoListFragment.TYPE_FOLLOWING)
            else -> VideoListFragment.newInstance(VideoListFragment.TYPE_RECOMMEND)
        }
    }
}
