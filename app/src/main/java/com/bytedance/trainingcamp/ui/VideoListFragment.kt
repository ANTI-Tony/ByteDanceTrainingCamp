package com.bytedance.trainingcamp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bytedance.trainingcamp.R
import com.bytedance.trainingcamp.VideoDetailActivity
import com.bytedance.trainingcamp.adapter.VideoCardAdapter
import com.bytedance.trainingcamp.model.VideoItem
import com.bytedance.trainingcamp.utils.MockDataUtils
import com.bytedance.trainingcamp.utils.CustomStaggeredGridLayoutManager

class VideoListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private lateinit var adapter: VideoCardAdapter
    private val videoList = mutableListOf<VideoItem>()

    companion object {
        private const val ARG_TYPE = "type"
        const val TYPE_RECOMMEND = 0
        const val TYPE_FOLLOWING = 1

        fun newInstance(type: Int): VideoListFragment {
            val fragment = VideoListFragment()
            val args = Bundle()
            args.putInt(ARG_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        setupSwipeRefresh()
        setupRecyclerView()
        loadData()
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
            R.color.purple_500,
            R.color.purple_700,
            R.color.teal_200
        )
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = CustomStaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerView.layoutManager = layoutManager

        adapter = VideoCardAdapter(videoList) { videoItem, position ->
            // 点击卡片，跳转到视频详情页
            val intent = Intent(requireContext(), VideoDetailActivity::class.java)
            intent.putExtra("video_id", videoItem.id)
            intent.putExtra("video_url", videoItem.videoUrl)
            intent.putExtra("title", videoItem.title)
            intent.putExtra("author_name", videoItem.author.name)
            intent.putExtra("author_avatar", videoItem.author.avatarUrl)
            intent.putExtra("description", videoItem.description)
            intent.putExtra("like_count", videoItem.likeCount)
            intent.putExtra("comment_count", videoItem.commentCount)
            intent.putExtra("is_liked", videoItem.isLiked)
            intent.putExtra("is_following", videoItem.author.isFollowing)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // 添加滚动监听，实现下拉加载更多
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    loadMoreData()
                }
            }
        })
    }

    private fun loadData() {
        // 加载初始数据
        val mockData = MockDataUtils.getMockVideoList(20)
        adapter.updateData(mockData)
    }

    private fun loadMoreData() {
        // 加载更多数据
        val moreData = MockDataUtils.getMockVideoList(10)
        adapter.addData(moreData)
    }

    private fun refreshData() {
        // 刷新数据
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val newData = MockDataUtils.getMockVideoList(20)
            adapter.updateData(newData)
            swipeRefreshLayout.isRefreshing = false
        }, 1000)
    }
}