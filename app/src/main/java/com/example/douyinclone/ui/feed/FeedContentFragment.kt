package com.example.douyinclone.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.douyinclone.R
import com.example.douyinclone.adapter.FeedAdapter
import com.example.douyinclone.data.model.VideoItem
import com.example.douyinclone.databinding.FragmentFeedContentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedContentFragment : Fragment() {
    
    private var _binding: FragmentFeedContentBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: FeedViewModel by activityViewModels()
    
    private lateinit var feedAdapter: FeedAdapter
    
    private var tabPosition: Int = 0
    
    companion object {
        private const val ARG_TAB_POSITION = "tab_position"
        
        private var onVideoClickListener: ((VideoItem, Int, View) -> Unit)? = null
        
        fun newInstance(position: Int, onVideoClick: (VideoItem, Int, View) -> Unit): FeedContentFragment {
            onVideoClickListener = onVideoClick
            return FeedContentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TAB_POSITION, position)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabPosition = arguments?.getInt(ARG_TAB_POSITION, 0) ?: 0
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedContentBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupFeedList()
        setupSwipeRefresh()
        observeViewModel()
    }
    
    private fun setupFeedList() {
        feedAdapter = FeedAdapter { video, position, sharedView ->
            onVideoClickListener?.invoke(video, position, sharedView)
        }
        
        // 使用StaggeredGridLayoutManager实现双列瀑布流
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        
        binding.rvFeed.apply {
            this.layoutManager = layoutManager
            adapter = feedAdapter
            
            // 添加滚动监听实现上拉加载
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    
                    if (dy > 0) {
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null)
                        val firstVisibleItem = if (firstVisibleItems.isNotEmpty()) {
                            firstVisibleItems.minOrNull() ?: 0
                        } else 0
                        
                        if (visibleItemCount + firstVisibleItem >= totalItemCount - 4) {
                            viewModel.loadMoreVideos()
                        }
                    }
                }
            })
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setColorSchemeResources(R.color.primary)
            setOnRefreshListener {
                viewModel.refreshVideos()
            }
        }
    }
    
    private fun observeViewModel() {
        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            feedAdapter.submitList(videos)
        }
        
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
